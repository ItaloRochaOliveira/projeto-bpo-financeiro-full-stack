package com.italo.geradorboleto.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @PostConstruct
    public void printDatabaseConfig() {
        System.out.println("=== Database Configuration Resolved ===");
        System.out.println("Datasource URL: " + datasourceUrl);
        System.out.println("Username: " + datasourceUsername);
        System.out.println("Password: " + datasourcePassword);
        System.out.println("Driver: " + driverClassName);
        System.out.println("========================================");
    }
}
