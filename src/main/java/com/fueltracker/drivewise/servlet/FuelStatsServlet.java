package com.fueltracker.drivewise.servlet;

import com.fueltracker.drivewise.model.FuelStats;
import com.fueltracker.drivewise.service.CarService;
import com.fueltracker.drivewise.service.FuelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import java.io.PrintWriter;

public class FuelStatsServlet extends HttpServlet {
    
    private CarService carService;
    private FuelService fuelService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void setCarService(CarService carService) {
        this.carService = carService;
    }

    public void setFuelService(FuelService fuelService) {
        this.fuelService = fuelService;
    }

    private void ensureServicesInitialized(HttpServletRequest request) {
        if (carService == null || fuelService == null) {
            WebApplicationContext context = WebApplicationContextUtils
                    .getWebApplicationContext(request.getServletContext());
            if (context != null) {
                if (carService == null) {
                    carService = context.getBean(CarService.class);
                }
                if (fuelService == null) {
                    fuelService = context.getBean(FuelService.class);
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        try {
            // Ensure services are initialized (fallback to ApplicationContext if needed)
            ensureServicesInitialized(request);
            
            // Check if services are initialized
            if (carService == null || fuelService == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\":\"Service not initialized\"}");
                out.flush();
                return;
            }
            
            // Manually parse carId from query parameters
            String carIdParam = request.getParameter("carId");
            
            if (carIdParam == null || carIdParam.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"carId parameter is required\"}");
                out.flush();
                return;
            }

            Long carId = Long.parseLong(carIdParam);
            
            // Check if car exists
            if (!carService.carExists(carId)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Car not found\"}");
                out.flush();
                return;
            }
            
            // Get fuel stats using the same service layer
            FuelStats stats = fuelService.calculateStats(carId);
            
            // Set status code explicitly
            response.setStatus(HttpServletResponse.SC_OK);
            
            // Write JSON response
            String json = objectMapper.writeValueAsString(stats);
            out.print(json);
            out.flush();
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Invalid carId format\"}");
            out.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Internal server error: " + e.getMessage() + "\"}");
            out.flush();
        }
    }
}

