# Project Title

A brief description of what this project does and who it's for

# Backend Account Service - Java Spring Boot

## Payroll Management Service

This project implements a secure payroll distribution system for employees using Java and the Spring Framework. Instead of sending payrolls via corporate email, the payrolls are now securely delivered to each employee's account on the corporate website.

## Project Overview

The goal of this project is to create a robust API structure that allows employees to access their payroll information through a secure portal. The service ensures the following:

- **Role-Based Access**: Employees can only access their own payrolls, and administrators can manage the payrolls across the company.
- **Security**: Implements modern security measures, including encrypted communication, role-based access control, and protection against common vulnerabilities.
- **Usability**: Provides a clean, efficient, and user-friendly API for payroll management, reducing the risks associated with email-based payroll distribution.

## Key Features

1. **API Structure**: The API supports endpoints for payroll management, including viewing and managing payroll data.
2. **Role Model**: Supports various user roles such as Employee, Manager, and Administrator, with appropriate access control for each role.
3. **Business Logic**: Handles the core logic for distributing, viewing, and updating payroll information.
4. **Security**: Ensures secure access and data transmission through SSL and other security best practices.

## Getting Started

### Project Structure

The project structure is organized as follows:

src ├── main │ ├── java │ │ └── com │ │ └── yourpackage │ │ ├── controller │ │ ├── dto │ │ ├── entity │ │ ├── repository │ │ └── security │ └── resources │ ├── application.properties │ └── keystore └── test └── java └── com └── yourpackage

### Prerequisites

- Java 17+
- Spring Boot
- Gradle for building the project

## Built With

- **Java**: The primary programming language used for developing the application.
- **Spring Framework**: A powerful framework for building Java applications, providing extensive features for web development and security.
- **Spring Boot**: Simplifies the setup and development of Spring applications by providing default configurations and built-in tools.
- **Hibernate**: An object-relational mapping (ORM) tool that facilitates database interactions and management.
- **H2 Database**: A lightweight, in-memory database often used for testing and development purposes.

## Installation Instructions

### Prerequisites

- Ensure you have [Java JDK 17](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) or higher installed.
- You should have [Gradle](https://gradle.org/install/) installed for dependency management.

### H2 Database Console

You can access the H2 database console at `http://localhost:28852/h2-console`.

### Steps to Install

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/yourproject.git
   cd yourproject


## Authentication

This project implements secure authentication for user accounts. The following requirements and features are enforced:

### Password Requirements

To ensure the security of user accounts, the following password requirements are enforced:

- Passwords must contain **at least 12 characters**.
- Users must avoid using breached passwords. For testing purposes, the following list contains passwords that are considered breached and should not be used:
  ```json
  {
      "PasswordForJanuary",
      "PasswordForFebruary",
      "PasswordForMarch",
      "PasswordForApril",
      "PasswordForMay",
      "PasswordForJune",
      "PasswordForJuly",
      "PasswordForAugust",
      "PasswordForSeptember",
      "PasswordForOctober",
      "PasswordForNovember",
      "PasswordForDecember"
  }

## Roles and Permissions

The following table outlines the permissions for different user roles regarding the API endpoints:

Note: The first user will be Administrator by default.


| Endpoint                   | Anonymous | User | Accountant | Administrator | Auditor |
|----------------------------|-----------|------|------------|----------------|---------|
| POST api/auth/signup       | Yes       | Yes  | Yes        | Yes            | Yes     |
| POST api/auth/changepass   | No        | Yes  | Yes        | No             | No      |
| GET api/empl/payment       | No        | Yes  | Yes        | No             | No      |
| POST api/acct/payments     | No        | No   | Yes        | No             | No      |
| PUT api/acct/payments      | No        | No   | Yes        | No             | No      |
| GET api/admin/user         | No        | No   | No         | Yes            | No      |
| DELETE api/admin/user      | No        | No   | No         | Yes            | No      |
| PUT api/admin/user/role    | No        | No   | No         | Yes            | No      |
| PUT api/admin/user/access  | No        | No   | No         | Yes            | No      |
| GET api/security/events     | No        | No   | No         | No             | Yes     |



### Example API Endpoints

Here are some of the key API endpoints available in the Payroll API Service:
### User Sign Up
- **Endpoint**: `/api/auth/signup`
- **Method**: `POST`
- **Description**: Registers a new user account.
- **Request Body**:
```json
{
  "username": "john_doe",
  "email": "john.doe@acme.com",
  "password": "yourSecurePassword123"
}

  ```
Response:

  ```
json

{
  "status": "success",
  "message": "User registered successfully.",
  "userId": "12345"
}

  ```

### Payment Endpoint

**Endpoint**: `POST api/acct/payments`

**Description**: This endpoint allows authorized users to create a new payment entry for an employee.

**Request Body**:
```json
{
    "employee": "john.doe@acme.com",
    "period": "10-2024",
    "salary": 5000
}

```

**Endpoint**: GET api/empl/payment

Description: This endpoint retrieves salary information for the authenticated employee. It takes an optional period parameter that specifies the month and year. If the period parameter is not specified, the endpoint returns the salary information for each period from the database in descending order by date.

Parameters:

- **period** (optional): The period in `MM-yyyy` format to filter salary information. If provided, the endpoint will return salary details for that specific month and year.

    e.g. A GET request for api/empl/payment?period=01-2021 with the correct authentication for johndoe@acme.com.

Response Format:

If the period parameter is specified:

json
```
{
    "name": "<user name>",
    "lastname": "<user lastname>",
    "period": "<name of month-YYYY>",
    "salary": "X dollar(s) Y cent(s)"
}
```
If the period parameter is not specified, the response will be an array of salary information objects:

json
```
[
    {
        "name": "John",
        "lastname": "Doe",
        "period": "January-2024",
        "salary": "500 dollar(s) 0 cent(s)"
    },
    {
        "name": "John",
        "lastname": "Doe",
        "period": "February-2024",
        "salary": "600 dollar(s) 0 cent(s)"
    }
    // Additional salary entries...
]
```

### Logging Events

The service logs important information security events to enhance monitoring and accountability. Below is a list of events that are logged, along with their descriptions:
| Description                                               | Event Name     |
|----------------------------------------------------------|-----------------|
| A user has been successfully registered                   | CREATE_USER     |
| A user has changed the password successfully              | CHANGE_PASSWORD  |
| A user is trying to access a resource without access rights| ACCESS_DENIED    |
| Failed authentication                                     | LOGIN_FAILED     |
| A role is granted to a user                              | GRANT_ROLE      |
| A role has been revoked                                   | REMOVE_ROLE     |
| The Administrator has locked the user                    | LOCK_USER       |
| The Administrator has unlocked a user                    | UNLOCK_USER     |
| The Administrator has deleted a user                     | DELETE_USER     |
| A user has been blocked on suspicion of a brute force attack| BRUTE_FORCE    |

| Field    | Description                                   |
|----------|-----------------------------------------------|
| date     | <date>                                       |
| action   | <event_name from table>                      |
| subject  | <The user who performed the action>          |
| object   | <The object on which the action was performed>|
| path     | <api>                

Each logged event contains the following fields:

```
json

{
    "date": "<date>",
    "action": "<event_name from table>",
    "subject": "<The user who performed the action>",
    "object": "<The object on which the action was performed>",
    "path": "<api>"
}
   
```

## Conclusion

This Payroll Management Service provides a secure and efficient way for employees to access payroll information, ensuring robust security and access controls.

## License

This project is licensed under the [MIT License](LICENSE).

Thank you for your interest in this project!
 
