package se.vgregion.metaservice.vocabularyservice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import se.vgregion.metaservice.keywordservice.domain.NodeListResponseObject;
import se.vgregion.metaservice.keywordservice.domain.NodePath;
import se.vgregion.metaservice.keywordservice.domain.NodeProperty;
import se.vgregion.metaservice.keywordservice.domain.Options;
import se.vgregion.metaservice.keywordservice.domain.ResponseObject;
import se.vgregion.metaservice.keywordservice.domain.ResponseObject.StatusCode;
import se.vgregion.metaservice.keywordservice.domain.SearchProfile;
import se.vgregion.metaservice.keywordservice.domain.XMLResponseObject;
import se.vgregion.metaservice.keywordservice.exception.InvalidPropertyTypeException;
import se.vgregion.metaservice.keywordservice.exception.KeywordsException;
import se.vgregion.metaservice.keywordservice.exception.NodeAlreadyExistsException;
import se.vgregion.metaservice.keywordservice.exception.NodeNotFoundException;
import se.vgregion.metaservice.keywordservice.exception.ParentNotFoundException;

/**
 * Class for handling queries for a vocabulary
 */
public class VocabularyService {

    private static Logger log = Logger.getLogger(VocabularyService.class);
    private MedicalTaxonomyService medicalTaxonomyService;
    //TODO: specify this elsewhere?
    private String profileIdPropertyName = "profileId";
    private String userIdPropertyName = "userId";
    private String urlPropertyName = "url";
    private Set<String> allowedNamespaces = null;
    /** Caches namespaceName to namespaceId-name resolutions. */
    private Map<String, String> namespaceCache = new HashMap<String, String>();
    /** Map of searchprofiles indexed by profileId */
    private Map<String, SearchProfile> searchProfiles;

    public LastChangeResponseObject getLastChange(Identification identification, String requestId, String namespaceName) {
        String namespaceId = getNamespaceIdByName(namespaceName, requestId);
        if (namespaceId == null) {
            return new LastChangeResponseObject(requestId, StatusCode.error_getting_keywords_from_taxonomy, "Invalid namespace name");
        }
        Long lastChange;
        try {
            lastChange = medicalTaxonomyService.getLastChange(namespaceId);
        } catch (KeywordsException ex) {
            //TODO: another error message??
            return new LastChangeResponseObject(requestId, StatusCode.error_getting_keywords_from_taxonomy, "No lastChange property found");
        }
        return new LastChangeResponseObject(requestId, lastChange);
    }

