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

# --- Configuration ---
YOLO_MODEL_PATH = "best.pt"
USDA_API_KEY = os.getenv("USDA_API_KEY", "")
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "")

# --- Model Initialization ---
yolo_model = YOLO(YOLO_MODEL_PATH)
genai.configure(api_key=GEMINI_API_KEY)
gemini_model = genai.GenerativeModel("gemini-1.5-flash")

app = FastAPI()

# --- Refine food name using Gemini ---
def refine_food_name_with_gemini(original_name: str) -> str:
    prompt = f"""
    '{original_name}' is the predicted food name from the image recognition model.
    Please convert it to a standard food name in English that can be searched in the USDA FoodData Central database.
    Return only the word.
    """
    try:
        response = gemini_model.generate_content(prompt)
        return response.text.strip().lower()
    except Exception:
        return original_name

# --- Estimate sugar using Gemini ---
def estimate_sugar_with_gemini(food_name: str) -> (str, float):
    prompt = f"""
Estimate the amount of sugar (in grams) typically found in 1 serving or per 100g of '{food_name}' based on common ingredients and cooking methods.

Instructions:
- Consider preparation method and key ingredients.
- Use 'g' as the unit (e.g., 2g, 3.5g, 0~1g).
- Keep it concise and end the response with a numeric value + unit.
    """
    try:
        response = gemini_model.generate_content(prompt)
        text = response.text.strip()
        numbers = re.findall(r"([0-9]+(?:\.[0-9]+)?)", text)
        valid_numbers = [float(n) for n in numbers if 0 <= float(n) <= 50]
        estimated_value = max(valid_numbers) if valid_numbers else 0.0
        return f"(Gemini estimation: {text})", estimated_value
    except Exception as e:
        return f"(Gemini estimation failed: {str(e)})", 0.0

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
            food_sugar_data[food] = f"(No USDA match, {estimate_msg})"
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
        raise HTTPException(status_code=500, detail=f"Daily diet analysis failed: {str(e)}")

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8080, reload=True)
