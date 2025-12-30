package com.fueltracker.drivewise.controller;

import com.fueltracker.drivewise.dto.AddFuelRequest;
import com.fueltracker.drivewise.dto.CreateCarRequest;
import com.fueltracker.drivewise.model.Car;
import com.fueltracker.drivewise.model.FuelEntry;
import com.fueltracker.drivewise.model.FuelStats;
import com.fueltracker.drivewise.service.CarService;
import com.fueltracker.drivewise.service.FuelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {
    private final CarService carService;
    private final FuelService fuelService;

    public CarController(CarService carService, FuelService fuelService) {
        this.carService = carService;
        this.fuelService = fuelService;
    }

    @PostMapping
    public ResponseEntity<Car> createCar(@RequestBody CreateCarRequest request) {
        Car car = carService.createCar(request.getBrand(), request.getModel(), request.getYear());
        return ResponseEntity.status(HttpStatus.CREATED).body(car);
    }

    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        List<Car> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        Car car = carService.getCarById(id);
        if (car == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(car);
    }

    @PostMapping("/{id}/fuel")
    public ResponseEntity<FuelEntry> addFuel(
            @PathVariable Long id,
            @RequestBody AddFuelRequest request) {
        if (!carService.carExists(id)) {
            return ResponseEntity.notFound().build();
        }
        FuelEntry entry = fuelService.addFuelEntry(id, request.getLiters(), request.getPrice(), request.getOdometer());
        return ResponseEntity.status(HttpStatus.CREATED).body(entry);
    }

    @GetMapping("/{id}/fuel/stats")
    public ResponseEntity<FuelStats> getFuelStats(@PathVariable Long id) {
        if (!carService.carExists(id)) {
            return ResponseEntity.notFound().build();
        }
        FuelStats stats = fuelService.calculateStats(id);
        return ResponseEntity.ok(stats);
    }
}
