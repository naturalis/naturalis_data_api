package nl.naturalis.bioportal.oaipmh.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openarchives.oai._2.RecordType;

public class CrsDumpDao {

	private static String INSERT_STATEMENT;

	private final Connection conn;
	private final PreparedStatement ps;


	public CrsDumpDao(String dsn, String user, String password) throws ClassNotFoundException, SQLException
	{
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(dsn, user, password);
		if (INSERT_STATEMENT == null) {
			// Generate the SQL for the INSERT statement
			StringBuilder sql = new StringBuilder(1024);
			StringBuilder questionMarks = new StringBuilder(64);
			sql.append("INSERT INTO crs_dump(");
			DatabaseMetaData dbmd = conn.getMetaData();
			ResultSet rs = dbmd.getColumns(null, null, "crs_dump", null);
			boolean first = true;
			while (rs.next()) {
				if (!first) {
					sql.append(',');
					questionMarks.append(',');
				}
				// Append column name
				sql.append('`').append(rs.getString(4)).append('`');
				questionMarks.append('?');
			}
			sql.append(")(").append(questionMarks).append(')');
			INSERT_STATEMENT = sql.toString();
		}
		ps = conn.prepareStatement(INSERT_STATEMENT);
	}


	public void save(RecordType oaiRecord)
	{
		oaiRecord.getMetadata().getAny();
	}

}
