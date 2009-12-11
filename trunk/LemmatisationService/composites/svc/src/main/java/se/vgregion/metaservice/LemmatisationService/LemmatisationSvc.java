
package se.vgregion.metaservice.LemmatisationService;

import se.vgregion.metaservice.LemmatisationService.domain.LemmatisedResponse;


/**
 * LemmatisationSvc is the interface for all routines exposed by
 * the LemmatisationService-composite-svc project.
 */
public interface LemmatisationSvc {

    /**
     * Lemmatise the parameter word and retrieve all paradigms
     * to the word. 
     *
     * @return Return the lemmatised word and its paradigms.
     */
    public LemmatisedResponse getParadigmsObject(String word);


    
}
