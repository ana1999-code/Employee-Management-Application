package com.example.emloyee.management.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/employee_management");
        dataSource.setUsername("admin");
        dataSource.setPassword("admin");
        dataSource.setMinimumIdle(1);
        dataSource.setMaximumPoolSize(1);
        return dataSource;
    }
}

