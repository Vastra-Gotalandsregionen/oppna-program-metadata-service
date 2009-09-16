package se.vgregion.metaservice.keywordservice.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import se.vgregion.metaservice.keywordservice.MedicalTaxonomyService;
import se.vgregion.metaservice.keywordservice.dao.BlacklistedWordDao;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode;

import com.apelon.apelonserver.client.ApelonException;
import com.apelon.apelonserver.client.ServerConnection;
import com.apelon.apelonserver.client.ServerConnectionSecureSocket;
import com.apelon.dts.client.DTSException;
import com.apelon.dts.client.association.AssociationQuery;
import com.apelon.dts.client.association.AssociationType;
import com.apelon.dts.client.association.ConceptAssociation;
import com.apelon.dts.client.association.Synonym;
import com.apelon.dts.client.attribute.DTSProperty;
import com.apelon.dts.client.attribute.DTSPropertyType;
import com.apelon.dts.client.common.DTSHeader;
import com.apelon.dts.client.concept.ConceptAttributeSetDescriptor;
import com.apelon.dts.client.concept.ConceptChild;
import com.apelon.dts.client.concept.DTSConcept;
import com.apelon.dts.client.concept.DTSConceptQuery;
import com.apelon.dts.client.concept.DTSSearchOptions;
import com.apelon.dts.client.concept.NavChildContext;
import com.apelon.dts.client.concept.NavQuery;
import com.apelon.dts.client.concept.OntylogConcept;
import com.apelon.dts.client.concept.SearchQuery;
import com.apelon.dts.client.match.MatchQuery;
import com.apelon.dts.client.namespace.Namespace;
import com.apelon.dts.client.namespace.NamespaceQuery;
import java.lang.Integer;

/**
 * Implementation of the abstract class MedicalTaxonomyService. This
 * implementation connects to Apelon backend for retrieving keywords.
 * 
 * @author tobias
 * 
 */
public class MedicalTaxonomyServiceApelonImpl extends MedicalTaxonomyService {

	private Namespace namespace;
	private ConceptAttributeSetDescriptor ca;
	NamespaceQuery nameQuery;
	SearchQuery searchQuery;
	AssociationQuery assocQuery;
	NavQuery navQuery;

	BlacklistedWordDao blacklistedWordDao;
	
	//private List<String> excludeSourceIds;

	private static Logger log = Logger
			.getLogger(MedicalTaxonomyServiceApelonImpl.class);

	/**
	 * @see MedicalTaxonomyService
	 */
	public boolean initConnection() {
		boolean retval = true;
		try {
			ServerConnection serverConnection = new ServerConnectionSecureSocket(
					host, port, username, password);
			serverConnection.setQueryServer(Class
					.forName("com.apelon.dts.server.SearchQueryServer"),
					DTSHeader.SEARCHSERVER_HEADER);
			serverConnection.setQueryServer(
					com.apelon.dts.server.OntylogConceptServer.class,
					DTSHeader.ONTYLOGCONCEPTSERVER_HEADER);
			serverConnection.setQueryServer(
					com.apelon.dts.server.NamespaceServer.class,
					DTSHeader.NAMESPACESERVER_HEADER);

			searchQuery = (SearchQuery) SearchQuery
					.createInstance(serverConnection);

			nameQuery = NamespaceQuery.createInstance(serverConnection);
			namespace = nameQuery.findNamespaceByName(namespaceName);

			assocQuery = AssociationQuery.createInstance(serverConnection);
			navQuery = NavQuery.createInstance(serverConnection);

			ca = new ConceptAttributeSetDescriptor("Defined View ASD",
					resultKeywordsLimit);
			ca.setAllSynonymTypes(true);

			DTSConceptQuery conceptQuery = DTSConceptQuery
					.createInstance(serverConnection);
			DTSPropertyType propertyType = conceptQuery.findPropertyTypeByName(
					getSourceIdPropertyKey(), namespace.getId());
			if (propertyType != null) {
				ca.addPropertyType(propertyType);
			} else {
				log.warn(MessageFormat.format("Specified property {0} could not be found in taxonomy", getSourceIdPropertyKey()));
			}

		} catch (ApelonException e) {
			// TODO Auto-generated catch block
			retval = false;
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			retval = false;
			e.printStackTrace();
		} catch (DTSException e) {
			// TODO Auto-generated catch block
			retval = false;
			e.printStackTrace();
		}

		return retval;

	}

