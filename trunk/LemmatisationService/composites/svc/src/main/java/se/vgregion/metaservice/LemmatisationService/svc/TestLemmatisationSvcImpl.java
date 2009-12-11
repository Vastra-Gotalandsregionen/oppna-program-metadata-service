

package se.vgregion.metaservice.LemmatisationService.svc;

import java.util.LinkedList;
import java.util.List;
import se.vgregion.metaservice.LemmatisationService.LemmatisationSvc;


public class TestLemmatisationSvcImpl implements LemmatisationSvc {

    public List<String> getSynonyms() {
        List<String> synonyms = new LinkedList<String>();
        synonyms.add("apa");
        synonyms.add("apan");
        synonyms.add("apans");
        synonyms.add("apor");
        synonyms.add("apornas");
        return synonyms;
    }

}
