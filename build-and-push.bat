@echo off
REM Script để build và push Docker image lên Docker Hub (Windows)
REM Sử dụng: build-and-push.bat <version>
REM Ví dụ: build-and-push.bat 1.0.0

if "%1"=="" (
    echo ❌ Vui lòng cung cấp version!
    echo Sử dụng: build-and-push.bat ^<version^>
    echo Ví dụ: build-and-push.bat 1.0.0
    exit /b 1
)

set VERSION=%1
set IMAGE_NAME=toobidu/quizizz-backend

echo 🚀 Bắt đầu build Docker image...
echo 📦 Image: %IMAGE_NAME%:%VERSION%
echo.

REM Build image
echo 🔨 Building image...
docker build -t %IMAGE_NAME%:%VERSION% .
docker tag %IMAGE_NAME%:%VERSION% %IMAGE_NAME%:latest

echo ✅ Build thành công!
echo.

REM Push image
echo 📤 Pushing image to Docker Hub...
docker push %IMAGE_NAME%:%VERSION%
docker push %IMAGE_NAME%:latest

echo.
echo ✅ Push thành công!
echo 🎉 Image đã được push lên Docker Hub:
echo    - %IMAGE_NAME%:%VERSION%
echo    - %IMAGE_NAME%:latest
