# Vessel Management System (VMS)
LogBook Summer 2025 - Software Developer Trainee Assignment

## Assignment Overview
This project implements a Vessel Management System as part of the LogBook Summer 2025 Software Developer Trainee application process.

## Assignment Requirements

### Database Requirements
- Implement many-to-many relationship between owners and ships
- One owner can have many ships
- One ship can be owned by many owners  
- Ships have details: Name, IMO Number, Type, Tonnage

### RESTful CRUD API Requirements
1. Get all ships from ship table
2. Add new ship
3. Update ship
4. Delete ship
5. Get all details about a specific ship
6. Delete an owner who owns several ships

### Required Technologies (Java Implementation)
- PostgreSQL database
- JPA and Hibernate for ORM
- Spring Framework for dependency injection
- Gradle for build management
- Integration Tests proving the whole system works

## Technology Stack Implemented
- Java 17
- Spring Boot 3.4.5
- Spring Data JPA & Hibernate
- PostgreSQL database
- H2 for testing (in-memory)
- Gradle build system
- Lombok
- SpringDoc OpenAPI 3 for API documentation
- JUnit 5 & Mockito for testing

## Database Schema

### Owner_Table
```sql
CREATE TABLE Owner_Table (
    Owner_Id BIGSERIAL PRIMARY KEY,
    Owner_name VARCHAR(255) NOT NULL UNIQUE
);
```

### Ships_Table  
```sql
CREATE TABLE Ships_Table (
    Id BIGSERIAL PRIMARY KEY,
    Ship_name VARCHAR(255) NOT NULL,
    Imo_number VARCHAR(50) NOT NULL UNIQUE
);
```

### Category_Table
```sql
CREATE TABLE Category_Table (
    Ship_id BIGINT PRIMARY KEY,
    Ship_type VARCHAR(100),
    Ship_tonnage INTEGER,
    FOREIGN KEY (Ship_id) REFERENCES Ships_Table(Id) ON DELETE CASCADE
);
```

### Ship_Ownership_Link_Table
```sql
CREATE TABLE Ship_Ownership_Link_Table (
    Ship_Id_FK BIGINT NOT NULL,
    Owner_Id_FK BIGINT NOT NULL,
    PRIMARY KEY (Ship_Id_FK, Owner_Id_FK),
    FOREIGN KEY (Ship_Id_FK) REFERENCES Ships_Table(Id) ON DELETE CASCADE,
    FOREIGN KEY (Owner_Id_FK) REFERENCES Owner_Table(Owner_Id) ON DELETE CASCADE
);
```

## Getting Started

### Prerequisites
- Java 17 or higher
- PostgreSQL database
- Git

### Step-by-Step Setup Instructions

#### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/vessel-management-system.git
cd vessel-management-system
```

#### 2. Install Java 17 (if not already installed)
**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

**Windows:** Download from [Oracle](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or use [sdkman](https://sdkman.io/)

**macOS:**
```bash
brew install openjdk@17
```

#### 3. Install and Setup PostgreSQL

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo service postgresql start
```

**Windows:** Download from [PostgreSQL Official Site](https://www.postgresql.org/download/windows/)

**macOS:**
```bash
brew install postgresql
brew services start postgresql
```

#### 4. Configure PostgreSQL Database

1. **Connect to PostgreSQL as superuser:**
   ```bash
   sudo -u postgres psql
   ```

2. **Execute the database setup script:**
   ```sql
   \i src/main/resources/db/migration/setup_database_and_user.sql
   ```
   
   **Note:** Before running this script, open it and change the password on line with `CREATE USER vms_app_user WITH PASSWORD 'your_secure_password';`

3. **Create the schema:**
   ```sql
   \i src/main/resources/db/migration/V1__init_schema.sql
   ```

4. **Load sample data (optional):**
   ```sql
   \i src/main/resources/db/migration/seed_data.sql
   ```

5. **Exit PostgreSQL:**
   ```sql
   \q
   ```

#### 5. Configure Application Properties

Open `src/main/resources/application.properties` and update the database password:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/vessel_management_system
spring.datasource.username=vms_app_user
spring.datasource.password=your_secure_password
```

Replace `your_secure_password` with the password you set in the database setup script.

#### 6. Build and Run the Application

1. **Make sure you're in the project directory:**
   ```bash
   cd vessel-management-system
   ```

2. **Give execute permission to gradle wrapper (Linux/macOS):**
   ```bash
   chmod +x gradlew
   ```

3. **Build the application:**
   ```bash
   ./gradlew build
   ```

4. **Run the application:**
   ```bash
   ./gradlew bootRun
   ```

   **For Windows, use:**
   ```cmd
   .\gradlew.bat bootRun
   ```

#### 7. Verify the Application is Running

- Application URL: http://localhost:8080
- Swagger Documentation: http://localhost:8080/swagger-ui.html
- Health Check: http://localhost:8080/api/v1/ships

#### 8. Test the API

**Get all ships:**
```bash
curl -X GET http://localhost:8080/api/v1/ships
```

**Get all owners:**
```bash
curl -X GET http://localhost:8080/api/v1/owners
```

### Troubleshooting

**If you get database connection errors:**
1. Make sure PostgreSQL is running: `sudo service postgresql status`
2. Verify the database exists: `sudo -u postgres psql -l | grep vessel`
3. Check if the user exists: `sudo -u postgres psql -c "\du" | grep vms_app_user`

**If you get permission errors:**
1. Make sure the gradlew file is executable: `chmod +x gradlew`
2. Check Java version: `java -version`

**Port already in use:**
```bash
# Find what's using port 8080
sudo lsof -i :8080
# Kill the process if needed
sudo kill -9 <PID>
```

## API Documentation
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

## Implemented API Endpoints

### Ship Management (/api/v1/ships)
- `GET /api/v1/ships` - Get all ships
- `POST /api/v1/ships` - Add new ship  
- `PUT /api/v1/ships/{shipId}` - Update ship
- `DELETE /api/v1/ships/{shipId}` - Delete ship
- `GET /api/v1/ships/{shipId}` - Get ship details

### Owner Management (/api/v1/owners)
- `GET /api/v1/owners` - Get all owners
- `POST /api/v1/owners` - Create new owner
- `DELETE /api/v1/owners/{ownerId}` - Delete owner

## Implementation Details

### Entity Relationships
- Ship entity with one-to-one relationship to ShipCategoryDetails
- Many-to-many relationship between Ship and Owner entities
- Bidirectional mapping with helper methods for relationship management

### Business Logic
- Ships must have unique IMO numbers
- Deleting owners removes ownership links but preserves ships
- Deleting ships removes all related data (details, ownership links)
- Ships can have multiple owners, owners can own multiple ships

### Validation
- Bean validation for DTOs
- Custom validation messages
- Global exception handling

### Testing
- Unit tests for service layer logic
- Integration tests for REST endpoints
- H2 in-memory database for test isolation
- Mockito for mocking dependencies

## Project Structure
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
│       ├── db/migration/  # SQL scripts
│       └── application.properties
└── test/                  # Unit and integration tests
```

## Running Tests
```bash
./gradlew test
```

## Assignment Compliance
- PostgreSQL database with proper schema
- Many-to-many relationship implementation
- All 6 required API endpoints implemented
- JPA/Hibernate for data persistence
- Spring Framework for dependency injection
- Gradle build system
- Integration and JUnit tests validating system functionality
- API documentation with Swagger
- SQL scripts for database setup and seeding

## Demo Video

https://github.com/user-attachments/assets/e7ec352c-0b61-4aab-a187-4b7bec3e5e12