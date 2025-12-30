package com.fueltracker.drivewise.service;

import com.fueltracker.drivewise.model.Car;
import com.fueltracker.drivewise.model.FuelEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CarService {
    private final Map<Long, Car> cars = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private FuelService fuelService;

    @Autowired
    @Lazy
    public void setFuelService(FuelService fuelService) {
        this.fuelService = fuelService;
    }

    public Car createCar(String brand, String model, int year) {
        Long id = idGenerator.getAndIncrement();
        Car car = new Car(id, brand, model, year);
        cars.put(id, car);
        return car;
    }

    public List<Car> getAllCars() {
        List<Car> allCars = new ArrayList<>(cars.values());
        // Populate fuel entries for each car
        for (Car car : allCars) {
            populateFuelEntries(car);
        }
        return allCars;
    }

    public Car getCarById(Long id) {
        Car car = cars.get(id);
        if (car != null) {
            populateFuelEntries(car);
        }
        return car;
    }

    // Internal method to get car without populating fuel entries (to avoid circular
    // calls)
    public Car getCarByIdWithoutPopulating(Long id) {
        return cars.get(id);
    }

    private void populateFuelEntries(Car car) {
        if (fuelService != null) {
            List<FuelEntry> entries = fuelService.getFuelEntriesByCarId(car.getId());
            car.setFuelEntries(new ArrayList<>(entries));
        }
    }

    public boolean carExists(Long id) {
        return cars.containsKey(id);
    }
}
