package utility.files;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import codingChallenge.Event;

public class DatabaseUtility {
	Logger logger = Logger.getLogger("DatabaseUtility.class");

	public Boolean createEvenTable(Event event) throws SQLException {

		Connection con = null;
		Statement stmt = null;
		int result = 0;

		try {

			Class.forName("org.hsqldb.jdbc.JDBCDriver");
			con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/test", "SA", "");
			stmt = con.createStatement();
			DatabaseMetaData dbm = con.getMetaData();

			// Create table TABLE_EVENT_DETAILS
			ResultSet tables = dbm.getTables(null, null, "TABLE_EVENT_DETAILS", null);
			if (tables.next()) {
				logger.info("Table already exists");
			} else {
				result = stmt.executeUpdate(
						"CREATE TABLE TABLE_EVENT_DETAILS(event_id VARCHAR2(20) NOT NULL, event_state VARCHAR(50) NOT NULL,event_type VARCHAR(20) NOT NULL, event_timestamp INT,event_alert VARCHAR 2(20));");
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return result > 0;
	}

	public Boolean saveEvent(Event event) throws SQLException, ClassNotFoundException {
		createEvenTable(event);
		PreparedStatement statement = null;
		final String sql = "INSERT INTO event (id, duration, type, host, alert)  VALUES (?, ?, ?, ?, ?)";
		Class.forName("org.hsqldb.jdbc.JDBCDriver");
		// String db = "jdbc:hsqldb:hsql://localhost/eventdb;ifexists=true";
		try (Connection con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/test;ifexists=true", "SA",
				"")) {
			statement = con.prepareStatement(sql);
			statement.setString(1, event.getId());
			statement.setLong(2, event.getDuration());
			statement.setString(3, event.getType());
			statement.setString(4, event.getHost());
			statement.setBoolean(5, event.isAlert());
			return statement.executeUpdate() > 0;
		} catch (Exception e) {
			logger.error("Failure saving event, skipping", e);
			return false;
		} finally {
			statement.close();
		}
	}

}