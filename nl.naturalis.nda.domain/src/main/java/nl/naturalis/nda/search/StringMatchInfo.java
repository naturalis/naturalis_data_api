package nl.naturalis.nda.search;

public class StringMatchInfo extends MatchInfo<String> {

	private String valueHighlighted;


	public String getValueHighlighted()
	{
		return valueHighlighted;
	}


	public void setValueHighlighted(String valueHighlighted)
	{
		this.valueHighlighted = valueHighlighted;
	}

}
