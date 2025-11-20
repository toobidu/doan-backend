#!/bin/bash

# Script để build và push Docker image lên Docker Hub
# Sử dụng: ./build-and-push.sh <version>
# Ví dụ: ./build-and-push.sh 1.0.0

set -e

# Kiểm tra tham số version
if [ -z "$1" ]; then
    echo "❌ Vui lòng cung cấp version!"
    echo "Sử dụng: ./build-and-push.sh <version>"
    echo "Ví dụ: ./build-and-push.sh 1.0.0"
    exit 1
fi

VERSION=$1
IMAGE_NAME="toobidu/quizizz-backend"

echo "🚀 Bắt đầu build Docker image..."
echo "📦 Image: ${IMAGE_NAME}:${VERSION}"
echo ""

# Build image
echo "🔨 Building image..."
docker build -t ${IMAGE_NAME}:${VERSION} .
docker tag ${IMAGE_NAME}:${VERSION} ${IMAGE_NAME}:latest

echo "✅ Build thành công!"
echo ""

# Push image
echo "📤 Pushing image to Docker Hub..."
docker push ${IMAGE_NAME}:${VERSION}
docker push ${IMAGE_NAME}:latest

echo ""
echo "✅ Push thành công!"
echo "🎉 Image đã được push lên Docker Hub:"
echo "   - ${IMAGE_NAME}:${VERSION}"
echo "   - ${IMAGE_NAME}:latest"
