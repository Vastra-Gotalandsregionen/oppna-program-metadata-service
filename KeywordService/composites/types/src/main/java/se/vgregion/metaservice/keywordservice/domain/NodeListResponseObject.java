package se.vgregion.metaservice.keywordservice.domain;

import java.util.List;

/**
 * This is the responseObject that should be used when the response is to
 * include a list of nodes.
 */
public class NodeListResponseObject extends ResponseObject{

    List<MedicalNode> nodeList = null;

    /**
     * Constructor for ok response
     * @param nodeList the list of nodes
     */
    public NodeListResponseObject(List<MedicalNode> nodeList){
        super();
        this.nodeList = nodeList;
    }

    /**
     * Constructor for an error response
     * @param statusCode the errorcode
     * @param errorMessage human-readable error message
     */
    public NodeListResponseObject(StatusCode statusCode, String errorMessage){
        super(statusCode,errorMessage);
    }

    /**
     * Get the list of nodes
     * @return the list of nodes, if this is an error response
     * this function returns null
     */
    public List<MedicalNode> getNodeList() {
        return nodeList;
    }

    /**
     * set the list of nodes
     * @param nodeList the new nodelist
     */
    public void setNodeList(List<MedicalNode> nodeList) {
        this.nodeList = nodeList;
    }

}
