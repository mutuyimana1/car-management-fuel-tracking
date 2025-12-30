# Fuel Consumption Calculation Explanation

## How Average Consumption is Calculated

The average fuel consumption is calculated as **Liters per 100 kilometers (L/100km)**.

### Formula
```
Average Consumption = (Total Fuel Consumed / Total Distance Traveled) × 100
```

### Requirements
- **At least 2 fuel entries** are needed to calculate consumption
- **Odometer readings must be increasing** (each entry should have a higher odometer value than the previous one)

### Calculation Process

1. **Sort entries by odometer** (chronological order)
2. **For each pair of consecutive entries:**
   - Calculate distance: `current.odometer - previous.odometer`
   - The fuel added at the previous stop was used to travel this distance
3. **Sum up:**
   - Total distance traveled
   - Total fuel consumed (fuel added at each stop, excluding the last one)
4. **Calculate:** `(Total Fuel / Total Distance) × 100`

### Example Calculation

**Fuel Entries:**
- Entry 1: Odometer = 45,000 km, Liters = 40 L
- Entry 2: Odometer = 45,500 km, Liters = 35 L  
- Entry 3: Odometer = 46,000 km, Liters = 42 L

**Calculation:**
1. Segment 1: 45,000 → 45,500 km
   - Distance: 500 km
   - Fuel used: 40 L (from Entry 1)
   - Consumption: (40 L / 500 km) × 100 = 8.0 L/100km

2. Segment 2: 45,500 → 46,000 km
   - Distance: 500 km
   - Fuel used: 35 L (from Entry 2)
   - Consumption: (35 L / 500 km) × 100 = 7.0 L/100km

3. **Average Consumption:**
   - Total fuel: 40 + 35 = 75 L
   - Total distance: 500 + 500 = 1,000 km
   - Average: (75 L / 1,000 km) × 100 = **7.5 L/100km**

### Why It Returns 0.0

The average consumption will be **0.0** if:
- ❌ Only 1 fuel entry exists (can't calculate distance)
- ❌ Odometer readings are the same or decreasing
- ❌ No valid segments with positive distance

### Example: Getting 6.4 L/100km

To get **6.4 L/100km** with **120 L total fuel**:

**Required total distance:**
- 120 L / 6.4 L/100km = 18.75 × 100km = **1,875 km**

**Example entries:**
- Entry 1: Odometer = 45,000 km, Liters = 40 L
- Entry 2: Odometer = 46,875 km, Liters = 40 L (1,875 km later)
- Entry 3: Odometer = 48,750 km, Liters = 40 L (1,875 km later)

**Calculation:**
- Segment 1: 1,875 km using 40 L → (40/1875)×100 = 2.13 L/100km
- Segment 2: 1,875 km using 40 L → (40/1875)×100 = 2.13 L/100km
- **Average: (80/3750)×100 = 2.13 L/100km** ❌ (Not 6.4)

**To get 6.4 L/100km:**
- Entry 1: Odometer = 45,000 km, Liters = 40 L
- Entry 2: Odometer = 45,625 km, Liters = 40 L (625 km later)
- Entry 3: Odometer = 46,250 km, Liters = 40 L (625 km later)

**Calculation:**
- Total fuel: 40 + 40 = 80 L
- Total distance: 625 + 625 = 1,250 km
- **Average: (80/1250)×100 = 6.4 L/100km** ✅

### Testing in Postman

1. **Create a car:**
   ```
   POST http://localhost:8080/api/cars
   Body: {"brand":"Toyota","model":"Corolla","year":2018}
   ```

2. **Add fuel entries with increasing odometer:**
   ```
   POST http://localhost:8080/api/cars/1/fuel
   Body: {"liters":40,"price":52.5,"odometer":45000}
   
   POST http://localhost:8080/api/cars/1/fuel
   Body: {"liters":35,"price":48.0,"odometer":45500}
   
   POST http://localhost:8080/api/cars/1/fuel
   Body: {"liters":42,"price":55.0,"odometer":46000}
   ```

3. **Get fuel stats:**
   ```
   GET http://localhost:8080/api/cars/1/fuel/stats
   ```

   **Expected Response:**
   ```json
   {
     "totalFuel": 117.0,
     "totalCost": 155.5,
     "averageConsumption": 7.5
   }
   ```

### Important Notes

- ⚠️ **Odometer must increase** with each entry
- ⚠️ **At least 2 entries** are required for consumption calculation
- ⚠️ The **last entry's fuel** is not used in calculation (it's for future travel)
- ✅ Consumption is rounded to **1 decimal place**

