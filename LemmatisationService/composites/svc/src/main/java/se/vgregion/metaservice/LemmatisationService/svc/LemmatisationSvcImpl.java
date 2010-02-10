package se.vgregion.metaservice.LemmatisationService.svc;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import se.vgregion.metaservice.LemmatisationService.LemmatisationSvc;
import se.vgregion.metaservice.LemmatisationService.domain.LemmatisedObject;
import se.vgregion.metaservice.LemmatisationService.domain.LemmatisedResponse;
import se.vgregion.metaservice.LemmatisationService.exception.InitializationException;
import se.vgregion.metaservice.LemmatisationService.model.Dictionary;
import se.vgregion.metaservice.LemmatisationService.model.FilesystemDictionary;

/**
 * @author johan.sjoberg
 */
public class LemmatisationSvcImpl implements LemmatisationSvc {
    private List<FilesystemDictionary> filesystemDictionaries;
    private Map<String, Dictionary> dictionaries;
    private String defaultDictionary = null;
    private boolean initialized = false;

    public void init() throws InitializationException {
        dictionaries = new HashMap<String, Dictionary>();
        String location = null;
        File file;
        URL url;

        try {
            for (FilesystemDictionary w : filesystemDictionaries) {
                // Parse each file as either a jar-resource file or
                // as an external filesystem path.

                location = w.getLocation();
                url = getClass().getResource(location);
                file = (url == null) ? new File(location) : new File(url.toURI());
                dictionaries.put(w.getIdentifier(), new Dictionary(file));
            }
            
            initialized = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InitializationException("Error locating dictionary at location: " + location);
        }
    }

    public LemmatisedResponse getParadigmsObject(String word) {
        return getParadigmsObject(word, defaultDictionary);
    }

    public LemmatisedResponse getParadigmsObject(String word, String identifier) {
        LemmatisedResponse response = new LemmatisedResponse();
        response.setStatusCode(LemmatisedResponse.StatusCode.ok);
        response.setOriginalWord(word);

        if (!initialized) {
            response.setStatusCode(LemmatisedResponse.StatusCode.error);
            response.setErrorMessage("Lemmatisation service is not initialized");
            return response;
        }
        
        Dictionary dictionary = dictionaries.get(identifier);
        if (dictionary == null) {
            response.setStatusCode(LemmatisedResponse.StatusCode.error);
            response.setErrorMessage("Dictionary not initialized");
            return response;
        }

        List<List<String>> wordList = dictionary.getWords(word);
        if (wordList.isEmpty()) {
            response.setStatusCode(LemmatisedResponse.StatusCode.no_word_found);
            response.setErrorMessage("No words matched the query");
            return response;
        }

        List<LemmatisedObject> resultList = new LinkedList<LemmatisedObject>();
        for (List<String> list : wordList) {
            LemmatisedObject obj = new LemmatisedObject();
            obj.setLemma(list.get(0));
            list.remove(0);
            obj.setParadigms(list);
            resultList.add(obj);
        }

        response.setList(resultList);
        return response;
    }

    public String getDefaultDictionary() {
        return defaultDictionary;
    }

    public void setDefaultDictionary(String defaultDictionary) {
        this.defaultDictionary = defaultDictionary;
    }

    public List<FilesystemDictionary> getFilesystemDictionaries() {
        return filesystemDictionaries;
    }

    public void setFilesystemDictionaries(List<FilesystemDictionary> filesystemDictionaries) {
        this.filesystemDictionaries = filesystemDictionaries;
    }
}
