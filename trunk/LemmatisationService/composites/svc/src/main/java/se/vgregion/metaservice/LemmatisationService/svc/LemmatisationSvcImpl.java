

package se.vgregion.metaservice.LemmatisationService.svc;

import java.util.LinkedList;
import java.util.List;
import se.vgregion.metaservice.LemmatisationService.LemmatisationSvc;
import se.vgregion.metaservice.LemmatisationService.domain.LemmatisedResponse;


public class LemmatisationSvcImpl implements LemmatisationSvc {

    public LemmatisedResponse getParadigmsObject(String word) {
        LemmatisedResponse response = new LemmatisedResponse();
        response.setOriginalWord(word);
        List<String> paradigms = new LinkedList<String>();

        paradigms.add("apan");
        paradigms.add("apans");
        paradigms.add("apor");
        paradigms.add("aporna");
        paradigms.add("apornas");

        response.setLemma("apa");
        response.setParadigms(paradigms);

        return response;
    }

}
