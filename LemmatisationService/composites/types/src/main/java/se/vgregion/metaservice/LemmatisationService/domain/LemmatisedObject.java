/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.vgregion.metaservice.LemmatisationService.domain;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author sture.svensson
 */
public class LemmatisedObject {

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
