package se.vgregion.metaservice.vocabularyservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import se.vgregion.metaservice.keywordservice.MedicalTaxonomyService;
import se.vgregion.metaservice.keywordservice.domain.Identification;
import se.vgregion.metaservice.keywordservice.domain.LookupResponseObject;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode;
import se.vgregion.metaservice.keywordservice.domain.NodeListResponseObject;
import se.vgregion.metaservice.keywordservice.domain.ResponseObject;

/**
 * Class for handling queries for a vocabulary
 * 
 * @author tobias
 * 
 */
public class VocabularyService {

    private MedicalTaxonomyService medicalTaxonomyService;
    private String whitelistName = "Whitelist"; //TODO: Move to profile configuration?
    private String blacklistName = "Blacklist"; //TODO: Move to profile configuration?

    /**
     * Look up a word in whitelist or blacklist
     * @param id the identification of the user that lookup the node
     * @param requestId the unique request id
     * @param word the word to lookup
     * @return
     */
    public LookupResponseObject lookupWord(Identification identification, String requestId, String word) {

        List<MedicalNode> nodes = medicalTaxonomyService.findNodesWithParents(word, true);
        LookupResponseObject response = null;
        if (nodes.size() != 0) {
            MedicalNode node = nodes.get(0);
            for (MedicalNode parent : node.getParents()) {
                if (parent.getName().equals(blacklistName)) {
                    response = new LookupResponseObject(requestId, LookupResponseObject.ListType.BLACKLIST);
                    continue;
                }
                if (parent.getName().equals(whitelistName)) {
                    response = new LookupResponseObject(requestId, LookupResponseObject.ListType.WHITELIST);
                }
            }
        }else{
            response = new LookupResponseObject(requestId, LookupResponseObject.ListType.NONE);
        }


        return response;
    }

    /**
     * Return all the childrens of a node
     * @param requestId the unique request id
     * @param path the path to the node
     * @return a NodeListResponeObject that contains a list of all the childrenNodes
     * check the statuscode in this object to see if the operation was succesfull
     */
    public NodeListResponseObject getVocabulary(String requestId, String path) {

        // TODO: handle errors, catch exceptions and set errorCodes

        List<MedicalNode> nodes = new ArrayList<MedicalNode>();
        path = path.charAt(0) == '/' ? path.substring(1) : path;
        String[] hierarchy = path.split("/");
        LinkedList<String> q = new LinkedList<String>(Arrays.asList(hierarchy));
        MedicalNode n = null;
        while (!q.isEmpty()) {
            n = medicalTaxonomyService.getChildNode(n, q.removeFirst());
            if (n == null) {
                return new NodeListResponseObject(requestId, nodes);
            }
        }
        nodes = medicalTaxonomyService.getChildNodes(n);

        return new NodeListResponseObject(requestId, nodes);
    }

    /**
     * Add a new node to appelon (Not implemented)
     * @param id the identification of the user that adds the node
     * @param requestId the unique request id
     * @param node the node to add, this node should already contain
     * the parent-relations
     * @return a ResponeObject, check the statuscode in this object to see
     * if the operation was succesfull
     */
    public ResponseObject addVocabularyNode(Identification id, String requestId, MedicalNode node) {

        //TODO: implement this method
        return new ResponseObject(requestId);

    }

    /**
     * Move a node in appelon, that is: change the parent of the node
     * (Not implemented)
     * @param id the identification of the user that moves the node
     * @param requestId the unique request id
     * @param nodeId the id of the node to move
     * @param destNodeId the id of the new parent
     * @return a ResponseObject, check the statuscode in this object to see
     * if the operation was succesfull
     */
    public ResponseObject moveVocabularyNode(Identification id, String requestId, String nodeId, String destNodeId) {

        //TODO: implement this method
        return new ResponseObject(requestId);

    }

    /**
     * Update the content of a node (not implemented yet)
     * @param id the identification of the user that updates the node
     * @param requestId the unique request id
     * @param node the updated node, the id of the node must not be changed.
     * @return a ResponseObject, check the statuscode in this object to see
     * if the operation was succesfull
     */
    public ResponseObject updateVocabularyNode(Identification id, String requestId, MedicalNode node) {

        //TODO: implement this method
        return new ResponseObject(requestId);

    }

    /**
     * Dump the entire appelon database as xml, awaiting specification.
     */
    public void dumpDbAsXML() {
        //TODO: Write spec and implement this method;
    }

    public void setMedicalTaxonomyService(
            MedicalTaxonomyService medicalTaxonomyService) {
        this.medicalTaxonomyService = medicalTaxonomyService;
    }
}
