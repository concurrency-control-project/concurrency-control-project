package com.example.concurrencycontrolproject.schedule;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.test.context.TestConfiguration;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@TestConfiguration
public class H2DateAliasConfig {

	private final DataSource dataSource;

	public H2DateAliasConfig(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@PostConstruct
	public void registerDateAlias() throws Exception {
		try (Connection conn = dataSource.getConnection();
			 Statement stmt = conn.createStatement()) {
			stmt.execute("CREATE ALIAS IF NOT EXISTS DATE FOR \"java.sql.Date.valueOf\"");
		}
	}
}
