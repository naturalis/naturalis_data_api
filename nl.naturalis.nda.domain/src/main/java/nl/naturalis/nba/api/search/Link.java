package nl.naturalis.nba.api.search;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Link {

	private String rel;
	private String href;

    /**
     * URL encodes the href. In case that fails, uses original href value.
     * @param rel
     * @param href not encoded !
     */
	public Link(String rel, String href) {
		this.rel = rel;
        try {
            this.href = URLEncoder.encode(href, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            this.href = href;
        }
	}


	public String getRel()
	{
		return rel;
	}


	public void setRel(String rel)
	{
		this.rel = rel;
	}


	public String getHref() {
		return href;
	}

    /**
     * URL encodes the href. In case that fails, uses original href value.
     * @param href not encoded !
     */
	public void setHref(String href) {
        try {
            this.href = URLEncoder.encode(href, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            this.href = href;
        }
    }

}
