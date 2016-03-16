package nl.naturalis.nba.elasticsearch.schema;

class Fields {

	static final Fields DEFAULT = new Fields();

	private final Raw raw;

	Fields()
	{
		this.raw = Raw.DEFAULT;
	}

	Raw getRaw()
	{
		return raw;
	}

}
