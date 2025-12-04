package com.academic.AIS;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class AisApplication {

	public static void main(String[] args) {
		SpringApplication.run(AisApplication.class, args);

	}

}
