package se.vgregion.metaservice.vocabularyservice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


import java.util.Set;
import java.util.logging.Level;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.log4j.Logger;
import se.vgregion.metaservice.keywordservice.MedicalTaxonomyService;
import se.vgregion.metaservice.keywordservice.domain.Identification;
import se.vgregion.metaservice.keywordservice.domain.LastChangeResponseObject;
import se.vgregion.metaservice.keywordservice.domain.LookupResponseObject;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode.UserStatus;
import se.vgregion.metaservice.keywordservice.domain.NodeListResponseObject;
import se.vgregion.metaservice.keywordservice.domain.NodeProperty;
import se.vgregion.metaservice.keywordservice.domain.Options;
import se.vgregion.metaservice.keywordservice.domain.ResponseObject;
import se.vgregion.metaservice.keywordservice.domain.ResponseObject.StatusCode;
import se.vgregion.metaservice.keywordservice.domain.XMLResponseObject;
import se.vgregion.metaservice.keywordservice.exception.KeywordsException;
import se.vgregion.metaservice.keywordservice.exception.NodeNotFoundException;

/**
 * Class for handling queries for a vocabulary
 */
public class VocabularyService {
    private static Logger log = Logger.getLogger(VocabularyService.class);
    private MedicalTaxonomyService medicalTaxonomyService;
    private String whitelistName = "Whitelist"; //TODO: Move to profile configuration?
    private String blacklistName = "Blacklist"; //TODO: Move to profile configuration?
    private String reviewlistName = "Reviewlist"; //TODO: Move to profile configuration?
    //TODO: specify this elsewhere?
    private String profileIdPropertyName = "profileId";
    private String userIdPropertyName = "userId";
    private String urlPropertyName = "url";
    private Set<String> allowedNamespaces = null;


    public LastChangeResponseObject getLastChange(Identification identification, String requestId){

        Long lastChange;
        try {
            lastChange = medicalTaxonomyService.getLastChange(identification);
        } catch (KeywordsException ex) {
            //TODO: another error message??
            return new LastChangeResponseObject(requestId, StatusCode.error_getting_keywords_from_taxonomy, "No lastChange property found");
        }
        return new LastChangeResponseObject(requestId,lastChange);
    }
    
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
                        medicalTaxonomyService.updateNodeProperties(node,false);
                        medicalTaxonomyService.setLastChangeNow();
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
                node = addNodeProperties(node, identification, options);
                medicalTaxonomyService.createNewConcept(node, reviewNode.getInternalId());
                medicalTaxonomyService.setLastChangeNow();

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
     * Move a node in a vocabulary, that is: change the parent of the node
     * @param id the identification of the user that moves the node
     * @param requestId the unique request id
     * @param nodeId the id of the node to move
     * @param destNodeId the id of the new parent
     * @return a ResponseObject, check the statuscode in this object to see
     * if the operation was succesfull
     */
    public ResponseObject moveVocabularyNode(Identification id, String requestId, String nodeId, String destNodeId) {
        ResponseObject responseObject = new ResponseObject(requestId);
        try {
            medicalTaxonomyService.moveNode(nodeId, destNodeId);
            medicalTaxonomyService.setLastChangeNow();
        } catch (KeywordsException ex) {
            log.error(MessageFormat.format("{0}:{1}: Node ({2}) could not be moved to parent {{3}}",
                    requestId, StatusCode.error_editing_taxonomy.code(), nodeId, destNodeId), ex);
            responseObject.setStatusCode(StatusCode.error_editing_taxonomy);
            responseObject.setErrorMessage("Error editing taxonomy: Node could not be moved");
        } catch (NodeNotFoundException ex) {
            log.error(MessageFormat.format("{0}:{1}:{2}", requestId, StatusCode.error_editing_taxonomy.code(), ex.getMessage()), ex);
            responseObject.setStatusCode(StatusCode.error_editing_taxonomy);
            responseObject.setErrorMessage("Error editing taxonomy: Node could not be found");
        }
        return responseObject;
    }

    /**
     * Update the content of a node (not implemented yet)
     * @param id the identification of the user that updates the node
     * @param requestId the unique request id
     * @param node the updated node, the id of the node must not be changed.
     * @return ResponseObject with status information.
     */
    public ResponseObject updateVocabularyNode(Identification id, String requestId, MedicalNode node) {
        //TODO: implement this method
        return new ResponseObject(requestId);

    }

