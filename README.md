# 🎓 Academic Information System (AIS)

A comprehensive, Academic Information System built with Spring Boot, featuring JWT authentication, role-based access control, and a modern RESTful API architecture.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Running with Docker](#running-with-docker)
  - [Running Locally](#running-locally)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Security](#security)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

The Academic Information System (AIS) is a robust platform designed to manage academic operations including student enrollment, teacher assignments, grade management, and administrative tasks. The system provides a secure, scalable solution with modern authentication mechanisms and clean architecture principles.

## ✨ Features

### Core Functionality
- **👤 User Management**
  - Role-based access control (Administrator, Teacher, Student)
  - Secure password hashing with BCrypt
  - JWT-based stateless authentication
  
- **👨‍🎓 Student Management**
  - Student registration and profile management
  - Group assignment
  - Grade viewing and statistics
  
- **👨‍🏫 Teacher Management**
  - Teacher registration and assignment
  - Subject assignment to groups
  - Grade entry and management
  
- **📚 Academic Operations**
  - Subject creation and management
  - Study group organization
  - Subject-teacher-group assignments
  - Comprehensive grade tracking

### Technical Features
- ✅ JWT Authentication & Authorization
- ✅ RESTful API with OpenAPI/Swagger documentation
- ✅ Global exception handling
- ✅ Input validation
- ✅ Docker containerization
- ✅ SOLID & OOP principles implementation
- ✅ DTO pattern for data transfer
- ✅ Transaction management

## 🛠 Technology Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.2** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Data persistence
- **Hibernate** - ORM
- **JWT (JJWT)** - Token-based authentication

### Database
- **PostgreSQL 17** - Primary database

### DevOps & Tools
- **Docker & Docker Compose** - Containerization
- **Maven** - Build tool
- **Swagger/OpenAPI** - API documentation
- **SLF4J & Logback** - Logging

## 🏗 Architecture

The application follows a layered architecture with clear separation of concerns:

```
┌─────────────────────────────────────────────┐
│          Presentation Layer                 │
│    (Controllers - REST API & MVC)           │
├─────────────────────────────────────────────┤
│           Service Layer                     │
│  (Business Logic - SOLID Principles)        │
├─────────────────────────────────────────────┤
│         Repository Layer                    │
│     (Data Access - Spring Data JPA)         │
├─────────────────────────────────────────────┤
│           Database Layer                    │
│         (PostgreSQL/H2)                     │
└─────────────────────────────────────────────┘
```

### Key Architectural Decisions

1. **DTOs for Data Transfer**: Entities are not exposed directly; DTOs ensure clean API contracts
2. **Service Decomposition**: AdminService split into focused services (StudentManagementService, TeacherManagementService, etc.)
3. **Stateless Authentication**: JWT tokens enable horizontal scaling
4. **Exception Handling**: Centralized error handling with meaningful HTTP status codes

## 🚀 Getting Started

### Prerequisites

- **Docker & Docker Compose** (recommended) OR
- **Java 17+**
- **Maven 3.8+**
- **PostgreSQL 16** (if running locally)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Mykhailo-cpp/AIS_programming_practice.git
   cd AIS_programming_practice
   ```

2. **Configure environment variables**
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

### Running with Docker (Recommended)

The easiest way to run the application:

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f ais-app

# Stop services
docker-compose down

# Stop and remove volumes (fresh start)
docker-compose down -v
```

The application will be available at:
- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **pgAdmin**: http://localhost:5050 (optional, with `--profile dev`)

### Running Locally

1. **Start PostgreSQL**
   ```bash
   # Using Docker
   docker run -d \
     --name ais-postgres \
     -e POSTGRES_DB=ais_db \
     -e POSTGRES_USER=ais_user \
     -e POSTGRES_PASSWORD=ais_password \
     -p 5432:5432 \
     postgres:16-alpine
   ```

2. **Configure application.properties**
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/ais_db
   spring.datasource.username=ais_user
   spring.datasource.password=ais_password
   ```

3. **Build and run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

## 📚 API Documentation

### Interactive Documentation
Once the application is running, visit:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs


## 🔒 Security

### Authentication Flow
1. User sends credentials to `/api/auth/login`
2. Server validates credentials
3. Server generates JWT access token (24h) and refresh token (7d)
4. Client includes token in `Authorization: Bearer <token>` header
5. JwtAuthenticationFilter validates token on each request

### Password Security
- Passwords are hashed using BCrypt (strength: 10)
- Plain text passwords are never stored
- Initial passwords are user's last name (should be changed on first login)

### Role-Based Access Control
- **ADMINISTRATOR**: Full system access
- **TEACHER**: Grade management, view assigned subjects
- **STUDENT**: View own grades and information


## 👨‍💻 Author

**Mykhailo**
- GitHub: [@Mykhailo-cpp](https://github.com/Mykhailo-cpp)
- Email: m.osadchuk2201@gmail.com
---

**⭐ If you find this project useful, please consider giving it a star!**
