package se.vgregion.metaservice.keywordservice.domain;

/**
 * This class should be the basis of all responseObjects, 
 * functions that wants to add additional return values (such as lists etc)
 * to the responseobject should extend this class
 */
public class ResponseObject {

    /**
     * This is the set of possible statusmessages in a responseobject
     */
    public enum StatusCode {

        ok(200),

        unsupported_text_format(1001),
        error_formatting_content(1002),
        no_content(1003),
        error_processing_content(2001),
        error_getting_keywords_from_taxonomy(3001),
        unknown_error(6001);
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
    private String requestId = null;
    private String errorMessage = null;
    private StatusCode statusCode = null;

    /**
     * Constructor for ok message
     */
    public ResponseObject(String requestId) {
        this.requestId = requestId;
        statusCode = StatusCode.ok;
    }

    /**
     * Constructor for error-response
     */
    public ResponseObject(String requestId, StatusCode statusCode, String errorMessage) {
        this.requestId = requestId;
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }

    /**
     * Get the statusCode for this response
     * @return the statusCode
     */
    public StatusCode getStatusCode() {
        return statusCode;
    }

    /**
     * Set the statusCode for this responseObject
     * @param statusCode the statusCode to set
     */
    public void setErrorCode(StatusCode errorCode) {
        this.statusCode = errorCode;
    }

    /**
     * Get the errorMessage for this responseObject
     * @return the errorMessage if this responseObject has one
     * null otherwise
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set the errorMessage for this responseObject
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Get the uniqe requestId that identifies the original request
     * @return the id
     */
    public String getRequestId() {
        return requestId;
    }


}
