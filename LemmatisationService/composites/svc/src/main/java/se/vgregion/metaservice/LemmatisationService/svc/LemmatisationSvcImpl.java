package se.vgregion.metaservice.LemmatisationService.svc;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import se.vgregion.metaservice.LemmatisationService.LemmatisationSvc;
import se.vgregion.metaservice.LemmatisationService.domain.LemmatisedObject;
import se.vgregion.metaservice.LemmatisationService.domain.LemmatisedResponse;
import se.vgregion.metaservice.LemmatisationService.exception.InitializationException;
import se.vgregion.metaservice.LemmatisationService.model.Dictionary;

public class LemmatisationSvcImpl implements LemmatisationSvc {
    private static final String DEFAULT_WORDLISTLOCATION = "/saldo.txt";
    private String wordlistLocation = null;
    private Dictionary dictionary;
    private boolean initialized;

    public void init() throws InitializationException {
        if (wordlistLocation == null) wordlistLocation = DEFAULT_WORDLISTLOCATION;
        URL url = getClass().getResource(wordlistLocation);

        try {
            dictionary = new Dictionary(new File(url.toURI()));
        } catch (Exception ex) {
            throw new InitializationException("Error locating wordlist at location '" + wordlistLocation + "'");
        }

        initialized = true;
    }

    public LemmatisedResponse getParadigmsObject(String word) {
        LemmatisedResponse response = new LemmatisedResponse();
        response.setStatusCode(LemmatisedResponse.StatusCode.ok);
        response.setOriginalWord(word);

        if (!initialized) {
            response.setStatusCode(LemmatisedResponse.StatusCode.error);
            response.setErrorMessage("Lemmatisation service is not initialized");
            return response;
        }

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

    public String getWordlistLocation() {
        return wordlistLocation;
    }

    public void setWordlistLocation(String wordlistLocation) {
        this.wordlistLocation = wordlistLocation;
    }
}
