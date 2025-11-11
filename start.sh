#!/bin/bash

echo "Starting Learning Platform..."

# Проверяем наличие Java
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed. Please install Java 21 or higher."
    exit 1
fi

# Проверяем наличие Maven
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed. Please install Maven 3.6 or higher."
    exit 1
fi

# Собираем проект
echo "Building project..."
mvn clean install -DskipTests

# Запускаем приложение
echo "Starting application..."
mvn spring-boot:run