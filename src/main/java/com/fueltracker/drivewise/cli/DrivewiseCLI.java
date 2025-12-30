package com.fueltracker.drivewise.cli;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class DrivewiseCLI {
    private static final String BASE_URL = "http://localhost:8080/api";
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String command = args[0];

        try {
            switch (command) {
                case "create-car":
                    createCar(args);
                    break;
                case "add-fuel":
                    addFuel(args);
                    break;
                case "fuel-stats":
                    fuelStats(args);
                    break;
                default:
                    System.err.println("Unknown command: " + command);
                    printUsage();
                    System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void createCar(String[] args) throws Exception {
        String brand = null;
        String model = null;
        int year = 0;

        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--brand") && i + 1 < args.length) {
                brand = args[++i];
            } else if (args[i].equals("--model") && i + 1 < args.length) {
                model = args[++i];
            } else if (args[i].equals("--year") && i + 1 < args.length) {
                year = Integer.parseInt(args[++i]);
            }
        }

        if (brand == null || model == null || year == 0) {
            System.err.println("Error: --brand, --model, and --year are required");
            System.exit(1);
        }

        String jsonBody = String.format(
                "{\"brand\":\"%s\",\"model\":\"%s\",\"year\":%d}",
                brand, model, year);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/cars"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) {
            System.out.println("Car created successfully!");
            System.out.println(response.body());
        } else {
            System.err.println("Failed to create car. Status: " + response.statusCode());
            System.err.println(response.body());
            System.exit(1);
        }
    }

    private static void addFuel(String[] args) throws Exception {
        Long carId = null;
        double liters = 0;
        double price = 0;
        int odometer = 0;

        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--carId") && i + 1 < args.length) {
                carId = Long.parseLong(args[++i]);
            } else if (args[i].equals("--liters") && i + 1 < args.length) {
                liters = Double.parseDouble(args[++i]);
            } else if (args[i].equals("--price") && i + 1 < args.length) {
                price = Double.parseDouble(args[++i]);
            } else if (args[i].equals("--odometer") && i + 1 < args.length) {
                odometer = Integer.parseInt(args[++i]);
            }
        }

        if (carId == null || liters == 0 || price == 0 || odometer == 0) {
            System.err.println("Error: --carId, --liters, --price, and --odometer are required");
            System.exit(1);
        }

        String jsonBody = String.format(
                "{\"liters\":%.2f,\"price\":%.2f,\"odometer\":%d}",
                liters, price, odometer);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/cars/" + carId + "/fuel"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) {
            System.out.println("Fuel entry added successfully!");
            System.out.println(response.body());
        } else if (response.statusCode() == 404) {
            System.err.println("Error: Car not found (404)");
            System.exit(1);
        } else {
            System.err.println("Failed to add fuel entry. Status: " + response.statusCode());
            System.err.println(response.body());
            System.exit(1);
        }
    }

    private static void fuelStats(String[] args) throws Exception {
        Long carId = null;

        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--carId") && i + 1 < args.length) {
                carId = Long.parseLong(args[++i]);
            }
        }

        if (carId == null) {
            System.err.println("Error: --carId is required");
            System.exit(1);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/cars/" + carId + "/fuel/stats"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            FuelStatsResponse stats = objectMapper.readValue(response.body(), FuelStatsResponse.class);
            System.out.println("Fuel Statistics:");
            System.out.printf("Total fuel: %.1f L%n", stats.getTotalFuel());
            System.out.printf("Total cost: %.2f%n", stats.getTotalCost());
            System.out.printf("Average consumption: %.1f L/100km%n", stats.getAverageConsumption());
        } else if (response.statusCode() == 404) {
            System.err.println("Error: Car not found (404)");
            System.exit(1);
        } else {
            System.err.println("Failed to get fuel stats. Status: " + response.statusCode());
            System.err.println(response.body());
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  create-car --brand <brand> --model <model> --year <year>");
        System.out.println("  add-fuel --carId <id> --liters <liters> --price <price> --odometer <odometer>");
        System.out.println("  fuel-stats --carId <id>");
    }

    private static class FuelStatsResponse {
        private double totalFuel;
        private double totalCost;
        private double averageConsumption;

        public double getTotalFuel() {
            return totalFuel;
        }

        public void setTotalFuel(double totalFuel) {
            this.totalFuel = totalFuel;
        }

        public double getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(double totalCost) {
            this.totalCost = totalCost;
        }

        public double getAverageConsumption() {
            return averageConsumption;
        }

        public void setAverageConsumption(double averageConsumption) {
            this.averageConsumption = averageConsumption;
        }
    }
}
