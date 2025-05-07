FROM python:3.11-slim

# 시스템 패키지 설치
RUN apt-get update && apt-get install -y \
    build-essential \
    libglib2.0-0 libsm6 libxext6 libxrender-dev \
    libgl1-mesa-glx \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# 작업 디렉토리
WORKDIR /app

# requirements 설치 먼저 캐시 활용
COPY requirements.txt .
RUN pip install --upgrade pip && pip install --no-cache-dir -r requirements.txt

# 나머지 복사
COPY . .

# 실행
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8080"]
