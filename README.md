🌐 **Язык | Language:** 🇷🇺 | [🇬🇧](README.en.md)

# 💰 Financial Control App

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.7-brightgreen)](https://spring.io/projects/spring-boot)
[![Next.js](https://img.shields.io/badge/Next.js-15.3.2-black)](https://nextjs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-blue)](https://www.postgresql.org/)

Веб-приложение для управления личными финансами.

## 📋 Содержание

- [Обзор](#-обзор)
- [Архитектура](#-архитектура)
- [Технологии](#-технологии)
- [Быстрый старт](#-быстрый-старт)
- [Настройка для разработки](#-настройка-для-разработки)
- [Документация](#-документация)
- [Лицензия](#-лицензия)

## 📋 Обзор

Financial Control App — это комплексная система управления личными финансами, которая помогает пользователям отслеживать расходы, управлять бюджетом и анализировать свои траты.

Приложение предоставляет веб-интерфейс для:
- Учёта ежедневных расходов и доходов
- Просмотра истории и категоризации транзакций
- Безопасного управления аккаунтами

## 🏗️ Архитектура

Приложение состоит из трёх основных компонентов:

- **Frontend**: Веб-приложение на Next.js, с которым взаимодействуют пользователи
- **Backend**: REST API на Spring Boot, обрабатывающий бизнес-логику
- **Database**: База данных PostgreSQL для хранения данных

Запросы пользователей проходят по цепочке Frontend → Backend → Database, а ответы возвращаются в обратном порядке.

## 🛠️ Технологии

**Backend:**
- Java 21, Spring Boot 3.4.7
- Spring Security, Spring Data JPA
- PostgreSQL, Liquibase

**Frontend:**
- Next.js 15.3.2, React 19
- Redux Toolkit, Material-UI

**Инструменты:**
- Docker, Docker Compose
- Maven, NPM

## 🚀 Быстрый старт

### Требования
- Docker & Docker Compose
- Git

### Установка и настройка

1. **Клонирование репозитория**
   ```bash
   git clone <repository-url>
   cd financial-control-app
   ```

2. **Настройка переменных окружения**
    - Скопируйте пример конфигурации: `cp .env.example .env`
    - Отредактируйте файл `.env` с вашими данными:
   ```env
   # Конфигурация базы данных
   DATABASE_URL=jdbc:postgresql://postgres:5432/financial
   DATABASE_USERNAME=your_db_username
   DATABASE_PASSWORD=your_db_password
   DATABASE_SCHEMA=public
   DATABASE_PORT=5432

   # Конфигурация бэкенда
   SERVER_PORT=8484
   REMEMBER_ME_KEY=your-256-bit-secret-key-here
   LOG_LEVEL=INFO
   FRONTEND_URL=http://localhost:3000
   ACTUATOR_ENDPOINTS=health,info,metrics

   # Конфигурация фронтенда
   FRONTEND_PORT=3000
   ```

3. **Запуск приложения**
   ```bash
   docker-compose up --build
   ```

4. **Доступ к приложению**
    - Фронтенд: http://localhost:3000
    - Документация API: http://localhost:8484/swagger-ui.html

## 🔧 Настройка для разработки

Для разработки можно запускать отдельные сервисы через Docker Compose:

### Только база данных
```bash
docker-compose up postgres
```

### Только бэкенд
```bash
docker-compose up backend
```
Бэкенд будет доступен по адресу: http://localhost:8484

### Только фронтенд
```bash
docker-compose up frontend
```
Фронтенд будет доступен по адресу: http://localhost:3000

### Несколько сервисов
```bash
# Бэкенд + База данных
docker-compose up backend postgres

# Фронтенд + Бэкенд + База данных
docker-compose up
```

## 📚 Документация

- **[Документация Backend API](backend/docs/API.md)** — справочник REST API с эндпоинтами, примерами и обработкой ошибок

## 📄 Лицензия

MIT License — см. файл [LICENSE](LICENSE).