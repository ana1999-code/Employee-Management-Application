CREATE TABLE employees(
id SERIAL PRIMARY KEY,
first_name VARCHAR NOT NULL,
last_name VARCHAR NOT NULL,
department_id INT,
email VARCHAR NOT NULL UNIQUE,
phone_number VARCHAR NOT NULL UNIQUE,
salary DOUBLE PRECISION CHECK(salary > 1.0));

ALTER TABLE employees
    ADD CONSTRAINT employee_dep_fk FOREIGN KEY (department_id) REFERENCES departments(id);
