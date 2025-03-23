# Popcorn Palace - Movie Theater Booking System

## Overview

The **Popcorn Palace** is a movie theater booking system designed to manage movies, showtimes, and bookings efficiently. This project is built using **Java Spring Boot** and leverages **PostgreSQL** for persistent data storage. It follows RESTful principles for clear and structured API communication.

## Prerequisites

Before you proceed, ensure you have the following installed:

- **Java SDK 21** - [Download Here](https://www.oracle.com/java/technologies/downloads/#java21)
- **Maven** - [Download Here](https://maven.apache.org/download.cgi)
- **Docker & Docker Compose** - [Download Here](https://www.docker.com/products/docker-desktop)
- **Postman** (Optional) - For API testing
- **An IDE** (e.g., IntelliJ IDEA, Eclipse, or VS Code)

## Installation Steps

1. **Clone the Repository**

```bash
$ git clone https://github.com/AmirAbuELHija/popcorn-palace.git
$ cd popcorn-palace
```

2. **Set Up the Database**
    - Use the provided `compose.yml` file to run PostgreSQL locally:

```bash
$ docker compose up -d
```

- This will initialize a PostgreSQL database with:
    - **Username**: `popcorn-palace`
    - **Password**: `popcorn-palace`
    - **Database**: `popcorn-palace`

3. **Build the Project** Run the following command to build the project using Maven:

```bash
$ mvn clean install
```

4. **Run the Application** To start the Spring Boot application:

```bash
$ mvn spring-boot:run
```

The application will be accessible at `http://localhost:8080`.

## API Endpoints

### Movies API

- **Get All Movies**: `GET /movies/all`
- **Add Movie**: `POST /movies`
- **Update Movie**: `POST /movies/update/{movieTitle}`
- **Delete Movie**: `DELETE /movies/{movieTitle}`

### Showtimes API

- **Get Showtime by ID**: `GET /showtimes/{showtimeId}`
- **Add Showtime**: `POST /showtimes`
- **Update Showtime**: `POST /showtimes/update/{showtimeId}`
- **Delete Showtime**: `DELETE /showtimes/{showtimeId}`

### Bookings API

- **Create Booking**: `POST /bookings`

## Validation Rules
To ensure data integrity, consistent behavior, and robust error handling, the following validation rules are implemented in the system:

### Movie Management
1. **Movie Title Uniqueness**
    - Each movie title must be unique to prevent duplicate entries.
2. **Valid Duration**
    - Movie duration must be specified in **minutes** and must be a **positive integer** greater than zero.
3. **Valid Rating**
    - Ratings must be between **1.0** and **10.0**.
4. **Release Year**
    - Release year must be within a **reasonable range** (e.g., between 1900 and the current year).

---

### Showtime Management
1. **Showtime Duration Consistency**
    - The duration of the selected movie must match the showtime duration (within a **10-minute tolerance**).
2. **No Overlapping Showtimes**
    - Showtimes for the **same theater** must not overlap to avoid conflicts.
3. **Valid Price**
    - Showtime prices must be a **positive number** greater than zero.
4. **Valid Time Format**
    - `startTime` and `endTime` must follow ISO 8601 format (e.g., `2024-03-23T15:00:00`).
5. **Valid Theater Name**
    - Theater names must be **unique** to avoid conflicts.

---

### Ticket Booking Management
1. **Seat Validation**
    - Seat numbers must be within the valid range for the selected theater (e.g., between **1** and **theater capacity**).
2. **No Double-Booking**
    - Ensure no seat is booked twice for the same showtime.
3. **Valid UUID for User IDs**
    - Each `userId` must follow the **UUID v4** format for consistency and security.
4. **Booking Time Limits**
    - Booking requests should only be allowed before showtime start.
5. **Cancellation Policy**
    - Tickets cannot be canceled less than **30 minutes before** showtime.

---

### General System Rules
1. **Input Data Integrity**
    - All string fields are **trimmed** to prevent unnecessary spaces.
2. **Proper Error Handling**
    - Invalid inputs must trigger a `400 Bad Request` response with clear error messages.
3. **Database Integrity**
    - Foreign key relationships are enforced to maintain data consistency across **movies**, **showtimes**, and **bookings**.

---

### Error Handling Strategy
| **Error Type** | **HTTP Status Code** | **Description** |
|----------------|----------------------|------------------|
| **Invalid Input** | `400 Bad Request` | Malformed data or invalid field values. |
| **Resource Not Found** | `404 Not Found` | When a requested resource (e.g., movie, showtime) doesn't exist. |
| **Conflict Error** | `409 Conflict` | For unique constraints like duplicate movie titles or overlapping showtimes. |
| **Internal Server Error** | `500 Internal Server Error` | For unexpected errors in the application logic. |

---

## Running Tests

This project includes comprehensive tests to ensure stability and correctness.

## Testing
The system includes comprehensive tests for all endpoints and business logic. Tests cover:
- Successful operations
- Error cases
- Validation rules
- Edge cases
- Data integrity

### Running Tests with Maven

To run unit tests:

```bash
$ mvn test
```

## Database Management

For testing purposes, an **H2 in-memory database** is included for faster test execution. However, for persistence in production, **PostgreSQL** should be used.

## Error Handling

The system responds with appropriate HTTP status codes for various conditions:

- **400 Bad Request**: Invalid input or missing fields.
- **404 Not Found**: Resource not found.
- **409 Conflict**: Resource conflicts (e.g., duplicate entries).
- **500 Internal Server Error**: Unexpected server errors.


## Troubleshooting

- **Database Connection Issues**: Ensure the Docker container is running with `docker ps`.
- **Port Conflicts**: Confirm no other services are running on ports 5432 or 8080.
- **Build Failures**: Ensure your Java and Maven installations are up-to-date.



