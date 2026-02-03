🌐 **Язык | Language:** [🇷🇺](README.md) | 🇬🇧

# 💰 Financial Control App

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.7-brightgreen)](https://spring.io/projects/spring-boot)
[![Next.js](https://img.shields.io/badge/Next.js-15.3.2-black)](https://nextjs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-blue)](https://www.postgresql.org/)

Personal finance management web application.

## 📋 Table of Contents

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Built with](#-built-with)
- [Quick Start](#-quick-start)
- [Development Setup](#-development-setup)
- [Documentation](#-documentation)
- [License](#-license)

## 📋 Overview

Financial Control App is a comprehensive personal finance management system that helps users track their expenses, manage budgets, and gain insights into their spending habits.

The application provides a modern web interface for:
- Recording daily expenses and income
- Categorizing transactions
- Viewing financial reports and analytics
- Managing user accounts securely

Built with modern technologies, it ensures reliable performance, security, and scalability.

## 🏗️ Architecture

The application consists of three main components:

- **Frontend**: Next.js web application that users interact with
- **Backend**: Spring Boot REST API that handles business logic
- **Database**: PostgreSQL database for data storage

User requests flow from Frontend → Backend → Database, with responses returning in reverse order.

## 🛠️ Built with

**Backend:**
- Java 21, Spring Boot 3.4.7
- Spring Security, Spring Data JPA
- PostgreSQL, Liquibase

**Frontend:**
- Next.js 15.3.2, React 19
- Redux Toolkit, Material-UI

**Tools:**
- Docker, Docker Compose
- Maven, NPM

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- Git

### Installation & Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd financial-control-app
   ```

2. **Configure environment variables**
    - Copy the example environment file: `cp .env.example .env`
    - Edit `.env` file with your database credentials:
   ```env
   # Database Configuration
   DATABASE_URL=jdbc:postgresql://postgres:5432/financial
   DATABASE_USERNAME=your_db_username
   DATABASE_PASSWORD=your_db_password
   DATABASE_SCHEMA=public
   DATABASE_PORT=5432

   # Backend Configuration
   SERVER_PORT=8484
   REMEMBER_ME_KEY=your-256-bit-secret-key-here
   LOG_LEVEL=INFO
   FRONTEND_URL=http://localhost:3000
   ACTUATOR_ENDPOINTS=health,info,metrics

   # Frontend Configuration
   FRONTEND_PORT=3000
   ```

3. **Start the application**
   ```bash
   docker-compose up --build
   ```

4. **Access the application**
    - Frontend: http://localhost:3000
    - API Documentation: http://localhost:8484/swagger-ui.html

## 🔧 Development Setup

For development, you can run individual services using Docker Compose:

### Database Only
```bash
docker-compose up postgres
```

### Backend Only
```bash
docker-compose up backend
```
Backend will be available at: http://localhost:8484

### Frontend Only
```bash
docker-compose up frontend
```
Frontend will be available at: http://localhost:3000

### Multiple Services
```bash
# Backend + Database
docker-compose up backend postgres

# Frontend + Backend + Database
docker-compose up
```

## 📚 Documentation

- **[Backend API Documentation](backend/docs/API.en.md)** — REST API reference with endpoints, examples, and error handling

## 📄 License

MIT License - see [LICENSE](LICENSE) file.
