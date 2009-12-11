

package se.vgregion.metaservice.LemmatisationService.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.LinkedList;
import java.util.List;


@XStreamAlias("lemmatisedresponse")
public class LemmatisedResponse {
    private String lemma = null;
    private String originalWord = null;
    private List<String> paradigms = null;

    public LemmatisedResponse() {
        paradigms = new LinkedList<String>();
    }

    public LemmatisedResponse(String originalWord, String lemma, List<String> paradigms) {
        this.originalWord = originalWord;
        this.lemma = lemma;
        this.paradigms = paradigms;
    }

    public boolean lookupSuccessful() {
        return lemma != null;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public List<String> getParadigms() {
        return paradigms;
    }

    public void setParadigms(List<String> paradigms) {
        this.paradigms = paradigms;
    }

    public String getOriginalWord() {
        return originalWord;
    }

    public void setOriginalWord(String originalWord) {
        this.originalWord = originalWord;
    }

}
