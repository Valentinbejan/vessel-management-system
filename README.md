# Vessel Management System (VMS)

## 🚢 Overview
A RESTful backend API for managing maritime vessels and their owners, built with Java 17 and Spring Boot 3.4.5. The system provides comprehensive CRUD operations for ships and owners, including complex many-to-many relationships.

## 🏗️ Technology Stack
- **Java 17** with **Spring Boot 3.4.5**
- **Spring Data JPA** & **Hibernate** for ORM
- **PostgreSQL** for production database
- **H2** for testing (in-memory)
- **Gradle** for build management
- **Lombok** for reducing boilerplate code
- **SpringDoc OpenAPI 3** for API documentation (Swagger)

## 🎯 Key Features
- Complete CRUD operations for ships and owners
- Many-to-many relationships between ships and owners
- Comprehensive validation and error handling
- RESTful API design following best practices
- Swagger/OpenAPI documentation
- Full test coverage (unit and integration tests)

## 📋 API Endpoints

### Ship Management (`/api/v1/ships`)
- `GET /` - Get all ships
- `GET /{shipId}` - Get ship by ID
- `POST /` - Create new ship
- `PUT /{shipId}` - Update ship details
- `DELETE /{shipId}` - Delete ship

### Owner Management (`/api/v1/owners`)
- `GET /` - Get all owners
- `POST /` - Create new owner
- `DELETE /{ownerId}` - Delete owner

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- PostgreSQL database
- Gradle (or use the included wrapper)

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/vessel-management-system.git
cd vessel-management-system
```

### 2. Configure database
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/vessel_management_system
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Run database setup
Execute the SQL scripts in `src/main/resources/db/migration/`:
1. `setup_database_and_user.sql` (as superuser)
2. `V1__init_schema.sql`
3. `seed_data.sql` (optional)

### 4. Run the application
```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## 📚 API Documentation
Once the application is running, visit:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 🧪 Testing
Run all tests:
```bash
./gradlew test
```

## 📁 Project Structure
```
src/
├── main/
│   ├── java/com/logbook/vessel_management_system/
│   │   ├── controller/     # REST controllers
│   │   ├── service/        # Business logic
│   │   ├── repository/     # Data access layer
│   │   ├── entity/         # JPA entities
│   │   ├── dto/           # Data transfer objects
│   │   ├── config/        # Configuration classes
│   │   └── exception/     # Exception handling
│   └── resources/
│       ├── db/migration/  # Database scripts
│       └── application.properties
└── test/                  # Unit and integration tests
```

## 🔧 Configuration
The application supports different profiles:
- `default` - Uses PostgreSQL for production
- `test` - Uses H2 in-memory database for testing

## 📊 Business Rules
- Ships must have unique IMO numbers (7 digits)
- Ships must have at least one owner
- Deleting owners removes ownership links but preserves ships
- Deleting ships removes all related data (details, ownership links)

## 🤝 Contributing
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors
- Your Name - [Your GitHub](https://github.com/yourusername)

## 🙏 Acknowledgments
- Spring Boot team for the excellent framework
- PostgreSQL community
- OpenAPI/Swagger for documentation tools