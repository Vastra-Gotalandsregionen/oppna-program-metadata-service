
package se.vgregion.metaservice.keywordservice.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Options
 */
public class Options {

    //Default values?
    private int wordLimit = 10;
    private String url = "";
    private Map<Integer,String[]> includeSourceIds;

    /**
     * Empty Constructor, uses the default values
     * wordLimit = 5 includeSourceIds = {"A","C"}
     */
    public Options(){
        Map<Integer, String[]> defaultSourceIds = new HashMap<Integer,String[]>();
        defaultSourceIds.put(32769, new String[]{"A","B","C"}); //TODO: Correct sweMesh id
        setIncludeSourceIds(defaultSourceIds);
    }

    /**
     * Constructor
     * @param wordLimit the max number of keywords to return
     * @param SourceIds a map of the includeSourceIds to include for each namespace
     */
    public Options(int wordLimit, Map<Integer,String[]> sourceIds) {
        setWordLimit(wordLimit);
        setIncludeSourceIds(sourceIds);
    }

    private void setIncludeSourceIds(Map<Integer,String[]> includeSourceIds) {
        this.includeSourceIds = includeSourceIds;
    }

    private void setWordLimit(int wordLimit) {
        this.wordLimit = wordLimit;
    }

    public Map<Integer,String[]> getIncludeSourceIds() {
        return this.includeSourceIds;
    }

    public int getWordLimit() {
        return wordLimit;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}