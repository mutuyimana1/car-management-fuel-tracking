package com.fueltracker.drivewise.config;

import com.fueltracker.drivewise.servlet.FuelStatsServlet;
import com.fueltracker.drivewise.service.CarService;
import com.fueltracker.drivewise.service.FuelService;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean<FuelStatsServlet> fuelStatsServlet(
            CarService carService,
            FuelService fuelService) {
        FuelStatsServlet servlet = new FuelStatsServlet();
        servlet.setCarService(carService);
        servlet.setFuelService(fuelService);

        ServletRegistrationBean<FuelStatsServlet> registrationBean = new ServletRegistrationBean<>(servlet,
                "/servlet/fuel-stats");
        registrationBean.setName("FuelStatsServlet");
        return registrationBean;
    }
}
