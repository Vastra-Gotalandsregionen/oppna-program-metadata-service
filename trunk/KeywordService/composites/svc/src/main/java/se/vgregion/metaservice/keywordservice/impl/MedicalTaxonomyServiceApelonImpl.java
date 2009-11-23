package se.vgregion.metaservice.keywordservice.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.logging.Level;
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
import com.apelon.dts.client.concept.ThesaurusConceptQuery;
import com.apelon.dts.client.namespace.Namespace;
import com.apelon.dts.client.namespace.NamespaceQuery;
import com.apelon.dts.client.subset.SubsetQuery;
import com.apelon.dts.client.term.Term;
import com.apelon.dts.client.term.TermAttributeSetDescriptor;
import com.apelon.dts.common.subset.Subset;
import java.util.Date;
import se.vgregion.metaservice.keywordservice.domain.NodeProperty;
import se.vgregion.metaservice.keywordservice.exception.KeywordsException;
import se.vgregion.metaservice.keywordservice.exception.NodeNotFoundException;
import com.apelon.dts.client.term.TermQuery;
import java.util.TreeMap;
import se.vgregion.metaservice.keywordservice.exception.InvalidPropertyTypeException;
import se.vgregion.metaservice.keywordservice.exception.NodeAlreadyExistsException;

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
    NamespaceQuery nameQuery = null;
    SearchQuery searchQuery;
    AssociationQuery assocQuery;
    NavQuery navQuery;
    TermQuery termQuery;
    SubsetQuery subsetQuery;
    ThesaurusConceptQuery thesaurusConceptQuery;
    BlacklistedWordDao blacklistedWordDao;
    //private List<String> excludeSourceIds;
    private static Logger log = Logger.getLogger(MedicalTaxonomyServiceApelonImpl.class);

    /**
     * @see MedicalTaxonomyService
     */
    public boolean initConnection() {
        log.info("Initiating connection to server " + host);
        boolean retval = true;
        try {
            ServerConnection serverConnection = new ServerConnectionSecureSocket(
                    host, port, username, password);
            serverConnection.setQueryServer(Class.forName("com.apelon.dts.server.SearchQueryServer"),
                    DTSHeader.SEARCHSERVER_HEADER);
            serverConnection.setQueryServer(
                    com.apelon.dts.server.OntylogConceptServer.class,
                    DTSHeader.ONTYLOGCONCEPTSERVER_HEADER);
            serverConnection.setQueryServer(
                    com.apelon.dts.server.NamespaceServer.class,
                    DTSHeader.NAMESPACESERVER_HEADER);

            searchQuery = (SearchQuery) SearchQuery.createInstance(serverConnection);

            nameQuery = NamespaceQuery.createInstance(serverConnection);
            namespace = nameQuery.findNamespaceByName(namespaceName);

            thesaurusConceptQuery = ThesaurusConceptQuery.createInstance(serverConnection);

            assocQuery = AssociationQuery.createInstance(serverConnection);
            navQuery = NavQuery.createInstance(serverConnection);

            termQuery = TermQuery.createInstance(serverConnection);

            subsetQuery = SubsetQuery.createInstance(serverConnection);

            ca = new ConceptAttributeSetDescriptor("Defined View ASD",
                    resultKeywordsLimit);
            ca.setAllSynonymTypes(true);
            ca.setAllPropertyTypes(true);

            DTSConceptQuery conceptQuery = DTSConceptQuery.createInstance(serverConnection);
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
    public Map<String, List<MedicalNode>> findKeywords(List<String> namespaceIds, String[] words, Map<Integer, String[]> sourceIds) {

        Map<String, List<MedicalNode>> allKeywords = new HashMap<String, List<MedicalNode>>();
        if (ca == null) {
            return allKeywords;
        }

        //Setup a cache for search options per namespace
        Map<Integer, DTSSearchOptions> optionsCache = new TreeMap<Integer, DTSSearchOptions>();
        for (String namespaceId : namespaceIds) {
            int namespaceIdInt = 0;
            AssociationType fetchAss;
            try {
                namespaceIdInt = Integer.parseInt(namespaceId);
                fetchAss = assocQuery.findAssociationTypeByName("Parent Of",
                        namespaceIdInt);
                AssociationType[] fetchAssocs = new AssociationType[]{fetchAss};
                ca.setInverseConceptAssociationTypes(fetchAssocs);
            } catch (DTSException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return allKeywords;
            } catch (NumberFormatException ex) {
                //TODO: Exception handling
                ex.printStackTrace();
                return allKeywords;
            }

            // ca.setConceptAssociationTypes(fetchAss);
            DTSSearchOptions searchOptions = new DTSSearchOptions(resultKeywordsLimit,
                    namespaceIdInt, ca);
            optionsCache.put(namespaceIdInt, searchOptions);
        }



        for (String word : words) {
            List<MedicalNode> nodes = new ArrayList<MedicalNode>();
            //Search for the word in every namespace
            for (DTSSearchOptions options : optionsCache.values()) {

                try {
                    log.debug("Searching for word " + word + " in namespace " + options.getNamespaceId());
                    OntylogConcept[] concepts = searchQuery.findConceptsWithNameMatching(word + "*", options, true);
                    log.info(MessageFormat.format(
                            "Found {0} hits for search term {1}", concepts.length,
                            word));
                    
                    // Translate each concept to MedicalNode
                    for (OntylogConcept concept : concepts) {
                        log.debug("Concept found: " + concept.getName());

                        if (shouldBeIncluded(concept, sourceIds)) {
                            MedicalNode node = createMedicalNode(concept, getSourceIdPropertyKey(), true);

                            nodes.add(node);

                            log.info(MessageFormat.format("Nodes added: {0}", nodes.size()));
                        } else {
                            log.info("Concept is excluded due to configuration properties");
                        }
                    }
                    allKeywords.put(word, nodes);

                } catch (DTSException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
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

    public long setLastChangeNow(String namespaceId) throws KeywordsException {
        //TODO: All update methods should call this?
        Long now = new Date().getTime();
        List<MedicalNode> list = findNodes("LastChange", namespaceId, false);
        MedicalNode node = null;
        if (list.isEmpty()) {
            log.error("Could not find the lastChange-Node");
            throw new KeywordsException("Could not find the lastUpdate-Node");
        } else {
            // There should only be one of these
            node = list.get(0);
            node.getProperties().clear();
            node.addProperty("lastChange", now.toString());
            try {
                //update and overwrite!
                updateNodeProperties(node, true);
            } catch (InvalidPropertyTypeException ex) {
                log.error("The apelon-setup does not have any lastChange propertyType");
                throw new KeywordsException("The apelon-setup does not have any lastChange propertyType");
            }
            return now;
        }
    }

    public long getLastChange(String namespaceId) throws KeywordsException {
        //TODO: base namespace on id
        List<MedicalNode> list = findNodes("LastChange", namespaceId, false);
        MedicalNode node = null;
        if (list.isEmpty()) {
            log.error("Could not find the lastChange-Node");
            throw new KeywordsException("Could not find the lastChange-Node");
        } else {
            // There should only be one of these
            node = list.get(0);
            List<NodeProperty> propList = node.getProperties();
            if (propList.isEmpty()) {
                log.error("Could not find the lastChange-property");
                throw new KeywordsException("Could not find the lastChange-property");
            }
            // and there should only be one of these as well
            return Long.parseLong(propList.get(0).getValue());
        }
    }

    public MedicalNode getNodeByInternalId(String internalId, String namespaceId) {
        MedicalNode node = null;
        DTSConcept concept;
        try {
            concept = getConceptByInternalId(internalId, namespaceId);
            node = createMedicalNode(concept, getSourceIdPropertyKey(), false);
        } catch (DTSException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        return node;
    }

    private DTSConcept getConceptByInternalId(String internalId, String namespaceId) throws DTSException {
        setFetchParents(ca, namespace.getId());
        DTSConcept concept = searchQuery.findConceptById(Integer.parseInt(internalId),
                Integer.parseInt(namespaceId), ca);
        return concept;
    }

    public List<MedicalNode> findNodes(String nodeName, String namespaceId, boolean matchSynonyms) {
        return findNodes(nodeName, namespaceId, matchSynonyms, false);

    }

    public List<MedicalNode> findNodesWithParents(String nodeName, String namespaceId, boolean matchSynonyms) {
        return findNodes(nodeName, namespaceId, matchSynonyms, true);
    }

    private List<MedicalNode> findNodes(String nodeName, String namespaceId, boolean matchSynonyms, boolean fetchParents) {
        List<MedicalNode> nodes = new ArrayList<MedicalNode>(100);
        setFetchParents(ca, namespace.getId());
        try {
            DTSSearchOptions options = new DTSSearchOptions(100, Integer.parseInt(namespaceId), ca);
            log.debug(MessageFormat.format("Searching for concepts with name {0}", nodeName));
            OntylogConcept[] concepts = searchQuery.findConceptsWithNameMatching(nodeName, options, matchSynonyms);
            log.debug(MessageFormat.format("Found {0} concepts", concepts.length));
            for (OntylogConcept concept : concepts) {
                MedicalNode node = createMedicalNode(concept, getSourceIdPropertyKey(), fetchParents);
                nodes.add(node);
            }
        } catch (DTSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Namespace id needs to be a valid integer", ex);
        }

        return nodes;
    }

    /**
     * Update the properties
     * @param node the node to update
     * @param owerwriteProperties overwrite properties with the same name
     * @throws KeywordsException
     */
    public void updateNodeProperties(MedicalNode node, boolean owerwriteProperties) throws KeywordsException, InvalidPropertyTypeException {
        try {
            DTSConcept concept = getConceptByInternalId(node.getInternalId(),node.getNamespaceId());

            int nrOfProperties = node.getProperties().size();
            DTSProperty[] props = new DTSProperty[nrOfProperties];

            if (node.getProperties() != null) {
                for (NodeProperty prop : node.getProperties()) {
                    DTSPropertyType pType = thesaurusConceptQuery.findPropertyTypeByName(prop.getName(), Integer.parseInt(node.getNamespaceId()));
                    if (pType == null) {
                        throw new InvalidPropertyTypeException("No such property type");
                    }

                    DTSProperty property = new DTSProperty(pType, prop.getValue());

                    if (owerwriteProperties) {
                        DTSProperty oldProperty = getFirstPropertyByName(concept, prop.getName());
                        if (oldProperty != null) {
                            thesaurusConceptQuery.deleteProperty(concept, oldProperty);
                        }
                        thesaurusConceptQuery.addProperty(concept, property);
                    } else if (!concept.containsProperty(property)) {
                        thesaurusConceptQuery.addProperty(concept, property);
                    }
                }
            }

        } catch (DTSException ex) {
            log.error("Exception setting properties for keywords in taxonomy service ", ex);
            throw new KeywordsException("Exception setting properties for keywords in taxonomy service" + ex);
        }
    }

    /**
     * Update the properties
     * @param node the node to update
     * @param owerwriteProperties overwrite properties with the same name
     * @throws KeywordsException
     */
    public void updateNodeSynonyms(MedicalNode node) throws KeywordsException {
        try {

            DTSConcept concept = getConceptByInternalId(node.getInternalId(),node.getNamespaceId());
            List<String> synonyms = node.getSynonyms();

            if (synonyms != null) {
                for (String synonymName : synonyms) {
                    AssociationType aType = getSynonymAssociationType();
                    Term term = getExistingTerm(synonymName);
                    if (term != null) {
                        log.info("Term " + synonymName + " exists, using existing");
                    } else {
                        log.info("Creating term " + synonymName);
                        term = termQuery.addTerm(new Term(synonymName, namespace.getId()));
                    }
                    Synonym syn = new Synonym(aType, term);
                    syn.setConcept(concept);
                    thesaurusConceptQuery.addSynonym(syn);
                }
            }
        } catch (DTSException ex) {
            log.error("Exception setting synonyms for keywords in taxonomy service ", ex);
            throw new KeywordsException("Exception setting synonyms for keywords in taxonomy service" + ex);
        }

    }

    private AssociationType getSynonymAssociationType() throws DTSException {
        AssociationType type = assocQuery.findAssociationTypeByName("Synonym", namespace.getId());
        return type;
    }

    private Term getExistingTerm(String name) throws DTSException {
        Term[] terms = termQuery.findTermsByName(name, namespace.getId(), TermAttributeSetDescriptor.NO_ATTRIBUTES);
        if (terms.length > 0) {
            return terms[0];
        }
        return null;
    }

    public DTSProperty getFirstPropertyByName(
            DTSConcept concept, String name) {
        DTSProperty[] props = concept.getFetchedProperties();
        for (DTSProperty prop : props) {
            if (name.equals(prop.getName())) {
                return prop;
            }

        }
        return null;
    }

    public void moveNode(MedicalNode node, MedicalNode destinationParentNode) throws KeywordsException, NodeNotFoundException {
        try {
            DTSConcept concept = getConceptByInternalId(node.getInternalId(),node.getNamespaceId());
            if (concept == null) {
                throw new NodeNotFoundException("Node " + node.getInternalId()+ " not found");
            }

            DTSConcept parentConcept = getConceptByInternalId(destinationParentNode.getInternalId(),node.getNamespaceId());
            if (parentConcept == null) {
                throw new NodeNotFoundException("Destination parent node " + " not found");
            }

            AssociationType parentRelation = getParentAssociation();
            ConceptAssociation oldParentAssociation = getParentAssociationForConcept(concept);
            ConceptAssociation newParentAssociation = new ConceptAssociation(parentConcept, parentRelation, concept);
            if (oldParentAssociation == null) // No previous parent, add parent
            {
                assocQuery.addConceptAssociation(newParentAssociation);
            } else //Update exisiting parent relation
            {
                assocQuery.updateConceptAssociation(oldParentAssociation, newParentAssociation);
            }

        } catch (DTSException ex) {
            log.error("Exception moving keywords in taxonomy service", ex);
            throw new KeywordsException("Exception moving keywords in taxonomy service");
        }

    }

    private ConceptAssociation getParentAssociationForConcept(DTSConcept concept) throws DTSException {
        ConceptAssociation[] associations = concept.getFetchedInverseConceptAssociations();
        for (ConceptAssociation assoc : associations) {
            if (assoc.getAssociationType().equals(getParentAssociation())) {
                return assoc;
            }

        }
        return null;
    }

    private AssociationType getParentAssociation() throws DTSException {
        AssociationType parentType = assocQuery.findAssociationTypeByName("Parent Of", namespace.getId());
        return parentType;
    }

    private int getSubsetId(String subsetName) {
        Subset subset = null;
        try {
            subset = subsetQuery.fetch(subsetName);
        } catch (DTSException ex) {
            java.util.logging.Logger.getLogger(MedicalTaxonomyServiceApelonImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        int subsetId = subset != null ? subset.getId() : 0;
        return subsetId;
    }

    public MedicalNode getChildNode(String namespaceId,
            MedicalNode node, String childName) {

        if (node == null) {
            log.info(MessageFormat.format("Trying to get root node {0}", childName));
            List<MedicalNode> rootNodes = findNodes(childName, namespaceId, false);
            if (rootNodes.size() == 0) {
                log.warn(MessageFormat.format("No nodes with name {0} found. Returning null", childName));
                return null;
            }

            if (rootNodes.size() == 1) {
                MedicalNode rootNode = rootNodes.get(0);
                if (rootNode.getParents().size() == 0) {
                    log.info(MessageFormat.format("Found root node {0}", childName));
                    return rootNode;
                } else { //Node has parents, not a root node
                    log.warn("Node is not a root node (has parents). Returning null");
                    return null;
                }

            } else { //If multiple nodes, loop through each node and find one without parents
                log.debug(MessageFormat.format("Multiple nodes with name {0} found", childName));
                boolean foundRootNode = false;
                MedicalNode rootNode = null;
                for (MedicalNode candidateNode : rootNodes) {
                    if (candidateNode.getParents().size() == 0) { //Is a root node
                        log.info(MessageFormat.format("Root node {0} found", childName));
                        if (!foundRootNode) { //First root node found.
                            foundRootNode = true;
                            rootNode =
                                    candidateNode;
                        } else { //Multiple root nodes exist. Abort
                            log.warn(MessageFormat.format("Multiple root nodes with name {0} found. Returning null", childName));
                            return null;
                        }
                    }
                }

                return rootNode;
            }

        } else {
            log.info(MessageFormat.format("Trying to get child {0} from node {1}", childName, node.getName()));
            try {
                DTSConcept concept = searchQuery.findConceptById(Integer.parseInt(node.getInternalId()),
                        namespace.getId(), ca);
                log.debug(MessageFormat.format("Corresponding concept for node is {0}", concept.getName()));
                ConceptChild[] conceptChildren = getChildren(concept);
                log.debug(MessageFormat.format("{0} children found for concept", conceptChildren.length));
                for (ConceptChild conceptChild : conceptChildren) {
                    log.debug(MessageFormat.format("Creating node from concept {0}", conceptChild.getName()));
                    MedicalNode childNode = createMedicalNode(conceptChild, getSourceIdPropertyKey(), false);
                    if (childNode.getName().equals(childName)) {
                        log.info(MessageFormat.format("Found child {0}", childNode.getName()));
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
            concept =
                    searchQuery.findConceptById(Integer.parseInt(node.getInternalId()),
                    namespace.getId(), ca);

            // Add all first-level children to MedicalNode
            ConceptAssociation[] children = concept.getFetchedConceptAssociations();
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

    private boolean shouldBeIncluded(OntylogConcept concept, Map<Integer, String[]> sourceIds) {
        String[] ids = sourceIds.get(concept.getNamespaceId()); //TODO: Fix mapping between current namespace and this sourceIds
        if (ids == null) {
            return true;
        }
        for (String includeId : ids) {
            for (DTSProperty property : concept.getFetchedProperties()) {
                if (property.getName().equals(getSourceIdPropertyKey())) {
                    if (property.getValue().startsWith(includeId)) {
                        log.info(MessageFormat.format("This concept should be included: {0}", property.getValue()));
                        return true;
                    }

                }
            }
        }
        return false;
    }

    /**
     * Create a new concept in apelon from a node, fix the last functionality
     * when addWord webservice is implemented
     * @param node
     * @param name
     * @param nameSpaceId
     * @param parentNodeId
     * @return
     * @throws KeywordsException
     */
    @Override
    public MedicalNode createNewConcept(
            MedicalNode node) throws KeywordsException, NodeAlreadyExistsException, InvalidPropertyTypeException, NodeNotFoundException {
        try {

            // check if any node with this name exists
            if (thesaurusConceptQuery.findConceptByName(node.getName(), Integer.valueOf(node.getNamespaceId()), ca) != null) {
                log.error("Node already exists");
                throw new NodeAlreadyExistsException("Node already exist");
            }

            // Create the concept
            DTSConcept concept = new DTSConcept(node.getName(), Integer.valueOf(node.getNamespaceId()));
            
            try {
                //add the concept
                concept = thesaurusConceptQuery.addConcept(concept);
                node.setInternalId(String.valueOf(concept.getId()));
                //create parent-realations
                if (!node.getParents().isEmpty()) {
                    String parentNodeId = node.getParents().get(0).getInternalId();
                    MedicalNode parentNode = getNodeByInternalId(parentNodeId, node.getNamespaceId());
                    moveNode(node, parentNode);
                }
                //Create the properties
                
                updateNodeProperties(node, false);

                // A new node should not have any initial children, if that changes
                // implement something to handle that here
                updateNodeSynonyms(node);

            } catch (KeywordsException ex) {
                thesaurusConceptQuery.deleteConcept(concept);
                throw ex;
            } catch (InvalidPropertyTypeException ex) {
                thesaurusConceptQuery.deleteConcept(concept);
                throw ex;
            } catch (NodeNotFoundException ex) {
                thesaurusConceptQuery.deleteConcept(concept);
                throw ex;
            }

            return createMedicalNode(concept, node.getSourceId(), false);
        } catch (DTSException ex) {
            log.info("Error when creating new node: ", ex);
            throw new KeywordsException(ex);
        }

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

        DTSProperty[] properties = concept.getFetchedProperties();

        // Add properties to MedicalNode
        for (DTSProperty property : properties) {
            node.addProperty(property.getName(), property.getValue());
        }

// get the children of a node
        ConceptAssociation[] children = concept.getFetchedConceptAssociations();
        if (children.length > 0) {
            node.setHasChildren(true);
        }

        if (fetchParents) {
            // Add all first-level parents to MedicalNode
            ConceptAssociation[] parents = concept.getFetchedInverseConceptAssociations();
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
            nodeName =
                    splitName[0].trim();
        } else {
            nodeName = concept.getFetchedPreferredTerm().getValue();

        }

        return nodeName;
    }

    private static String getSourceId(DTSConcept concept, String sourceIdKey) {
        DTSProperty[] props = concept.getFetchedProperties();
        for (DTSProperty prop : props) {
            if (prop.getName().equals(sourceIdKey)) {
                return prop.getValue();
            }

        }
        return "";
    }

    private String getSourceIdPropertyKey() {
        return this.sourceIdPropertyKey;
    }

    public String findNamespaceById(
            int namespaceId) throws Exception {
        if (nameQuery != null) {
            return nameQuery.findNamespaceById(namespaceId).getName();
        }
// not initialized

        throw new DTSException("Error finding namespaceById. MedicalTaxonomyService.initConnection() has not been invoked.");
    }

    public String findNamespaceIdByName(
            String namespaceName) throws Exception {
        if (nameQuery != null) {
            return "" + nameQuery.findNamespaceByName(namespaceName).getId();
        }
// not initialized

        throw new DTSException("Error finding namespaceIdByName. MedicalTaxonomyService.initConnection() has not been invoked.");
    }
}
