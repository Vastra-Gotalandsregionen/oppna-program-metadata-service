
package se.vgregion.metaservice.keywordservice.domain;

/**
 * Options
 */
public class Options {

    //Default values?
    private int wordLimit = 5;
    private String[] SourceIds = {"A","C"};

    /**
     * Empty Constructor, uses the default values
     * wordLimit = 5 sourceIds = {"A","C"}
     */
    public Options(){
    }

    /**
     * Constructor
     * @param wordLimit the max number of keywords to return
     * @param SourceIds the sources to use
     */
    public Options(int wordLimit, String[] SourceIds) {
        this.wordLimit = wordLimit;
        this.SourceIds = SourceIds;
    }

    public void setSourceIds(String[] SourceIds) {
        this.SourceIds = SourceIds;
    }

    public void setWordLimit(int wordLimit) {
        this.wordLimit = wordLimit;
    }

    public String[] getSourceIds() {
        return SourceIds;
    }

    public int getWordLimit() {
        return wordLimit;
    }

    

    
    

}
