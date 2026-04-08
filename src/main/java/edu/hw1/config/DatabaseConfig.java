package edu.hw1.config;

public final class DatabaseConfig {
    private DatabaseConfig() {
    }

    public static String jdbcUrl() {
        return System.getenv().getOrDefault("HISTORYSHELF_DB_URL", "jdbc:postgresql://localhost:5432/historyshelf");
    }

    public static String username() {
        return System.getenv().getOrDefault("HISTORYSHELF_DB_USER", "postgres");
    }

    public static String password() {
        return System.getenv().getOrDefault("HISTORYSHELF_DB_PASSWORD", "postgres");
    }
}
