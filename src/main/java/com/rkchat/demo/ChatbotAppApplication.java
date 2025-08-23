package com.rkchat.demo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatbotAppApplication {

	public static void main(String[] args) {
		System.out.println("=== ENVIRONMENT VARIABLES (BEFORE SPRING) ===");
		System.out.println("URL: " + System.getenv("SPRING_DATASOURCE_URL"));
		System.out.println("Username: " + System.getenv("SPRING_DATASOURCE_USERNAME"));
		System.out.println("Password: " + (System.getenv("SPRING_DATASOURCE_PASSWORD") != null ? "SET (length: " + System.getenv("SPRING_DATASOURCE_PASSWORD").length() + ")" : "NOT SET"));
		System.out.println("===============================================");

		testDirectConnection();


		SpringApplication.run(ChatbotAppApplication.class, args);
	}
	private static void testDirectConnection() {
		String url = System.getenv("SPRING_DATASOURCE_URL");
		String username = System.getenv("SPRING_DATASOURCE_USERNAME");
		String password = System.getenv("SPRING_DATASOURCE_PASSWORD");

		System.out.println("=== TESTING DIRECT JDBC CONNECTION ===");

		try {
			// Load PostgreSQL driver
			Class.forName("org.postgresql.Driver");

			System.out.println("Driver loaded successfully");
			System.out.println("Attempting connection to: " + url);

			// Try to connect
			Connection conn = DriverManager.getConnection(url, username, password);
			System.out.println("✅ DIRECT CONNECTION SUCCESSFUL!");

			// Test a simple query
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT version()");
			if (rs.next()) {
				System.out.println("PostgreSQL Version: " + rs.getString(1));
			}

			conn.close();
			System.out.println("Connection closed successfully");

		} catch (Exception e) {
			System.out.println("❌ DIRECT CONNECTION FAILED:");
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();

			// Additional debugging for common issues
			if (e.getMessage().contains("timeout")) {
				System.out.println("\n🔍 TIMEOUT ISSUE - Check:");
				System.out.println("- RDS Security Group allows your IP on port 5432");
				System.out.println("- RDS is 'Publicly Accessible'");
				System.out.println("- Your firewall isn't blocking outbound connections");
			} else if (e.getMessage().contains("authentication")) {
				System.out.println("\n🔍 AUTHENTICATION ISSUE - Check:");
				System.out.println("- Username and password are correct");
				System.out.println("- Database name exists");
			}
		}

		System.out.println("======================================");
	}

}