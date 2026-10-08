package com.hms.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		fixDatabaseUrlPermission();
		SpringApplication.run(BackendApplication.class, args);
	}

	private static void fixDatabaseUrlPermission() {
		String dbUrl = System.getenv("SPRING_DATASOURCE_URL");
		if (dbUrl == null || dbUrl.isEmpty()) {
			dbUrl = System.getenv("DATABASE_URL");
		}
		if (dbUrl != null && (dbUrl.startsWith("postgresql://") || dbUrl.startsWith("postgres://"))) {
			String fixed = "jdbc:" + dbUrl;
			System.setProperty("SPRING_DATASOURCE_URL", fixed);
			System.setProperty("spring.datasource.url", fixed);
		}
	}
}
