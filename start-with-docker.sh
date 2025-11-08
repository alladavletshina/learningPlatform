#!/bin/bash

echo "Starting Learning Platform with Docker..."

# Проверяем наличие Docker
if ! command -v docker &> /dev/null; then
    echo "Error: Docker is not installed. Please install Docker first."
    exit 1
fi

# Собираем JAR файл
echo "Building application..."
mvn clean install -DskipTests

# Собираем Docker образ
echo "Building Docker image..."
docker build -t learning-platform .

# Запускаем с Docker Compose
echo "Starting services with Docker Compose..."
docker-compose up -d

echo "Application is starting... Check logs with: docker-compose logs -f app"