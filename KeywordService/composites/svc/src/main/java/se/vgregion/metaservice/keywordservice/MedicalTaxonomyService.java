package se.vgregion.metaservice.keywordservice;

import java.util.List;
import java.util.Map;
import java.util.Set;

import se.vgregion.metaservice.keywordservice.domain.MedicalNode;

import com.apelon.dts.client.concept.ConceptChild;
import com.apelon.dts.client.concept.OntylogConcept;

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
	/*******			******/
	/** QUERY PROPERTIES **/
	protected String namespaceName;
	protected int resultKeywordsLimit;
	protected String sourceIdPropertyKey = "";
	/*******			******/
	
	/**
	 * Initiates a connection to the Medical Taxonomy system. This involves connecting to the system
	 * and setting up required parameters. Needs to be called prior to any other calls to the system
	 * @return true on success, false otherwise
	 */
	public abstract boolean initConnection();
	
	/**
	 * Finds keywords in the Medical Taxonomy system based on the array of input words.
	 * A keyword in this case is a node in the Medical Taxonomy
	 * @param words an array of words to search for in the Medical Taxonomy
	 * @return a Map where each word in the input array is mapped to a List of {@link}MedicalNode with the result
	 */
	public abstract Map<String, List<MedicalNode>> findKeywords(String[] words, String[] sourceIds);
	
	public abstract ConceptChild[] getNamespaceRoots(int namespaceId);
	
	public abstract MedicalNode getNodeByInternalId(String internalId); 
	
	/**
	 * Find nodes based on input nodeName. NodeName can be a wildcard pattern, e.g all*. If matchSynonyms is set, the nodeName wil be matched against concept name and synonyms
	 * @param nodeName - The name pattern for the nodes to find
	 * @param matchSynonyms - If true, match against synonym name and concept name
	 * @return a list of MedicalNodes with names that matches the nodeName pattern.
	 */
	public abstract List<MedicalNode> findNodes(String nodeName, boolean matchSynonyms);
		
	public abstract MedicalNode getChildNode(MedicalNode node, String childName);
	
	public abstract List<MedicalNode> getChildNodes(MedicalNode node);
	
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
	public void setSourceIdPropertyKey(String sourceIdPropertyKey){
		this.sourceIdPropertyKey = sourceIdPropertyKey;
	}
}
