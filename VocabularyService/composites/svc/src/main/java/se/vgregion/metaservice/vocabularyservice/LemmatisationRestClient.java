package se.vgregion.metaservice.vocabularyservice;

import javax.xml.bind.JAXBElement;
import org.springframework.web.client.RestTemplate;
import se.vgregion.metaservice.lemmatisation.generated.LemmatisedResponse;

/**
 * LemmatisationRestClient is used to make REST requests to
 * the LemmatisationService which lemmatises words and returns
 * all paradigms to each word. 
 */
public class LemmatisationRestClient {
    private RestTemplate restTemplate = null;
    private String restUrl;

    /**
     * Retrieves a lemmatised response of the parameter word.
     * The request is sent using REST to the LemmatisationService.
     *
     * @param word The word to lemmatise
     * @return A lemmatised response object fo the word
     */
    public LemmatisedResponse getLemmatisedResponse(String word) throws Exception {
        return (LemmatisedResponse) restTemplate.getForObject(restUrl, JAXBElement.class, word).getValue();
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getRestUrl() {
        return restUrl;
    }

    public void setRestUrl(String restUrl) {
        this.restUrl = restUrl;
    }
}
