package se.vgregion.metaservice.LemmatisationService.svc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import se.vgregion.metaservice.LemmatisationService.LemmatisationSvc;
import se.vgregion.metaservice.LemmatisationService.domain.LemmatisedObject;
import se.vgregion.metaservice.LemmatisationService.domain.LemmatisedResponse;
import se.vgregion.metaservice.LemmatisationService.exception.InitializationException;
import se.vgregion.metaservice.LemmatisationService.model.Dictionary;

public class LemmatisationSvcImpl implements LemmatisationSvc {

    Dictionary dictionary;

    public void init() throws InitializationException {

        URL url = ClassLoader.getSystemResource("saldo.txt");

        try {
            dictionary = new Dictionary(new File(url.getFile()));
        } catch (IOException ex) {
            throw new InitializationException("Could not read the wordlist-file");
        }

    }

    public LemmatisedResponse getParadigmsObject(String word) {

        LemmatisedResponse response = new LemmatisedResponse();
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
        }
        response.setStatusCode(LemmatisedResponse.StatusCode.ok);
        response.setOriginalWord(word);
        response.setList(resultList);

        return response;
    }
}
