package edu.hw1.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import edu.hw1.config.DatabaseConfig;

public final class ConnectionFactory {
    private ConnectionFactory() {
    }

    public static Connection open() throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.jdbcUrl(),
                DatabaseConfig.username(),
                DatabaseConfig.password());
    }
}
