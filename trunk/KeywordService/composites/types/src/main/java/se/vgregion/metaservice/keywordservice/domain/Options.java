
package se.vgregion.metaservice.keywordservice.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Options
 */
public class Options {
    /** Max number of keywords to return */
    private int wordLimit = 10;

    /** ? */
    private String url = "";

    /** A map of includeSourceIds to include for each namespace */
    private Map<Integer, String[]> includeSourceIds = null;
    
    /** Used to filter returned nodes from VocabularyService.getVocabulary by property */
    private Map<String, List<String>> filterByProperties = null;

    /** Used by findNodesByName*/
    private Boolean useSynonyms;

    /**
     * Empty Constructor, uses the default values
     * wordLimit = 10, includeSourceIds = {"A", "B", "C"}
     */
    public Options(){
        Map<Integer, String[]> defaultSourceIds = new HashMap<Integer,String[]>();
        defaultSourceIds.put(32769, new String[]{"A","B","C"}); //TODO: Correct sweMesh id
        setIncludeSourceIds(defaultSourceIds);
    }

    public void setIncludeSourceIds(Map<Integer,String[]> includeSourceIds) {
        this.includeSourceIds = includeSourceIds;
    }

    public void setWordLimit(int wordLimit) {
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

    public Map<String, List<String>> getFilterByProperties() {
        return filterByProperties;
    }

    public void setFilterByProperties(Map<String, List<String>> filterProperties) {
        this.filterByProperties = filterProperties;
    }

    public Boolean getUseSynonyms() {
        return useSynonyms;
    }

    public void setUseSynonyms(Boolean useSynonyms) {
        this.useSynonyms = useSynonyms;
    }


}