package se.vgregion.metaservice.keywordservice.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Options
 */
public class Options {

    /** the number of words to base the calculations on in getKeywords*/
    private int inputWords = 10;
    /** the maximum number of words to return on getKeywords */
    private int wordsToReturn = 10;
    /** If set, adds the url as a property in lookupword if a node is created */
    private String url = "";
    /** A map of includeSourceIds to include for each namespace */
    private Map<Integer, String[]> includeSourceIds = null;
    /** Used to filter returned nodes from VocabularyService.getVocabulary by property */
    private Map<String, List<String>> filterByProperties = null;
    /** Used by findNodesByName to match synonyms when searching for a node name */
    private boolean matchSynonyms = false;
    /** Used in addVocabularyNode and moveVocabularyNode to enrich node with synonyms */
    private boolean synonymize = false;

    /**
     * Empty Constructor, uses the default values
     * wincludeSourceIds = {"A", "B", "C"}
     */
    public Options() {
        Map<Integer, String[]> defaultSourceIds = new HashMap<Integer, String[]>();
        defaultSourceIds.put(32769, new String[]{"A", "B", "C"}); //TODO: Correct sweMesh id
        setIncludeSourceIds(defaultSourceIds);
    }

    public void setIncludeSourceIds(Map<Integer, String[]> includeSourceIds) {
        this.includeSourceIds = includeSourceIds;
    }

    public int getInputWords() {
        return inputWords;
    }

    public void setInputWords(int inputWords) {
        this.inputWords = inputWords;
    }

    public int getWordsToReturn() {
        return wordsToReturn;
    }

    public void setWordsToReturn(int wordsToReturn) {
        this.wordsToReturn = wordsToReturn;
    }

    public Map<Integer, String[]> getIncludeSourceIds() {
        return this.includeSourceIds;
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

    public boolean matchSynonyms() {
        return matchSynonyms;
    }

    public void setMatchSynonyms(boolean useSynonyms) {
        this.matchSynonyms = useSynonyms;
    }

    public boolean synonymize() {
        return synonymize;
    }

    public void setSynonymize(boolean synonymize) {
        this.synonymize = synonymize;
    }
}