	/**
	 * @see MedicalTaxonomyService
	 */
	public Map<String, List<MedicalNode>> findKeywords(String[] words, Map<Integer,String[]> sourceIds) {
		Map<String, List<MedicalNode>> allKeywords = new HashMap<String, List<MedicalNode>>();
		if (namespace == null || ca == null)
			return allKeywords;
		/*
		 * AssociationType[] fetchAss = { new AssociationType("Child Of", 7,
		 * "CHD", namespace.getId(), ItemsConnected.CONCEPTS, Purpose.ARBITRARY,
		 * AssociationType.NOT_DISPLAYABLE) };
		 */
		AssociationType fetchAss;
		try {
			fetchAss = assocQuery.findAssociationTypeByName("Parent Of",
					namespace.getId());
			AssociationType[] fetchAssocs = new AssociationType[] { fetchAss };
			ca.setInverseConceptAssociationTypes(fetchAssocs);
		} catch (DTSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return allKeywords;
		}

		// ca.setConceptAssociationTypes(fetchAss);
		DTSSearchOptions options = new DTSSearchOptions(resultKeywordsLimit,
				namespace.getId(), ca);

		for (String word : words) {
			try {
				OntylogConcept[] concepts = searchQuery
						.findConceptsWithNameMatching(word + "*", options, true);
				log.info(MessageFormat.format(
						"Found {0} hits for search term {1}", concepts.length,
						word));
				List<MedicalNode> nodes = new ArrayList<MedicalNode>(
						concepts.length);
				// Translate each concept to MedicalNode
				for (OntylogConcept concept : concepts) {
					log.debug("Concept found: " + concept.getName());
                    
					if (shouldBeIncluded(concept, sourceIds)) {
						MedicalNode node = createMedicalNode(concept, getSourceIdPropertyKey(), true);

						nodes.add(node);

						log.info(MessageFormat.format("Nodes added: {0}", nodes
								.size()));
					}
					else {
						log.info("Concept is excluded due to configuration properties");
					}	
				}
				allKeywords.put(word, nodes);

			} catch (DTSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return allKeywords;
	}

	public ConceptChild[] getNamespaceRoots(int namespaceId) {
		ConceptChild[] roots = null;
		try {
			
			
			roots = navQuery.getConceptChildRoots(ca, namespaceId);
		} catch (DTSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return roots;
	}
	
	public MedicalNode getNodeByInternalId(String internalId) {
		MedicalNode node = null;
		setFetchParents(ca, namespace.getId());
		try {
			DTSConcept concept = searchQuery.findConceptById(Integer.parseInt(internalId),
					namespace.getId(), ca);
			node = createMedicalNode(concept, getSourceIdPropertyKey(), false);
		} catch (DTSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return node;
	}
	

	public List<MedicalNode> findNodes(String nodeName, boolean matchSynonyms) {
		List<MedicalNode> nodes = new ArrayList<MedicalNode>(100);
		setFetchParents(ca, namespace.getId());
		DTSSearchOptions options = new DTSSearchOptions(100,namespace.getId(), ca);
		try {
			log.debug(MessageFormat.format("Searching for concepts with name {0}",nodeName));
			OntylogConcept[] concepts = searchQuery.findConceptsWithNameMatching(nodeName, options, matchSynonyms);
			log.debug(MessageFormat.format("Found {0} concepts",concepts.length));
			for(OntylogConcept concept : concepts) {
				MedicalNode node = createMedicalNode(concept, getSourceIdPropertyKey(), false);
				nodes.add(node);
			}
		} catch (DTSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return nodes;
		
	}
	
	public MedicalNode getChildNode(MedicalNode node, String childName) {
		
		if(node == null) {
			log.info(MessageFormat.format("Trying to get root node {0}",childName));
			List<MedicalNode> rootNodes = findNodes(childName, false);
			if(rootNodes.size() == 0) {
				log.warn(MessageFormat.format("No nodes with name {0} found. Returning null",childName));
				return null;
			}
			if(rootNodes.size() == 1) {
				MedicalNode rootNode = rootNodes.get(0);
				if(rootNode.getParents().size() == 0) {
					log.info(MessageFormat.format("Found root node {0}",childName));
					return rootNode;
				}
				else { //Node has parents, not a root node
					log.warn("Node is not a root node (has parents). Returning null");
					return null;
				}
			}
			else { //If multiple nodes, loop through each node and find one without parents
				log.debug(MessageFormat.format("Multiple nodes with name {0} found",childName));
				boolean foundRootNode = false;
				MedicalNode rootNode = null;
				for(MedicalNode candidateNode : rootNodes) {
					if(candidateNode.getParents().size() == 0) { //Is a root node
						log.info(MessageFormat.format("Root node {0} found", childName));
						if(!foundRootNode) { //First root node found.
							foundRootNode = true;
							rootNode = candidateNode;
						}
						else { //Multiple root nodes exist. Abort
							log.warn(MessageFormat.format("Multiple root nodes with name {0} found. Returning null",childName));
							return null;
						}
					}
				}
				
				return rootNode; 
			}
		}
		else {
			log.info(MessageFormat.format("Trying to get child {0} from node {1}",childName,node.getName()));
			try {
				DTSConcept concept = searchQuery.findConceptById(Integer.parseInt(node.getInternalId()),
						namespace.getId(), ca);
				log.debug(MessageFormat.format("Corresponding concept for node is {0}",concept.getName()));
				ConceptChild[] conceptChildren = getChildren(concept);
				log.debug(MessageFormat.format("{0} children found for concept",conceptChildren.length));
				for(ConceptChild conceptChild : conceptChildren) {
					log.debug(MessageFormat.format("Creating node from concept {0}",conceptChild.getName()));
					MedicalNode childNode = createMedicalNode(conceptChild, getSourceIdPropertyKey(), false);
					if(childNode.getName().equals(childName)) {
						log.info(MessageFormat.format("Found child {0}",childNode.getName()));
						return childNode;
					}
				}
			} catch (DTSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public List<MedicalNode> getChildNodes(MedicalNode node) {
		List<MedicalNode> nodes = new ArrayList<MedicalNode>();
		DTSConcept concept;
		try {
			AssociationType fetchAss = assocQuery.findAssociationTypeByName("Parent Of", namespace.getId());
			ca.addConceptAssociationType(fetchAss);
			concept = searchQuery.findConceptById(Integer.parseInt(node.getInternalId()),
					namespace.getId(), ca);
			
			// Add all first-level children to MedicalNode
			ConceptAssociation[] children = concept
					.getFetchedConceptAssociations();
			log.info(MessageFormat.format("Concept has {0} children",
					children.length));
			for (ConceptAssociation assoc : children) {
				DTSConcept lazyChild = assoc.getToConcept();
				DTSConcept child;
				try {
					child = searchQuery.findConceptById(lazyChild.getId(),
							namespace.getId(), ca);

					String parentName = getName(child);
					MedicalNode childMedNode = createMedicalNode(child,
							getSourceIdPropertyKey(), false);
					nodes.add(childMedNode);
				} catch (DTSException e) {
					log.warn(MessageFormat.format("Child not found: {0}",
							lazyChild.getName()));
				}
			}
			
		} catch (DTSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return nodes;
		
	}

	private ConceptChild[] getChildren(DTSConcept concept) throws DTSException {
		AssociationType fetchAss = assocQuery.findAssociationTypeByName("Parent Of", namespace.getId());
		ca.addConceptAssociationType(fetchAss);
		NavChildContext childContext = navQuery.getNavChildContext(concept, ca, fetchAss);
		return childContext.getChildren();
	}
	
	private void setFetchParents(ConceptAttributeSetDescriptor ca, int namespaceId) {
		AssociationType fetchAss;

		try {
			fetchAss = assocQuery.findAssociationTypeByName("Parent Of",
					namespaceId);

			ca.addInverseConceptAssociationType(fetchAss);
		} catch (DTSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	private boolean shouldBeIncluded(OntylogConcept concept, Map<Integer,String[]> sourceIds) {
		String[] ids = sourceIds.get(123); //TODO: Fix mapping between current namespace and this sourceIds
		for (String includeId : ids) {
			for (DTSProperty property : concept.getFetchedProperties()) {	
				if (property.getName().equals(getSourceIdPropertyKey())) {
					if (property.getValue().startsWith(includeId)) {
						log.info(MessageFormat
								.format("This concept should be included: {0}", property.getValue()));
						return true;
					}
				}
			}
		}
		
		return false;
	}

	protected MedicalNode createMedicalNode(DTSConcept concept,
			String sourceIdKey, boolean fetchParents) {
		MedicalNode node = new MedicalNode();
		node.setName(getName(concept));
		node.setInternalId(String.valueOf(concept.getId()));
		node.setSourceId(getSourceId(concept, sourceIdKey));
		node.setNamespaceId(String.valueOf(concept.getNamespaceId()));

		Synonym[] synonyms = concept.getFetchedSynonyms();

		// Add synonyms to MedicalNode
		for (Synonym synonym : synonyms) {
			node.addSynonym(synonym.getValue());
		}

		// get the children of a node
		ConceptAssociation[] children = concept.getFetchedConceptAssociations();
		if(children.length > 0)
		 node.setHasChildren(true);
		 
		if (fetchParents) {
			// Add all first-level parents to MedicalNode
			ConceptAssociation[] parents = concept
					.getFetchedInverseConceptAssociations();
			// ConceptAssociation[] parents =
			// concept.getFetchedConceptAssociations();
			log.info(MessageFormat.format("Concept has {0} parents",
					parents.length));
			for (ConceptAssociation assoc : parents) {
				DTSConcept lazyParent = assoc.getFromConcept();
				DTSConcept parent;
				try {
					parent = searchQuery.findConceptById(lazyParent.getId(),
							namespace.getId(), ca);

					String parentName = getName(parent);
					MedicalNode parentMedNode = createMedicalNode(parent,
							sourceIdKey, false);
					node.addParent(parentMedNode);
					log.debug(MessageFormat.format("Parent added to node: {0}",
							parentMedNode));
				} catch (DTSException e) {
					log.warn(MessageFormat.format("Parent not found: {0}",
							lazyParent.getName()));
				}
			}
		}

		return node;

	}

	/*
	public void setExcludeSourceIds(List<String> excludeSourceIds) {
		this.excludeSourceIds = excludeSourceIds;
	}
   */
	
	public void setBlacklistedWordDao(BlacklistedWordDao blacklistedWordDao) {
		this.blacklistedWordDao = blacklistedWordDao;
	}

	/**
	 * Retrieves name from concept
	 */
	private static String getName(DTSConcept concept) {
		Synonym preferredSynonym = concept.getFetchedPreferredTerm();
		String nodeName = null;
		if (preferredSynonym == null) {
			// Remove [a-z0-9] portion of node name.
			String[] splitName = concept.getName().split("\\[");
			nodeName = splitName[0].trim();
		} else {
			nodeName = concept.getFetchedPreferredTerm().getValue();

		}
		return nodeName;
	}

	private static String getSourceId(DTSConcept concept, String sourceIdKey) {
		DTSProperty[] props = concept.getFetchedProperties();
		for (DTSProperty prop : props) {
			if (prop.getName().equals(sourceIdKey))
				return prop.getValue();
		}
		return "";
	}

	private String getSourceIdPropertyKey() {
		return this.sourceIdPropertyKey;
	}
}
