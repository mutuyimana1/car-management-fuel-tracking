# Drivewise - Car Management & Fuel Tracking System

## Overview

A Spring Boot application with REST API, Servlet integration, and CLI client for managing cars and tracking fuel consumption.

## Architecture

- **Backend**: Spring Boot REST API with in-memory storage
- **Servlet**: Manual Java Servlet for fuel statistics
- **CLI Client**: Standalone Java application using HttpClient

## Cloning Application
```bash
git clone https://github.com/mutuyimana1/car-management-fuel-tracking.git
cd drivewise
```

## Running the Application

### Start the Backend Server

```bash
mvn spring-boot:run
```

The server will start on `http://localhost:8080`

### Run the CLI Client

After the server is running, you can use the CLI in a separate terminal:

```bash
# Compile and run the CLI
java -cp target/classes com.fueltracker.drivewise.cli.DrivewiseCLI <command> [options]
```

Or use Maven exec:

```bash
mvn exec:java -Dexec.mainClass="com.fueltracker.drivewise.cli.DrivewiseCLI" -Dexec.args="<command> [options]"
```

## API Endpoints

### REST API

1. **Create Car**

   ```
   POST /api/cars
   Body: {"brand": "Toyota", "model": "Corolla", "year": 2018}
   ```

2. **List All Cars**

   ```
   GET /api/cars
   ```
3. **List one Cars**

   ```
   GET /api/cars/{id}
   ```

4. **Add Fuel Entry**

   ```
   POST /api/cars/{id}/fuel
   Body: {"liters": 40, "price": 52.5, "odometer": 45000}
   ```

5. **Get Fuel Statistics** 
   ```
   GET /api/cars/{id}/fuel/stats
   Response: {"totalFuel": 120.0, "totalCost": 155.00, "averageConsumption": 6.4}
   ```

### Servlet Endpoint

6. **Fuel Stats via Servlet**
   ```
   GET /servlet/fuel-stats?carId={id}
   ```

## CLI Commands

1. **Create Car**

   ```bash
   java -cp target/classes com.fueltracker.drivewise.cli.DrivewiseCLI create-car --brand Toyota --model Corolla --year 2018
   ```

2. **Add Fuel Entry**

   ```bash
   java -cp target/classes com.fueltracker.drivewise.cli.DrivewiseCLI add-fuel --carId 1 --liters 40 --price 52.5 --odometer 45000
   ```

3. **Get Fuel Statistics**

   ```bash
   java -cp target/classes com.fueltracker.drivewise.cli.DrivewiseCLI fuel-stats --carId 1
   ```

   Expected output:

   ```
   Fuel Statistics:
   Total fuel: 120.0 L
   Total cost: 155.00
   Average consumption: 6.4 L/100km
   ```

## Error Handling

- 404 errors for invalid car IDs
- 400 errors for invalid request parameters
- Proper validation and error messages

## Features

- ✅ In-memory storage (no database required)
- ✅ REST API with proper HTTP status codes
- ✅ Manual Java Servlet implementation
- ✅ Separate CLI application using HttpClient
- ✅ Service layer reuse between REST API and Servlet
- ✅ Comprehensive error handling
