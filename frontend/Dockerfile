FROM node:18-alpine

# Аргумент для определения среды
ARG BUILD_ENV
ENV BUILD_ENV=${BUILD_ENV}

WORKDIR /app

# Устанавливаем зависимости
COPY package*.json ./
RUN npm ci

# Копируем исходный код
COPY . .

# Логика для разных сред
RUN if [ "$BUILD_ENV" = "local" ]; then \
      echo "Local environment - skipping build"; \
    else \
      echo "CI/Prod environment - building app"; \
      npm run build; \
    fi

# Открываем порт
EXPOSE 3000

# Запуск в зависимости от среды
CMD if [ "$BUILD_ENV" = "local" ]; then \
      npm run dev; \
    else \
      npm start; \
    fi