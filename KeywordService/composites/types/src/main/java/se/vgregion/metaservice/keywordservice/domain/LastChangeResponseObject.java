/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.vgregion.metaservice.keywordservice.domain;

/**
 *
 * @author sture.svensson
 */
public class LastChangeResponseObject extends ResponseObject {

    private Long lastChange;

    public LastChangeResponseObject() {
        super();
    }

    public LastChangeResponseObject(String requestId, Long lastChange) {
        super(requestId);
        this.lastChange = lastChange;
    }

    /**
     * Constructor for an error response
     * @param the uniqe requestId
     * @param statusCode the errorcode
     * @param errorMessage human-readable error message
     */
    public LastChangeResponseObject(String requestId, StatusCode statusCode, String errorMessage) {
        super(requestId, statusCode, errorMessage);
    }

    public Long getLastChange() {
        return lastChange;
    }

    public void setLastChange(Long lastChange) {
        this.lastChange = lastChange;
    }
}
