
package se.vgregion.metaservice.LemmatisationService;

import se.vgregion.metaservice.LemmatisationService.domain.LemmatisedResponse;
import se.vgregion.metaservice.LemmatisationService.exception.InitializationException;


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

    /**
     * Initialize the lemmatisation service with the default
     * wordlist (saldo.txt).
     *
     * @throws InitializationException
     */
    public void init() throws InitializationException;

    public String getWordlistLocation();
    public void setWordlistLocation(String wordlistLocation);

}
