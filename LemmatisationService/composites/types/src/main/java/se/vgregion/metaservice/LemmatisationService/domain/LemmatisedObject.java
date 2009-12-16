
package se.vgregion.metaservice.LemmatisationService.domain;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * LemmatisedObject
 */
@XmlRootElement
public class LemmatisedObject implements Serializable {
    private String lemma = null;
    private List<String> paradigms = null;

    public LemmatisedObject() {
        paradigms = new LinkedList<String>();
    }

    public LemmatisedObject(String lemma, List<String> paradigms) {
        this.lemma = lemma;
        this.paradigms = paradigms;
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


}
