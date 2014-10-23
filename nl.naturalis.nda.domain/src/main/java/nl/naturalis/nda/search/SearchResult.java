package nl.naturalis.nda.search;

import java.util.ArrayList;
import java.util.List;

public class SearchResult<T> {

    private float score;
    private String actualType;
    private List<Link> links;

    // TODO: this was changed from commented version to get BioportalDaos compiling. Maybe change back.
    // private List<MatchInfo<?>> matchInfo;
    private List<StringMatchInfo> matchInfo;

    private List<NameResolutionInfo> nameResolutionInfo;
    private T result;


    public SearchResult() {
    }


    public SearchResult(T result) {
        this.result = result;
    }

    /**
     * URL encodes the href. In case that fails, uses original href value.
     * @param rel
     * @param href not encoded !
     */
    public void addLink(String rel, String href) {
        if (links == null) {
            links = new ArrayList<>();
        }
        links.add(new Link(rel, href));
    }


    public void addLink(Link link) {
        if (links == null) {
            links = new ArrayList<>();
        }
        links.add(link);
    }


    public void addMatchInfo(StringMatchInfo matchInfo) {
        if (this.matchInfo == null) {
            this.matchInfo = new ArrayList<>();
        }
        this.matchInfo.add(matchInfo);
    }


    public float getScore() {
        return score;
    }


    public void setScore(float score) {
        this.score = score;
    }


    public String getActualType() {
        return actualType;
    }


    public void setActualType(String actualType) {
        this.actualType = actualType;
    }


    public List<StringMatchInfo> getMatchInfo() {
        return matchInfo;
    }


    public void setMatchInfo(List<StringMatchInfo> matchInfo) {
        this.matchInfo = matchInfo;
    }


    public T getResult() {
        return result;
    }


    public void setResult(T result) {
        this.result = result;
    }


    public List<Link> getLinks() {
        return links;
    }


    public void setLinks(List<Link> links) {
        this.links = links;
    }


    public List<NameResolutionInfo> getNameResolutionInfo() {
        return nameResolutionInfo;
    }


    public void setNameResolutionInfo(List<NameResolutionInfo> nameResolutionInfo) {
        this.nameResolutionInfo = nameResolutionInfo;
    }
}
