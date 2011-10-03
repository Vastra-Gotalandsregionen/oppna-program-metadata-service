package se.vgregion.metaservice.keywordservice;

import com.apelon.dts.client.DTSException;
import java.util.List;
import java.util.Map;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode;
import com.apelon.dts.client.concept.ConceptChild;
import se.vgregion.metaservice.keywordservice.exception.InvalidPropertyTypeException;
import se.vgregion.metaservice.keywordservice.exception.KeywordsException;
import se.vgregion.metaservice.keywordservice.exception.NodeAlreadyExistsException;
import se.vgregion.metaservice.keywordservice.exception.NodeNotFoundException;
import se.vgregion.metaservice.keywordservice.exception.ParentNotFoundException;

/**
 * Handles communication with a medical taxonomy. Implementing classes of this interface acts like fa�ades
 * to the underlying Medical Taxonomy system. By exposing a subset of the underlying API and a general domain model
 * , using this API makes communication with the Medical Taxonomy system simpler and vendor-independent.
 * @author tobias
 *
 */
public abstract class MedicalTaxonomyService {

    /** CONNECTION PROPERTIES **/
    protected String username;
    protected String password;
    protected String host;
    protected int port;

    /** QUERY PROPERTIES **/
    protected String namespaceName;
    protected int resultKeywordsLimit;
    protected String sourceIdPropertyKey = "";

    /**
     * Initiates a connection to the Medical Taxonomy system. This involves connecting to the system
     * and setting up required parameters. Needs to be called prior to any other calls to the system
     * @return true on success, false otherwise
     */
    public abstract boolean initConnection() throws Exception;

    /**
     * Finds keywords in the Medical Taxonomy system based on the array of input words.
     * A keyword in this case is a node in the Medical Taxonomy
     * @param namespaceIds - A list of all namespace ids to search in
     * @param words an array of words to search for in the Medical Taxonomy
     * @return a Map where each word in the input array is mapped to a List of {@link}MedicalNode with the result
     */
    public abstract Map<String, List<MedicalNode>> findKeywords(List<String> namespaceIds, String[] words, Map<Integer, String[]> sourceIds);

    public abstract ConceptChild[] getNamespaceRoots(int namespaceId);

    /**
     * Returns a node with the specified id in the given namespace
     * @param internalId the id of the node
     * @param namespaceId the namespace to search in
     * @return a node with the specified id or null if none is found
     */
    public abstract MedicalNode getNodeByInternalId(String internalId, String namespaceId) throws NodeNotFoundException;

    /**
     * Find nodes based on input nodeName. NodeName can be a wildcard pattern, e.g all*. If matchSynonyms is set, the nodeName wil be matched against concept name and synonyms
     * @param nodeName - The name pattern for the nodes to find
     * @param namespaceId - The id of the namespace to search in
     * @param matchSynonyms - If true, match against synonym name and concept name
     * @return a list of MedicalNodes with names that matches the nodeName pattern.
     */
    public abstract List<MedicalNode> findNodes(String nodeName, String namespaceId, boolean matchSynonyms) throws DTSException;

    /**
     * Find nodes based on input nodeName. NodeName can be a wildcard pattern, e.g all*. If matchSynonyms is set, the nodeName wil be matched against concept name and synonyms
     * @param nodeName - The name pattern for the nodes to find
     * @param namespaceId - The id of the namespace to search in
     * @param matchSynonyms - If true, match against synonym name and concept name
     * @param numberNodes - The number of nodes to return
     * @return a list of MedicalNodes with names that matches the nodeName pattern.
     */
    public abstract List<MedicalNode> findNodes(String nodeName, String namespaceId, boolean matchSynonyms, int numberNodes) throws DTSException;

    /**
     *
     * @param namespaceId
     * @param propertyKey
     * @param propertyValue
     * @param numberNodes
     * @return
     * @throws DTSException
     */
    public abstract List<MedicalNode> findNodesByProperty(String namespaceId, String propertyKey, String propertyValue, int numberNodes) throws KeywordsException;

    /**
     * Move a node to a new parent in the tree. WARNING, if the node has several parents, these will be deleted.
     * After this operation, the node will only have one parent.
     * @param node the node to move
     * @param destinationParentNode the node to move to
     * @throws KeywordsException
     */
    public abstract void moveNode(MedicalNode node, MedicalNode destinationParentNode) throws KeywordsException, NodeNotFoundException;

    /**
     * Find nodes based on input nodeName and fetch parents to node. NodeName can be a wildcard pattern, e.g all*. If matchSynonyms is set, the nodeName wil be matched against concept name and synonyms
     * @param nodeName - The name pattern for the nodes to find
     * @param namespaceId - The id of the namespace to search in
     * @param matchSynonyms - If true, match against synonym name and concept name
     * @return a list of MedicalNodes with names that matches the nodeName pattern.
     */
    public abstract List<MedicalNode> findNodesWithParents(String nodeName, String namespaceId, boolean matchSynonyms) throws DTSException;

    public abstract MedicalNode getChildNode(String namespaceId, MedicalNode node, String childName) throws DTSException;

    public abstract List<MedicalNode> getChildNodes(MedicalNode node);
    
    public abstract MedicalNode createNewConcept(MedicalNode node) throws KeywordsException, NodeAlreadyExistsException, InvalidPropertyTypeException, NodeNotFoundException;

    public abstract void updateConcept(MedicalNode node) throws NodeNotFoundException, KeywordsException, InvalidPropertyTypeException, ParentNotFoundException;

    public abstract void createNodeProperties(MedicalNode node, boolean overwriteOProperties) throws KeywordsException, InvalidPropertyTypeException;

    public abstract long setLastChangeNow(String namespaceId) throws KeywordsException;

    /**
     * Get the last change date for the specified namespace. The last change date indicates when the namespace was last changed by the WebService. If a change has been made
     * without involving the web service, the date will not be updated.
     *
     * @param namespaceId - The id of the namespace to search in
     */
    public abstract long getLastChange(String namespaceId) throws KeywordsException;

    /**
     * Sets the username to use when connecting to the Medical Taxonomy backend
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the password to use when connecting to the Medical Taxonomy backend
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the host where the Medical Taxonomy backend is installed
     * @param host the host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Sets the port to use when connecting to the Medical Taxonomy backend
     * @param port the port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Sets the name of the namespace to run queries against in the Medical Taxonomy backend
     * @param namespaceName the name of the namespace
     */
    public void setNamespaceName(String namespaceName) {
        this.namespaceName = namespaceName;
    }

    /**
     * Sets the maximum number of results that the Medical Taxonomy backend should return
     * for each query
     * @param resultKeywordsLimit
     */
    public void setResultKeywordsLimit(int resultKeywordsLimit) {
        this.resultKeywordsLimit = resultKeywordsLimit;
    }

    /**
     * Sets the property key (source id) of a medical node
     * @param sourceIdPropertyKey
     */
    public void setSourceIdPropertyKey(String sourceIdPropertyKey) {
        this.sourceIdPropertyKey = sourceIdPropertyKey;
    }

    /**
     * Locates a namespace name by namespaceId.
     *
     * @param namespaceId
     * @return The name of the namespace
     * @throws Exception If a communication error occurs to the database, or
     * the init routine has not been invoked.
     */
    public abstract String findNamespaceById(int namespaceId) throws Exception;

    /**
     * Locates a namespace id by its name.
     *
     * @param namespaceName
     * @return The id of the namespace
     * @throws Exception If a communication error occurs to the database, or
     * the init routine has not been invoked.
     */
    public abstract String findNamespaceIdByName(String namespaceName) throws Exception;
}
