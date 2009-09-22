/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.vgregion.metaservice.keywordservice.domain;

/**
 *
 * @author tobias
 */
public class LookupResponseObject extends ResponseObject {

    public enum ListType {
        WHITELIST,
        BLACKLIST,
        NONE,
    }

    private ListType listType;

    public ListType getListType() {
        return listType;
    }

    public void setListType(ListType listType) {
        this.listType = listType;
    }

    public LookupResponseObject() {
        super();
    }

    public LookupResponseObject(String requestId, ListType listType) {
        super(requestId);
        this.listType = listType;
    }

    /**
     * Constructor for an error response
     * @param the uniqe requestId
     * @param statusCode the errorcode
     * @param errorMessage human-readable error message
     */
    public LookupResponseObject(String requestId, StatusCode statusCode, String errorMessage) {
        super(requestId, statusCode, errorMessage);
    }

    
}

