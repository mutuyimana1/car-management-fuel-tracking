package com.fueltracker.drivewise.service;

import com.fueltracker.drivewise.model.Car;
import com.fueltracker.drivewise.model.FuelEntry;
import com.fueltracker.drivewise.model.FuelStats;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class FuelService {
    private final Map<Long, List<FuelEntry>> fuelEntriesByCar = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final CarService carService;

    public FuelService(CarService carService) {
        this.carService = carService;
    }

    public FuelEntry addFuelEntry(Long carId, double liters, double price, int odometer) {
        // Generate ID based on the maximum existing ID to ensure sequential IDs
        Long id = generateNextId();
        FuelEntry entry = new FuelEntry(id, carId, liters, price, odometer);

        fuelEntriesByCar.computeIfAbsent(carId, k -> new ArrayList<>()).add(entry);

        // Also add to the Car object's fuelEntries list directly from the map
        Car car = carService.getCarByIdWithoutPopulating(carId);
        if (car != null) {
            car.addFuelEntry(entry);
        }

        return entry;
    }

    private Long generateNextId() {
        // Find the maximum ID from all existing fuel entries to ensure sequential IDs
        // This prevents ID gaps even if the counter gets out of sync
        long maxId = 0;
        for (List<FuelEntry> entries : fuelEntriesByCar.values()) {
            for (FuelEntry entry : entries) {
                if (entry.getId() != null && entry.getId() > maxId) {
                    maxId = entry.getId();
                }
            }
        }
        // Return the next ID (maxId + 1), ensuring sequential IDs
        long nextId = maxId + 1;
        // Update the atomic counter to stay ahead
        idGenerator.set(nextId + 1);
        return nextId;
    }

    public List<FuelEntry> getFuelEntriesByCarId(Long carId) {
        return fuelEntriesByCar.getOrDefault(carId, new ArrayList<>());
    }

    public FuelStats calculateStats(Long carId) {
        List<FuelEntry> entries = getFuelEntriesByCarId(carId);

        if (entries.isEmpty()) {
            return new FuelStats(0, 0, 0);
        }

        double totalFuel = entries.stream()
                .mapToDouble(FuelEntry::getLiters)
                .sum();

        double totalCost = entries.stream()
                .mapToDouble(FuelEntry::getPrice)
                .sum();

        // Calculate average consumption: L/100km
        // Need at least 2 entries with different odometer readings to calculate distance
        double averageConsumption = 0.0;
        if (entries.size() >= 2) {
            // Sort by odometer to get chronological order
            List<FuelEntry> sortedEntries = new ArrayList<>(entries);
            sortedEntries.sort(Comparator.comparingInt(FuelEntry::getOdometer));

            int totalDistance = 0;
            double totalFuelForDistance = 0;
            int validSegments = 0;

            for (int i = 1; i < sortedEntries.size(); i++) {
                FuelEntry prev = sortedEntries.get(i - 1);
                FuelEntry curr = sortedEntries.get(i);

                int distance = curr.getOdometer() - prev.getOdometer();
                // Only count segments with positive distance (odometer increasing)
                if (distance > 0) {
                    totalDistance += distance;
                    // The fuel added at the previous stop was used to travel this distance
                    totalFuelForDistance += prev.getLiters();
                    validSegments++;
                }
            }

            // Calculate average consumption: (total fuel consumed / total distance) * 100
            // This gives L/100km
            if (totalDistance > 0 && validSegments > 0) {
                averageConsumption = (totalFuelForDistance / totalDistance) * 100;
                // Round to 1 decimal place
                averageConsumption = Math.round(averageConsumption * 10.0) / 10.0;
            }
        }

        return new FuelStats(totalFuel, totalCost, averageConsumption);
    }
}
