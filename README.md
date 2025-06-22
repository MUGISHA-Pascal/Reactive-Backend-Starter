# Reactive Backend Starter

A modern, reactive Spring Boot backend application built with WebFlux, R2DBC, and PostgreSQL.

## 🚀 Features

- **Reactive Architecture**: Spring WebFlux for non-blocking I/O
- **Database**: PostgreSQL with R2DBC for reactive database access
- **Security**: JWT-based authentication and authorization
- **API Documentation**: Swagger/OpenAPI 3.0 integration
- **WebSocket Support**: Real-time communication capabilities
- **Monitoring**: Spring Boot Actuator for health checks and metrics
- **Docker Support**: Containerized deployment

## 🛠️ Tech Stack

- Java 21
- Spring Boot 3.4.3
- Spring WebFlux
- Spring Data R2DBC
- PostgreSQL
- Spring Security + JWT
- Swagger/OpenAPI 3.0
- WebSocket
- Lombok
- Maven

## 📋 Prerequisites

- Java 21+
- Maven 3.6+
- PostgreSQL 12+

## 🚀 Quick Start

### 1. Database Setup

```sql
CREATE DATABASE javadb;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE javadb TO postgres;
```

### 2. Run the Application

```bash
# Using Maven
mvn spring-boot:run

# Using Maven Wrapper
./mvnw spring-boot:run

# Using Docker
docker build -t reactive-backend .
docker run -p 8081:8081 reactive-backend
```

The application starts on `http://localhost:8081`

## 📚 API Documentation

- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/v3/api-docs
- **Health Check**: http://localhost:8081/actuator/health

## 🔧 Available Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### Users
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Products
- `GET /api/products` - Get all products
- `POST /api/products` - Create product
- `GET /api/products/{id}` - Get product by ID
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

### Inventory
- `GET /api/inventory` - Get inventory status

### Files
- `POST /api/files/upload` - Upload file
- `GET /api/files/{id}` - Download file
- `DELETE /api/files/{id}` - Delete file

### Chat (WebSocket)
- WebSocket endpoint for real-time messaging

## 🔐 Security

JWT-based authentication. Include token in Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## 📁 Project Structure

```
src/main/java/com/starter/backend/
├── BackendApplication.java          # Main application class
├── config/                          # Configuration classes
├── controllers/                     # REST controllers
├── dtos/                           # Data Transfer Objects
├── enums/                          # Enumeration classes
├── exceptions/                     # Custom exceptions
├── models/                         # Entity models
├── payload/                        # Request/Response payloads
├── repository/                     # R2DBC repositories
├── schedules/                      # Scheduled tasks
├── security/                       # Security configuration
├── services/                       # Business logic services
├── util/                          # Utility classes
└── aspects/                       # AOP aspects
```

## 🔧 Configuration

Update database settings in `src/main/resources/application.properties`:

```properties
spring.r2dbc.url=r2dbc:postgresql://localhost:5432/javadb
spring.r2dbc.username=your_username
spring.r2dbc.password=your_password
```

## 🧪 Testing

```bash
mvn test
```

## 📊 Monitoring

- **Health Check**: `/actuator/health`
- **Metrics**: `/actuator/metrics`
- **Info**: `/actuator/info`

## 🐳 Docker

```bash
# Build
docker build -t reactive-backend .

# Run
docker run -p 8081:8081 \
  -e SPRING_R2DBC_URL=r2dbc:postgresql://host.docker.internal:5432/javadb \
  reactive-backend
```

## 🔄 Development

- Hot reload enabled with Spring Boot DevTools
- Logs written to `project.log`
- Debug mode available in `application.properties`

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:

1. Check the API documentation at `/swagger-ui.html`
2. Review the application logs
3. Check the health endpoint at `/actuator/health`
4. Open an issue in the repository

## 🔗 Related Links

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring WebFlux Documentation](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [R2DBC Documentation](https://r2dbc.io/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/) 