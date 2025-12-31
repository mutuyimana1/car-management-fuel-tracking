# Postman Guide for Adding Fuel Entries

## Step-by-Step Instructions

### Step 1: Create a Car First

**Request:**
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/cars`
- **Headers:**
  - `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "brand": "Toyota",
  "model": "Corolla",
  "year": 2018
}
```

**Expected Response (201):**
```json
{
  "id": 1,
  "brand": "Toyota",
  "model": "Corolla",
  "year": 2018,
  "fuelEntries": []
}
```

**⚠️ IMPORTANT:** Note the `id` from the response (e.g., `1`)

---

### Step 2: Add Fuel Entry

**Request:**
- **Method:** `POST` ⚠️ (NOT GET!)
- **URL:** `http://localhost:8080/api/cars/1/fuel`
  - Replace `1` with the actual car ID from Step 1
- **Headers:**
  - `Content-Type: application/json` ⚠️ (This is REQUIRED!)
- **Body (raw JSON):**
```json
{
  "liters": 40.0,
  "price": 52.5,
  "odometer": 45000
}
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "carId": 1,
  "liters": 40.0,
  "price": 52.5,
  "odometer": 45000
}
```

---

## Common Issues & Solutions

### Issue 1: Getting 404 Not Found

**Possible Causes:**
1. ❌ **Car doesn't exist** - Make sure you created a car first and are using the correct ID
2. ❌ **Wrong HTTP method** - Must be `POST`, not `GET`
3. ❌ **Missing Content-Type header** - Must include `Content-Type: application/json`
4. ❌ **URL typo** - Check for typos in the URL

**Solution:**
1. First, verify the car exists:
   - `GET http://localhost:8080/api/cars` - Lists all cars
   - `GET http://localhost:8080/api/cars/1` - Gets car with ID 1
2. If car doesn't exist, create it first (Step 1)
3. Make sure you're using `POST` method
4. Make sure `Content-Type: application/json` header is set

### Issue 2: Getting 404 even though car exists

**Check:**
- Is the server running? Test: `GET http://localhost:8080/api/cars`
- Did you restart the server after code changes?
- Is the URL exactly: `http://localhost:8080/api/cars/{id}/fuel` (no trailing slash)

### Issue 3: Body format issues

**Correct format:**
```json
{
  "liters": 40.0,
  "price": 52.5,
  "odometer": 45000
}
```

**Common mistakes:**
- ❌ Extra commas
- ❌ Missing quotes around keys
- ❌ Wrong data types (strings instead of numbers)

---

## Postman Setup Checklist

- [ ] Method is set to **POST**
- [ ] URL is: `http://localhost:8080/api/cars/{id}/fuel` (replace {id})
- [ ] Headers tab has: `Content-Type: application/json`
- [ ] Body tab is set to **raw** and **JSON**
- [ ] Body contains valid JSON with `liters`, `price`, and `odometer`
- [ ] Server is running on port 8080
- [ ] Car with the specified ID exists

---

## Quick Test Sequence

1. **Create car:**
   ```
   POST http://localhost:8080/api/cars
   Body: {"brand":"Toyota","model":"Corolla","year":2018}
   ```

2. **Verify car exists:**
   ```
   GET http://localhost:8080/api/cars/1
   ```

3. **Add fuel:**
   ```
   POST http://localhost:8080/api/cars/1/fuel
   Body: {"liters":40.0,"price":52.5,"odometer":45000}
   ```

4. **Verify fuel was added:**
   ```
   GET http://localhost:8080/api/cars/1
   ```
   (Should show fuelEntries array with the entry)


