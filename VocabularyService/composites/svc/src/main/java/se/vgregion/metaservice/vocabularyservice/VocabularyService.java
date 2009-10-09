package se.vgregion.metaservice.vocabularyservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import se.vgregion.metaservice.keywordservice.MedicalTaxonomyService;
import se.vgregion.metaservice.keywordservice.domain.Identification;
import se.vgregion.metaservice.keywordservice.domain.LookupResponseObject;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode;
import se.vgregion.metaservice.keywordservice.domain.NodeListResponseObject;
import se.vgregion.metaservice.keywordservice.domain.Options;
import se.vgregion.metaservice.keywordservice.domain.ResponseObject;
import se.vgregion.metaservice.keywordservice.exception.KeywordsException;

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
    private String reviewlistName = "Reviewlist"; //TODO: Move to profile configuration?
    //TODO: specify this elsewhere?
    private String profileIdPropertyName = "profileId";
    private String userIdPropertyName = "userId";
    private String urlPropertyName = "url";

    /**
     * Look up a word in whitelist or blacklist
     * @param id the identification of the user that lookup the node
     * @param requestId the unique request id
     * @param word the word to lookup
     * @return
     */
    public LookupResponseObject lookupWord(Identification identification, String requestId, String word, Options options) {

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
                if (parent.getName().equals(reviewlistName)) {
                    node = addNodeProperties(node, identification, options);
                    try {
                        medicalTaxonomyService.updateNodeProperties(node);

                        response = new LookupResponseObject(requestId, LookupResponseObject.ListType.NONE);
                    } catch (KeywordsException ex) {
                        //TODO: new statuscode?
                        response = new LookupResponseObject(requestId,
                                ResponseObject.StatusCode.error_getting_keywords_from_taxonomy,
                                "Could not add properties to keyword");
                    }
                }
            }
        } else {
            response = new LookupResponseObject(requestId, LookupResponseObject.ListType.NONE);

            // create a new node and add to review-list
            MedicalNode reviewNode = medicalTaxonomyService.findNodes(reviewlistName, false).get(0);

            try {
                MedicalNode node = new MedicalNode();
                node.setName(word);
                node.setNamespaceId("33315");
                node = addNodeProperties(node,identification,options);
                medicalTaxonomyService.createNewConcept(node, reviewNode.getInternalId());

            } catch (KeywordsException ex) {
                //TODO: new statuscode?
                response = new LookupResponseObject(requestId,
                        ResponseObject.StatusCode.error_getting_keywords_from_taxonomy,
                        "Could not create the new keyword");
            }
        }
        return response;
    }

    private MedicalNode addNodeProperties(MedicalNode node, Identification identification, Options options) {
        node.addProperty(profileIdPropertyName, identification.getProfileId());
        node.addProperty(userIdPropertyName, identification.getUserId());
        if (options.getUrl() != null) {
            node.addProperty(urlPropertyName, options.getUrl());
        }

        return node;
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
        try {
            medicalTaxonomyService.moveNode(nodeId, destNodeId);
        } catch (KeywordsException ex) {
            Logger.getLogger(VocabularyService.class.getName()).log(Level.SEVERE, null, ex);
        }
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
