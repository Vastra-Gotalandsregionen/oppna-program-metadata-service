
package se.vgregion.metaservice.keywordservice.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Options
 */
public class Options {

    //Default values?
    private int wordLimit = 5;
    private Map<Integer,String[]> sourceIds;

    /**
     * Empty Constructor, uses the default values
     * wordLimit = 5 sourceIds = {"A","C"}
     */
    public Options(){
        Map<Integer, String[]> defaultSourceIds = new HashMap<Integer,String[]>();
        defaultSourceIds.put(123, new String[]{"A","C"}); //TODO: Correct sweMesh id
    }

    /**
     * Constructor
     * @param wordLimit the max number of keywords to return
     * @param SourceIds a map of the sourceIds to include for each namespace
     */
    public Options(int wordLimit, Map<Integer,String[]> sourceIds) {
        setWordLimit(wordLimit);
        setSourceIds(sourceIds);
    }

    private void setSourceIds(Map<Integer,String[]> sourceIds) {
        this.sourceIds = sourceIds;
    }

    private void setWordLimit(int wordLimit) {
        this.wordLimit = wordLimit;
    }

    public Map<Integer,String[]> getSourceIds() {
        return sourceIds;
    }

    public int getWordLimit() {
        return wordLimit;
    }
}