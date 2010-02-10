
package se.vgregion.metaservice.LemmatisationService;

import java.util.List;
import se.vgregion.metaservice.LemmatisationService.domain.LemmatisedResponse;
import se.vgregion.metaservice.LemmatisationService.exception.InitializationException;
import se.vgregion.metaservice.LemmatisationService.model.FilesystemDictionary;


/**
 * LemmatisationSvc is the interface for all routines exposed by
 * the LemmatisationService-composite-svc project.
 */
public interface LemmatisationSvc {

    /**
     * Lemmatise the parameter word and retrieve all paradigms
     * to the word. This routine uses the default dictionary
     * and thus requires it to be set in advance.
     *
     * @param word The word to lemmatise
     * @return Return the lemmatised word and its paradigms.
     */
    public LemmatisedResponse getParadigmsObject(String word);

    /**
     * Lemmatise the parameter word and retrieve all paradigms
     * to the word. This routine uses dictionary specified by
     * the identifier.
     *
     * @param word The word to lemmatise
     * @param identifier The dictionary identifier
     * @return Return the lemmatised word and its paradigms. 
     */
    public LemmatisedResponse getParadigmsObject(String word, String identifier);

    /**
     * Initializes the lemmatisation service by reading filesystem
     * dictionaries into memory. 
     *
     * @throws InitializationException
     */
    public void init() throws InitializationException;

    public void setDefaultDictionary(String dictionaryIdentifier);
    public String getDefaultDictionary();
    
    public List<FilesystemDictionary> getFilesystemDictionaries();
    public void setFilesystemDictionaries(List<FilesystemDictionary> filesystemDictionaries);

}
