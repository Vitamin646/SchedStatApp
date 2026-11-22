package io.github.vitamin646dev.schedstatapp.db;

import org.flywaydb.core.Flyway;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Access to the SQLite database file. Opening it applies pending Flyway migrations.
 * Every {@link #connection()} call returns a new connection that the caller must close.
 */
public final class Database {

	private static final String MIGRATIONS_LOCATION = "classpath:io/github/vitamin646dev/schedstatapp/db/migration";

	private final String url;

	private Database(String url) {
		this.url = url;
	}

	/**
	 * Default location: {@code ~/.schedstatapp/schedstat.db}.
	 */
	public static Path defaultFile() {
		return Path.of(System.getProperty("user.home"), ".schedstatapp", "schedstat.db");
	}

	/**
	 * Opens (creating if needed) the database file and migrates it to the latest schema.
	 */
	public static Database open(Path file) {
		Path absolute = file.toAbsolutePath();
		try {
			Path parent = absolute.getParent();
			if (parent != null) {
				Files.createDirectories(parent);
			}
		} catch (IOException e) {
			throw new DataAccessException("Cannot create database directory for " + absolute, e);
		}

		Database database = new Database("jdbc:sqlite:" + absolute + "?foreign_keys=on");
		database.migrate();
		return database;
	}

	public Connection connection() {
		try {
			return DriverManager.getConnection(url);
		} catch (SQLException e) {
			throw new DataAccessException("Cannot open database connection", e);
		}
	}

	private void migrate() {
		Flyway.configure()
				.dataSource(url, null, null)
				.locations(MIGRATIONS_LOCATION)
				.load()
				.migrate();
	}
}
