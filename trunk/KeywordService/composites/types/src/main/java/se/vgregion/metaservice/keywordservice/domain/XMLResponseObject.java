package se.vgregion.metaservice.keywordservice.domain;

/**
 * This class should be the basis of all responseObjects,
 * functions that wants to add additional return values (such as lists etc)
 * to the responseobject should extend this class
 */
public class XMLResponseObject extends ResponseObject {
    private String xml = null;


    /**
     * Empty Constructor
     */
    public XMLResponseObject(){
    }

    /**
     * Constructor for ok message
     */
    public XMLResponseObject(String requestId, String xml) {
        super(requestId);
        this.xml = xml;
    }

    /**
     * Constructor for error-response
     */
    public XMLResponseObject(String requestId, StatusCode statusCode, String errorMessage) {
        super(requestId, statusCode, errorMessage);
    }

    /**
     * Get the XML value
     * @return
     */
    public String getXml() {
        return xml;
    }


    /**
     * Set the XML value
     * @param xml
     */
    public void setXml(String xml) {
        this.xml = xml;
    }


}
