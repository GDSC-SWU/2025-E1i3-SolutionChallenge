from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.responses import JSONResponse
from ultralytics import YOLO
import requests
import uvicorn
import cv2
import numpy as np
import re
import google.generativeai as genai
import os

# --- 설정값 ---
GCS_MODEL_URL = os.getenv("GCS_MODEL_URL", "")
YOLO_MODEL_PATH = "best.pt"
USDA_API_KEY = os.getenv("USDA_API_KEY", "")
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "")

# --- YOLO 모델 다운로드 ---
def download_model():
    if not os.path.exists(YOLO_MODEL_PATH):
        if not GCS_MODEL_URL:
            raise RuntimeError("GCS_MODEL_URL is not set.")
        print(f"📥 Downloading YOLO model from {GCS_MODEL_URL}...")
        response = requests.get(GCS_MODEL_URL)
        if response.status_code == 200:
            with open(YOLO_MODEL_PATH, "wb") as f:
                f.write(response.content)
            print("✅ Model downloaded.")
        else:
            raise RuntimeError(f"Download failed: HTTP {response.status_code}")
    else:
        print("🔄 YOLO model already exists.")

# --- 모델 초기화 ---
try:
    download_model()
    if not os.path.exists(YOLO_MODEL_PATH):
        raise FileNotFoundError("❌ YOLO model not found after download.")
    print("🚀 Initializing YOLO model...")
    yolo_model = YOLO(YOLO_MODEL_PATH)
    print("✅ YOLO model loaded.")
except Exception as e:
    print(f"🔥 Failed to initialize YOLO model: {e}")
    raise

genai.configure(api_key=GEMINI_API_KEY)
gemini_model = genai.GenerativeModel("gemini-1.5-flash")

app = FastAPI()

# --- 헬스 체크 ---
@app.get("/")
def health_check():
    return {"status": "ok"}

# --- 음식 이름 보정 ---
def refine_food_name_with_gemini(original_name: str) -> str:
    prompt = f"""
    '{original_name}'은/는 이미지 인식 모델이 예측한 음식 이름입니다.
    미국 USDA 식품영양 데이터베이스에서 검색 가능한 표준 영어 식품명으로 바꿔주세요.
    단어만 반환하세요.
    """
    try:
        response = gemini_model.generate_content(prompt)
        return response.text.strip().lower()
    except Exception:
        return original_name

# --- 당류 추정 ---
def estimate_sugar_with_gemini(food_name: str) -> (str, float):
    prompt = f"""
'{food_name}'은/는 일반적으로 어떤 재료로 조리되며, 1인분 또는 100g 기준으로 당류(sugar)가 얼마나 포함되어 있는지 추정해 주세요.

조건:
- 조리 방식과 주요 재료를 고려하세요.
- 단위는 반드시 **g(그램)**으로 표현하세요.
- 예: 2g, 3.5g, 0~1g 등
- 문장은 간결하게, 결과는 반드시 **수치 + 단위**로 끝나도록 해주세요.
    """
    try:
        response = gemini_model.generate_content(prompt)
        text = response.text.strip()
        numbers = re.findall(r"([0-9]+(?:\.[0-9]+)?)", text)
        valid_numbers = [float(n) for n in numbers if 0 <= float(n) <= 50]
        estimated_value = max(valid_numbers) if valid_numbers else 0.0
        return f"(Gemini 추정: {text})", estimated_value
    except Exception as e:
        return f"(Gemini 추정 실패: {str(e)})", 0.0

# --- USDA API ---
def get_fdc_id(food_name, api_key):
    url = f"https://api.nal.usda.gov/fdc/v1/foods/search?query={food_name}&api_key={api_key}"
    res = requests.get(url)
    if res.status_code == 200:
        foods = res.json().get("foods", [])
        if foods:
            return foods[0].get("fdcId")
    return None

def get_sugar_from_fdc_id(fdc_id, api_key):
    url = f"https://api.nal.usda.gov/fdc/v1/food/{fdc_id}?api_key={api_key}"
    res = requests.get(url)
    if res.status_code == 200:
        for nutrient in res.json().get("foodNutrients", []):
            name = nutrient.get("nutrient", {}).get("name", "").lower()
            if "sugar" in name:
                return nutrient.get("amount", 0.0)
    return 0.0

def classify_risk_level(total_sugar):
    if total_sugar < 25:
        return "Less"
    elif total_sugar < 50:
        return "Proper"
    else:
        return "Excess"

def analyze_sugar_from_yolo(image_bytes: bytes):
    npimg = np.frombuffer(image_bytes, np.uint8)
    img = cv2.imdecode(npimg, cv2.IMREAD_COLOR)
    if img is None:
        raise ValueError("cv2.imdecode returned None")

    results = yolo_model.predict(img)
    boxes = results[0].boxes
    detected_classes = list(set([results[0].names[int(cls)] for cls in boxes.cls]))

    food_sugar_data = {}
    refined_names_map = {}
    total_sugar = 0.0

    for food in detected_classes:
        refined_name = refine_food_name_with_gemini(food)
        refined_names_map[food] = refined_name
        fdc_id = get_fdc_id(refined_name, USDA_API_KEY)
        if fdc_id:
            sugar = get_sugar_from_fdc_id(fdc_id, USDA_API_KEY)
            if sugar == 0.0:
                estimate_msg, estimated_value = estimate_sugar_with_gemini(refined_name)
                food_sugar_data[food] = f"(USDA: 0g, {estimate_msg})"
                total_sugar += estimated_value
            else:
                food_sugar_data[food] = round(sugar, 2)
                total_sugar += sugar
        else:
            estimate_msg, estimated_value = estimate_sugar_with_gemini(refined_name)
            food_sugar_data[food] = f"(USDA 없음, {estimate_msg})"
            total_sugar += estimated_value

    return {
        "detected_classes": detected_classes,
        "refined_names": refined_names_map,
        "food_sugar_data": food_sugar_data,
        "total_sugar": round(total_sugar, 2),
        "risk_level": classify_risk_level(total_sugar)
    }

@app.post("/analyze-day")
async def analyze_day(
    morning: UploadFile = File(...),
    lunch: UploadFile = File(...),
    dinner: UploadFile = File(...),
    snack: UploadFile = File(...)
):
    try:
        results = {}
        total = 0.0

        for name, file in {
            "morning": morning,
            "lunch": lunch,
            "dinner": dinner,
            "snack": snack
        }.items():
            image_bytes = await file.read()
            result = analyze_sugar_from_yolo(image_bytes)
            results[name] = result
            total += result["total_sugar"]

        return {
            "meals": results,
            "daily_total_sugar": round(total, 2),
            "daily_risk_level": classify_risk_level(total)
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"하루 식단 분석 실패: {str(e)}")
