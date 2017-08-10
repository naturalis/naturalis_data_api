package nl.naturalis.nba.etl;

import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.utils.ArrayUtil;
import nl.naturalis.nba.utils.convert.Stringifier;

/**
 * Utility class that deletes all documents from an index, but leaves the index
 * intact. Alternatively you can choose to delete only documents belong to a
 * certain {@link SourceSystem}.
 * 
 * @author Ayco Holleman
 *
 */
public class NbaTruncate {

	public static void main(String[] args)
	{
		try {
			if (args.length == 2) {
				DocumentType<?> dt = DocumentType.forName(args[0]);
				SourceSystem ss = SourceSystem.getInstance(args[1].toUpperCase(), null);
				ETLUtil.truncate(dt, ss);
				System.exit(0);
			}
			if (args.length == 1) {
				DocumentType<?> dt = DocumentType.forName(args[0]);
				ETLUtil.truncate(dt);
				System.exit(0);
			}
		}
		catch (Throwable t) {
			System.err.println(t.getMessage());
		}
		error();
		System.exit(1);
	}

	private static void error()
	{
		String docTypes = ArrayUtil.implode(DocumentType.getAllDocumentTypes(),
				new Stringifier<DocumentType<?>>() {

					@Override
					public String execute(DocumentType<?> obj, Object... conversionArguments)
					{
						return " " + obj.getName();
					}

				});
		String sourceSystems = ArrayUtil.implode(SourceSystem.getAllSourceSystems(),
				new Stringifier<SourceSystem>() {

					@Override
					public String execute(SourceSystem obj, Object... conversionArguments)
					{
						return " " + obj.getCode();

					}
				});
		System.err.println("USAGE: truncate <document_type> [<source_system>]");
		System.err.println("       document types: " + docTypes.trim());
		System.err.println("       source systems: " + sourceSystems.trim());
	}

}