    /**
     * Look up a word in whitelist or blacklist
     * @param id the identification of the user that lookup the node
     * @param requestId the unique request id
     * @param word the word to lookup
     * @return
     */
    public LookupResponseObject lookupWord(Identification id, String requestId, String word, Options options) {
        SearchProfile profile = searchProfiles.get(id.getProfileId());
        if (profile == null) {
            return new LookupResponseObject(requestId, StatusCode.error_getting_keywords_from_taxonomy, "Specified profile does not exist");
        }
        String namespace = profile.getWhiteList().getNamespace(); //TODO: No support for having different namespaces. Only looks in the whitelist-namespaceId
        String namespaceId = getNamespaceIdByName(namespace, requestId);
        List<MedicalNode> nodes = medicalTaxonomyService.findNodesWithParents(word, namespaceId, true);
        LookupResponseObject response = new LookupResponseObject(requestId, LookupResponseObject.ListType.NONE);

        if (nodes.size() != 0) {
            MedicalNode node = nodes.get(0);

            // Ensure read privileges to target namespaceId
            if (hasNamespaceReadAccess(node.getNamespaceId(), id.getProfileId(), requestId)) {

                for (MedicalNode parent : node.getParents()) {


                    if (parent.getName().equals(profile.getBlackList().getName())) {
                        response = new LookupResponseObject(requestId, LookupResponseObject.ListType.BLACKLIST);
                        continue;
                    }
                    if (parent.getName().equals(profile.getWhiteList().getName())) {
                        response = new LookupResponseObject(requestId, LookupResponseObject.ListType.WHITELIST);
                    }
                    if (parent.getName().equals(profile.getReviewList().getName())) {

                        // Ensure write privileges to reviewList
                        if (hasNamespaceWriteAccess(node.getNamespaceId(), id.getProfileId(), requestId)) {
                            node = addNodeProperties(node, id, options);

                            try {
                                medicalTaxonomyService.createNodeProperties(node, false);
                                medicalTaxonomyService.setLastChangeNow(node.getNamespaceId());

                            } catch (InvalidPropertyTypeException ex) {
                                response = new LookupResponseObject(requestId,
                                        ResponseObject.StatusCode.error_getting_keywords_from_taxonomy,
                                        "Invalid property types");

                            } catch (KeywordsException ex) {
                                response = new LookupResponseObject(requestId,
                                        ResponseObject.StatusCode.error_getting_keywords_from_taxonomy,
                                        "Could not add properties to keyword");
                            }
                        } else {
                            response.setErrorMessage("The profile is invalid or does not have read privileges to " + profile.getReviewList().getName());
                            response.setStatusCode(StatusCode.error_getting_keywords_from_taxonomy);
                        }

                    }
                }

            } else {
                response.setErrorMessage("The profile is invalid or does not have read privileges to target namespace");
                response.setStatusCode(StatusCode.error_getting_keywords_from_taxonomy);
            }


        } else {

            // create a new node and add to review-list
            MedicalNode reviewNode = medicalTaxonomyService.findNodes(profile.getReviewList().getName(), namespaceId, false).get(0);

            try {
                MedicalNode node = new MedicalNode();
                node.setName(word);
                node.setNamespaceId(namespaceId);
                node = addNodeProperties(node, id, options);

                // ensure write privileges to reviewlist
                if (hasNamespaceWriteAccess(node.getNamespaceId(), id.getProfileId(), requestId)) {
                    node.addParent(reviewNode);
                    medicalTaxonomyService.createNewConcept(node);
                    medicalTaxonomyService.setLastChangeNow(node.getNamespaceId());

                } else {
                    response.setErrorMessage("The profile is invalid or does not have read privileges to " + profile.getReviewList().getName());
                    response.setStatusCode(StatusCode.error_getting_keywords_from_taxonomy);
                }
            } catch (InvalidPropertyTypeException ex) {
                response = new LookupResponseObject(requestId, ResponseObject.StatusCode.error_editing_taxonomy,
                        "Invalid property types");
            } catch (NodeNotFoundException ex) {
                response = new LookupResponseObject(requestId, ResponseObject.StatusCode.error_editing_taxonomy,
                        "Could not find the reviewList");
            } catch (NodeAlreadyExistsException ex) {
                //This should never happen
                response = new LookupResponseObject(requestId, ResponseObject.StatusCode.error_editing_taxonomy,
                        "Node already exists");
            } catch (KeywordsException ex) {
                //TODO: new statuscode?
                response = new LookupResponseObject(requestId,
                        ResponseObject.StatusCode.error_editing_taxonomy,
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
     * @param options Options object to filter on property
     * @return a NodeListResponeObject that contains a list of all the childrenNodes
     * check the statuscode in this object to see if the operation was succesfull
     */
    public NodeListResponseObject getVocabulary(String requestId, String path, Options options) {
        List<MedicalNode> nodes = new ArrayList<MedicalNode>();
        List<MedicalNode> response = new ArrayList<MedicalNode>();
        NodePath nodePath = new NodePath();
        nodePath.setPath(path);

        String[] hierarchy = nodePath.getRelativePath().split("/");
        String namespaceId = getNamespaceIdByName(nodePath.getNamespace(), requestId);
        LinkedList<String> q = new LinkedList<String>(Arrays.asList(hierarchy));
        MedicalNode n = null;

        while (!q.isEmpty()) {
            n = medicalTaxonomyService.getChildNode(namespaceId, n, q.removeFirst());
            if (n == null) {
                log.error(MessageFormat.format("{0}:{1}: Invalid path {2}",
                        requestId, StatusCode.error_getting_keywords_from_taxonomy, path));
                return new NodeListResponseObject(requestId, StatusCode.error_getting_keywords_from_taxonomy, "Invalid path '" + path + "'");
            }
        }
        nodes = medicalTaxonomyService.getChildNodes(n);

        // filter returned nodes by property
        if (nodes.size() > 0 & options != null && options.getFilterByProperties() != null) {
            for (MedicalNode node : nodes) {
                for (NodeProperty prop : node.getProperties()) {
                    for (Entry<String, List<String>> filterEntry : options.getFilterByProperties().entrySet()) {
                        if (filterEntry.getKey().equals(prop.getName())) {
                            for (String filter : filterEntry.getValue()) {
                                if (filter.equals(prop.getValue())) {
                                    // Matching properties; add the node to the response
                                    // Currently, it's enough that a node matches one filter entry
                                    log.debug("Node matches filter. Adding node " + node.getName() + " to the list of nodes to return");
                                    response.add(node);
                                }
                            }
                        }
                    }
                }
            }

            // Return only the nodes matching the filters
            return new NodeListResponseObject(requestId, response);
        }

        // Return all child nodes
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
        ResponseObject response = new ResponseObject();
        response.setRequestId(requestId);

        // Check if the profile has write-access to the namespaceId of the node
        if (hasNamespaceWriteAccess(node.getNamespaceId(), id.getProfileId(), requestId)) {
            try {
                log.info(node.getParents().isEmpty());
                medicalTaxonomyService.createNewConcept(node);
                response.setStatusCode(StatusCode.ok);
                log.info("Created new node");
            } catch (InvalidPropertyTypeException ex) {
                response.setErrorMessage("Could not create new keyword, the property name is invalid");
                response.setStatusCode(StatusCode.error_editing_taxonomy);
            } catch (NodeNotFoundException ex) {
                response.setErrorMessage("Could not create new keyword, could not find the specified parent");
                response.setStatusCode(StatusCode.error_editing_taxonomy);
            } catch (NodeAlreadyExistsException ex) {
                response.setErrorMessage("Could not create new keyword, the keyword already exists");
                response.setStatusCode(StatusCode.error_editing_taxonomy);
            } catch (KeywordsException ex) {
                response.setErrorMessage("Could not create new keyword, error editing taxonomy");
                response.setStatusCode(StatusCode.error_editing_taxonomy);
            }
        } else {
            response.setErrorMessage("The profile is invalid or does not have read privileges to target namespace");
            response.setStatusCode(StatusCode.error_editing_taxonomy);
        }

        return response;
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
    public ResponseObject moveVocabularyNode(Identification id, String requestId, MedicalNode node, MedicalNode destNode) {
        ResponseObject response = new ResponseObject(requestId);

        try {

            if (hasNamespaceWriteAccess(node.getNamespaceId(), id.getProfileId(), requestId) &&
                    hasNamespaceWriteAccess(destNode.getNamespaceId(), id.getProfileId(), requestId)) {

                medicalTaxonomyService.moveNode(node, destNode);
                medicalTaxonomyService.setLastChangeNow(destNode.getNamespaceId());
                response.setStatusCode(StatusCode.ok);

            } else {
                response.setErrorMessage("The profile is invalid or does not have read privileges to target namespace");
                response.setStatusCode(StatusCode.error_editing_taxonomy);
            }

        } catch (KeywordsException ex) {
            log.error(MessageFormat.format("{0}:{1}: Node ({2}) could not be moved to parent {{3}}",
                    requestId, StatusCode.error_editing_taxonomy.code(), node.getInternalId(), destNode.getInternalId()), ex);
            response.setStatusCode(StatusCode.error_editing_taxonomy);
            response.setErrorMessage("Error editing taxonomy: Node could not be moved");
        } catch (NodeNotFoundException ex) {
            log.error(MessageFormat.format("{0}:{1}:{2}", requestId, StatusCode.error_editing_taxonomy.code(), ex.getMessage()), ex);
            response.setStatusCode(StatusCode.error_editing_taxonomy);
            response.setErrorMessage("Error editing taxonomy: Node could not be found");
        } catch (Exception ex) {
            log.error(MessageFormat.format("{0}:{1}:{2}", requestId, StatusCode.error_editing_taxonomy.code(), ex.getMessage()), ex);
            response.setStatusCode(StatusCode.error_editing_taxonomy);
            response.setErrorMessage("Error editing taxonomy: Node could not be found");
        }

        return response;
    }

    /**
     * Update the content of a node (not implemented yet)
     * @param id the identification of the user that updates the node
     * @param requestId the unique request id
     * @param node the updated node, the id of the node must not be changed.
     * @return ResponseObject with status information.
     */
    public ResponseObject updateVocabularyNode(Identification id, String requestId, MedicalNode node) {
        ResponseObject response = new ResponseObject(requestId);

        if (hasNamespaceWriteAccess(node.getNamespaceId(), id.getProfileId(), requestId)) {
            try {
                medicalTaxonomyService.updateConcept(node);
                response.setStatusCode(StatusCode.ok);
            } catch (NodeNotFoundException ex) {
                response.setErrorMessage("The node was not found");
                response.setStatusCode(StatusCode.error_editing_taxonomy);
            } catch (KeywordsException ex) {
                response.setErrorMessage("The node could not be updated");
                response.setStatusCode(StatusCode.error_editing_taxonomy);
            } catch (InvalidPropertyTypeException ex) {
                response.setErrorMessage("One of the properties was of an undefined type");
                response.setStatusCode(StatusCode.error_editing_taxonomy);
            } catch (ParentNotFoundException ex) {
                response.setErrorMessage("Could not find the specified parent");
                response.setStatusCode(StatusCode.error_editing_taxonomy);
            }
            

        } else {
            response.setErrorMessage("The profile is invalid or does not have read privileges to target namespace");
            response.setStatusCode(StatusCode.error_editing_taxonomy);
        }


        return response;

    }

    /**
     * Retrieves the XML representation of an Apelon namespaceId.
     * Only preconfiguered namespaces can be selected.
     * The namespaceId configuration is available by the classpath
     * resource <code>keywordservice-svc.properties</code>.
     *
     * @param id User identification
     * @param requestId unique request identifier
     * @param namespaceId The namespaceId to export
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
            List<MedicalNode> nodeList = getVocabulary(requestId, namespace, null).getNodeList();

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
                    if (writer != null) {
                        writer.close();
                    }
                    if (out != null) {
                        out.close();
                    }
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

        writer.writeStartElement("synonyms");
        for (String synonym : node.getSynonyms()) {
            writer.writeStartElement("synonym");
            writer.writeCharacters(synonym);
            writer.writeEndElement();
        }
        writer.writeEndElement();

        writer.writeStartElement("hasChildren");
        writer.writeCharacters(node.getHasChildren() ? "true" : "false");
        writer.writeEndElement();


        // Prepare a new request to recurse into child nodes

        String childpath = path + "/" + node.getName();
        List<MedicalNode> nodeList = getVocabulary(requestId, path, null).getNodeList();

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

    /**
     * Check if a used has read access to the given namespaceId. This routine
     * makes use of the namespaceId cache and updates it where neccessary.
     *
     * @param namespaceName The id of the namespaceId used in the request
     * @param profileId The id of the profile used in the request
     * @param requestId The request identifier
     * @return True if the profile has read access to the namespaceId
     */
    private boolean hasNamespaceReadAccess(String namespaceId, String profileId, String requestId) {
        String namespace = getNamespaceById(namespaceId, requestId);
        if (namespace != null) {
            SearchProfile profile = searchProfiles.get(profileId);

            if (profile != null) {
                if (profile.getSearchableNamespaces().contains(namespace)) {
                    return true;

                } else {
                    log.warn(MessageFormat.format("{0}:{1}: Submitted profileId {2} does not have read privileges to namespace {3}",
                            requestId, StatusCode.unknown_error, profileId, namespace));
                }
            } else {
                log.warn(MessageFormat.format("{0}:{1}: Submitted profileId {2} does not match any predefined search profile",
                        requestId, StatusCode.unknown_error, profileId));
            }
        } else {
            log.warn(MessageFormat.format("{0}:{1}: Error locating namespace for namespaceId {2}",
                    requestId, StatusCode.unknown_error, namespaceId));
        }

        return false;
    }

    /**
     * Check if a used has write access to the given namespaceId. This routine
     * makes use of the namespaceId cache and updates it where neccessary.
     *
     * @param namespaceName The id of the namespaceId used in the request
     * @param profileId The id of the profile used in the request
     * @param requestId The request identifier
     * @return True if the profile has write access to the namespaceId
     */
    private boolean hasNamespaceWriteAccess(String namespaceId, String profileId, String requestId) {
        String namespace = getNamespaceById(namespaceId, requestId);
        if (namespace != null) {
            SearchProfile profile = searchProfiles.get(profileId);

            if (profile != null) {
                if (profile.getWriteableNamespaces().contains(namespace)) {
                    return true;

                } else {
                    log.warn(MessageFormat.format("{0}:{1}: Submitted profileId {2} does not have read privileges to namespace {3}",
                            requestId, StatusCode.unknown_error, profileId, namespace));
                }
            } else {
                log.warn(MessageFormat.format("{0}:{1}: Submitted profileId {2} does not match any predefined search profile",
                        requestId, StatusCode.unknown_error, profileId));
            }
        } else {
            log.warn(MessageFormat.format("{0}:{1}: Error locating namespace for namespaceId {2}",
                    requestId, StatusCode.unknown_error, namespaceId));
        }

        return false;
    }

    /**
     * A utility routine to get the namespaceId name from a namespaceName.
     * This routine initially checks the namespaceCache. If no match is
     * found it retrieves the namespaceId name and updates the namespaceCache.
     *
     * @param namespaceName The id of the namespaceId to lookup. Must be capable
     * of being converted to an integer.
     * @param requestId The request identifier
     * @return The namespaceId or null if an error occured
     */
    private String getNamespaceById(String namespaceId, String requestId) {
        String namespace = namespaceCache.get(namespaceId);

        if (namespace != null) {
            return namespace;
        }

        try {
            // Query the MedicaTaxonomyService for the namespaceId and update the cache
            namespace = medicalTaxonomyService.findNamespaceById(Integer.parseInt(namespaceId));
            namespaceCache.put(namespaceId, namespace);
            return namespace;

        } catch (NumberFormatException ex) {
            log.warn(MessageFormat.format("{0}:{1}:Unable to locate namespace name. NamespaceId {2} cannot be converted to an integer.",
                    requestId, StatusCode.unknown_error, namespaceId));
        } catch (Exception ex) {
            log.warn(MessageFormat.format("{0}:{1}:Error retrieving namespace", requestId, StatusCode.unknown_error), ex);
        }

        return null;
    }

    /**
     * A utility routine to get the namespace id from a namespace name.
     * This routine initially checks the namespaceCache. If no match is
     * found it retrieves the namespace id from the taxonomy service and updates the namespaceCache.
     *
     * @param namespaceName The name of the namespace to lookup.
     * @param requestId The request identifier
     * @return The namespace id or null if an error occured
     */
    private String getNamespaceIdByName(String namespaceName, String requestId) {
        String namespaceId = namespaceCache.get(namespaceName);

        if (namespaceId != null) {
            return namespaceId;
        }

        try {
            // Query the MedicaTaxonomyService for the namespaceId and update the cache
            namespaceId = medicalTaxonomyService.findNamespaceIdByName(namespaceName);
            namespaceCache.put(namespaceName, namespaceId);
            return namespaceId;

        } catch (Exception ex) {
            log.warn(MessageFormat.format("{0}:{1}:Error retrieving namespace {2}", requestId, StatusCode.unknown_error, namespaceName), ex);
        }

        return null;
    }

    public void setSearchProfiles(List<SearchProfile> searchProfiles) {
        // Simplify spring configuration by creating list instead of map
        this.searchProfiles = new HashMap<String, SearchProfile>();
        for (SearchProfile profile : searchProfiles) {
            this.searchProfiles.put(profile.getProfileId(), profile);
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
