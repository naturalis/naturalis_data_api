package nl.naturalis.nda.elasticsearch.load.crs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;

public class CrsGbifIptImporter extends AbstractSpecimenImporter {

	private static final String SQL_INSERT = "INSERT INTO `%s`.`%s` (`crs_identifier`, "
			+ "`catalognumber`, `collectioncode`, `institutioncode`, `scientificname`, `scientificnameauthorship`, "
			+ "`verbatimtaxonrank`, `genus`, `subgenus`, `specificepithet`, `infraspecificepithet`, "
			+ "`nomenclaturalcode`, `individualcount`, `basisofrecord`, `stateprovince`, `country`, `continent`, "
			+ "`county`, `locality`, `decimallatitude`, `decimallongitude`, `geodeticdatum`, `verbatimdepth`, "
			+ "`verbatimelevation`, `sex`, `typestatus`, `lifestage`, `habitat`, `description`, `eventdate`, "
			+ "`verbatimeventdate`, `identifiedby`, `dateidentified`, `recordedby`, `informationwithheld`, "
			+ "`preparations`) VALUES (0, :catalognumber, :collectioncode, :institutioncode, :scientificname, "
			+ ":scientificnameauthorship, :verbatimtaxonrank, :genus, :subgenus, :specificepithet, :infraspecificepithet, "
			+ ":nomenclaturalcode, :individualcount, :basisofrecord, :stateprovince, :country, :continent, :county, "
			+ ":locality, :decimallatitude, :decimallongitude, :geodeticdatum, :verbatimdepth, :verbatimelevation, "
			+ ":sex, :typestatus, :lifestage, :habitat, :description, :eventdate, :verbatimeventdate, :identifiedby, "
			+ ":dateidentified, :recordedby, :informationwithheld, :preparations)";

	private final PreparedStatement insertStatement;


	public CrsGbifIptImporter() throws Exception
	{
		super();
		String dbUser = System.getProperty("dbUser");
		String dbPassword = System.getProperty("dbPassword");
		String dbName = System.getProperty("dbName");
		String dbTable = System.getProperty("dbTable");
		Connection conn;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/" + dbName, dbUser, dbPassword);
		}
		catch (SQLException e) {
			String error = String.format("Could not connect using dbUser=%s;dbPassword=%s;dbName=%s", dbUser, dbPassword, dbName);
			throw new Exception(error);
		}
		String sqlInsert = String.format(SQL_INSERT, dbName, dbTable);
		insertStatement = conn.prepareStatement(sqlInsert);
	}


	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}


	@Override
	protected void saveSpecimens(List<ESSpecimen> specimens, List<String> ids)
	{
		for (int i = 0; i < specimens.size(); ++i) {
			//insertStatement.setSt
		}
	}


	@Override
	protected void deleteSpecimen(String databaseId)
	{
		// TODO Auto-generated method stub

	}

}