    /**
     * Retrieves the XML representation of an Apelon namespace.
     * Only preconfiguered namespaces can be selected.
     * The namespace configuration is available by the classpath
     * resource <code>keywordservice-svc.properties</code>.
     *
     * @param id User identification
     * @param requestId unique request identifier
     * @param namespace The namespace to export
     */
    public XMLResponseObject getNamespaceXml(Identification id, String requestId, String namespace) {
        XMLResponseObject response = new XMLResponseObject();
        response.setRequestId(requestId);

        Long now = new Date().getTime();
        
        if (allowedNamespaces.contains(namespace)) {
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = null;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
        
            // Get the first set of nodes
            List<MedicalNode> nodeList = getVocabulary(requestId, namespace).getNodeList();
            
            try {
                writer = factory.createXMLStreamWriter(out, "utf-8");
                writer.writeStartDocument();
                writer.writeComment("XML generated: " + new java.util.Date().toString());
                writer.writeStartElement("namespace");

                // Recursively traverse all child nodes
                if (nodeList != null) {
                    for (MedicalNode node : nodeList) {
                        traverseChildNodes(node, requestId, writer, namespace);
                    }
                }

                writer.writeEndElement();
                writer.writeEndDocument();
                writer.flush();
                response.setXml(out.toString());
                writer.close();
                out.close();
                response.setStatusCode(XMLResponseObject.StatusCode.ok);

            } catch (IOException ex) {
                log.error("Error exporting namespace to XML", ex);
                response.setStatusCode(XMLResponseObject.StatusCode.error_processing_content);
                response.setErrorMessage("Error exporting namespace to XML: " + ex.getMessage());
                
            } catch (XMLStreamException ex) {
                log.error("Error exporting namespace to XML", ex);
                response.setStatusCode(XMLResponseObject.StatusCode.error_processing_content);
                response.setErrorMessage("Error exporting namespace to XML: " + ex.getMessage());
                

            } finally {
                try {
                    if (writer != null) writer.close();
                    if (out != null) out.close();
                } catch (Exception ex) {
                    log.error(ex);
                }
            }            
            
        } else {
            response.setStatusCode(XMLResponseObject.StatusCode.error_processing_content);
            response.setErrorMessage("Namespace is not in the list of namespaces authorized for xml export");
            

        }
        response.setTime(now);
        return response;
    }


    /**
     * Helper routine to traverse all child nodes recursively from a parent node
     *
     * @param node The node to recurse from
     * @param requestId The request identifier
     * @param writer Writer used to produce xml for the node
     * @param path The path to the node using '/' as a path separator
     * @throws XMLStreamException
     */
    private void traverseChildNodes(MedicalNode node, String requestId, XMLStreamWriter writer, String path) throws XMLStreamException {
        
        // Produce XML for all the properties in the node

        writer.writeStartElement("node");

        writer.writeStartElement("name");
        writer.writeCharacters(node.getName());
        writer.writeEndElement();
        
        writer.writeStartElement("internalId");
        writer.writeCharacters(node.getInternalId());
        writer.writeEndElement();

        writer.writeStartElement("namespaceId");
        writer.writeCharacters(node.getNamespaceId());
        writer.writeEndElement();

        writer.writeStartElement("sourceId");
        writer.writeCharacters(node.getSourceId());
        writer.writeEndElement();

        writer.writeStartElement("path");
        writer.writeCharacters(path);
        writer.writeEndElement();

        writer.writeStartElement("properties");
        for (NodeProperty prop : node.getProperties()) {
            writer.writeStartElement(prop.getName());
            writer.writeCharacters(prop.getValue());
            writer.writeEndElement();
        }
        writer.writeEndElement();

        writer.writeStartElement("userstatus");
        for (UserStatus status : node.getUserStatus()) {
            writer.writeStartElement(status.name());
            writer.writeCharacters(status.value());
            writer.writeEndElement();
        }
        writer.writeEndElement();

        writer.writeStartElement("synonyms");
        for (String synonym : node.getSynonyms()) {
            writer.writeStartElement("synonym");
            writer.writeCharacters(synonym);
            writer.writeEndElement();
        }
        writer.writeEndElement();

        writer.writeStartElement("parents");
        for (MedicalNode parent : node.getParents()) {
            writer.writeStartElement("parent");
            writer.writeCharacters(parent.getName());
            writer.writeEndElement();
        }
        writer.writeEndElement();

        writer.writeStartElement("hasChildren");
        writer.writeCharacters(node.getHasChildren() ? "true" : "false");
        writer.writeEndElement();


        // Prepare a new request to recurse into child nodes
      
        String childpath = path + "/" + node.getName();
        List<MedicalNode> nodeList = getVocabulary(requestId, path).getNodeList();

        if (nodeList != null) {
            writer.writeStartElement("children");
            
            for (MedicalNode child : nodeList) {
                writer.writeStartElement("child");
                writer.writeCharacters(child.getName());
                writer.writeEndElement();
            }
            writer.writeEndElement();
        }

        writer.writeEndElement(); // end node

        
        // Recurse into childnodes
        if (nodeList != null) {
            for (MedicalNode n : nodeList) {
                traverseChildNodes(n, requestId, writer, childpath);
            }
        }

    }

    
    public void setMedicalTaxonomyService(MedicalTaxonomyService medicalTaxonomyService) {
        this.medicalTaxonomyService = medicalTaxonomyService;
    }

    public void setNamepacesExposedToXmlApi(String allowedNamespaces) {
        Set<String> set = new HashSet<String>();
        String[] arr = allowedNamespaces.split(",");
        if (arr != null) {
            for (String str : arr) {
                set.add(str.trim());
            }
        }
        this.allowedNamespaces = set;
    }
}
