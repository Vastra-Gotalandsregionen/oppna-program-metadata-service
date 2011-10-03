
package se.vgregion.metaservice.keywordservice.web;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author torsten.landergren
 */
public class Keyword {
    private String keyword = "";
    private List<String> synonyms = new ArrayList<String>();

    public Keyword(String keyword, List<String> synonyms) {
        this.keyword = keyword;
        this.synonyms = synonyms;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSynonyms() {
        return StringUtils.join(synonyms, ", ");
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

}
