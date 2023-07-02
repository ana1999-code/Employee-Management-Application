# Employee Management Application
This is a simple web application for managing employees and departments. The application is built using Java with clean JDBC implementation for data access and Flyway for database migration.

## Getting Started
To run the application locally, you need to have Docker and Maven installed on your machine.

1. Clone the repository:
```bash
git clone https://github.com/ana1999-code/Employee-Management-Application.git
cd Employee-Management-Application
```
2. Run the following command to start the application and PostgreSQL database using Docker Compose:
```bash
docker-compose up -d
```
3. Apply database migrations using Flyway:
```bash
mvn flyway:migrate
```
Once the application and database are up and running, you can access the endpoints through your web browser or API testing tool like Postman.
## Endpoints
The application exposes the following endpoints:

### Employees
- __GET /employees__: Get a list of all employees.
- __GET /employees/{id}__: Get a specific employee by ID.
- __POST /employees__: Create a new employee.
- __PUT /employees/{id}__: Update an existing employee by ID.
- __DELETE /employees/{id}__: Delete an employee by ID.
### Departments
- __GET /departments__: Get a list of all departments.
- __GET /departments/{id}__: Get a specific department by ID.
- __POST /departments__: Create a new department.
- __PUT /departments/{id}__: Update an existing department by ID.
- __DELETE /departments/{id}__: Delete a department by ID.

## Technology Stack
- Java
- JDBC
- PostgreSQL
- Docker
- Maven
- Flyway
