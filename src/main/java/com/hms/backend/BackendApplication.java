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
		String rawUrl = System.getenv("SPRING_DATASOURCE_URL");
		if (rawUrl == null || rawUrl.isEmpty()) {
			rawUrl = System.getenv("DATABASE_URL");
		}

		if (rawUrl != null && !rawUrl.isEmpty()) {
			parseAndSetJdbcUrl(rawUrl);
		}
	}

	public static void parseAndSetJdbcUrl(String rawUrl) {
		try {
			String cleanUrl = rawUrl;
			if (cleanUrl.startsWith("jdbc:")) {
				cleanUrl = cleanUrl.substring(5);
			}
			if (cleanUrl.startsWith("postgresql://")) {
				cleanUrl = cleanUrl.substring(13);
			} else if (cleanUrl.startsWith("postgres://")) {
				cleanUrl = cleanUrl.substring(11);
			}

			if (cleanUrl.contains("@")) {
				String[] parts = cleanUrl.split("@");
				String userPass = parts[0];
				String hostDb = parts[1];

				if (userPass.contains(":")) {
					String[] creds = userPass.split(":", 2);
					System.setProperty("spring.datasource.username", creds[0]);
					System.setProperty("spring.datasource.password", creds[1]);
					System.setProperty("SPRING_DATASOURCE_USERNAME", creds[0]);
					System.setProperty("SPRING_DATASOURCE_PASSWORD", creds[1]);
				}

				String hostPortDb = hostDb;
				if (!hostDb.contains(":") && hostDb.contains("/")) {
					String[] hostAndDb = hostDb.split("/", 2);
					hostPortDb = hostAndDb[0] + ":5432/" + hostAndDb[1];
				}

				String finalJdbcUrl = "jdbc:postgresql://" + hostPortDb;
				System.setProperty("spring.datasource.url", finalJdbcUrl);
				System.setProperty("SPRING_DATASOURCE_URL", finalJdbcUrl);
			} else {
				String finalJdbcUrl = cleanUrl.startsWith("jdbc:postgresql://") ? cleanUrl : "jdbc:postgresql://" + cleanUrl;
				System.setProperty("spring.datasource.url", finalJdbcUrl);
				System.setProperty("SPRING_DATASOURCE_URL", finalJdbcUrl);
			}
		} catch (Exception e) {
			System.err.println("Error parsing database URL: " + e.getMessage());
		}
	}
}
