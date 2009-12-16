package se.vgregion.metaservice.LemmatisationService.domain;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * LemmatisedResponse
 */
@XmlRootElement
public class LemmatisedResponse implements Serializable{
    private String errorMessage = null;
    private StatusCode statusCode = null;
    private String originalWord = null;
    private List<LemmatisedObject> list;
    public enum StatusCode {
        ok(200),
        no_word_found(404),
        error(500);
        private final int code;

        StatusCode(int code) {
            this.code = code;
        }

        /**
         * Get the status code as an integer
         * @return the code
         */
        public int code() {
            return code;
        }
    }


    public LemmatisedResponse() {
    }

    public List<LemmatisedObject> getList() {
        return list;
    }

    public void setList(List<LemmatisedObject> list) {
        this.list = list;
    }

    public String getOriginalWord() {
        return originalWord;
    }

    public void setOriginalWord(String originalWord) {
        this.originalWord = originalWord;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }
    
}
