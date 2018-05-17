package com.stormphoenix.graduatedesign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TrajectorMapApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrajectorMapApiApplication.class, args);
	}
}
