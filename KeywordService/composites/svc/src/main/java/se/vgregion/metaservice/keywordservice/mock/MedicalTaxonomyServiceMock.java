package se.vgregion.metaservice.keywordservice.mock;

import com.apelon.dts.client.DTSException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.apelon.dts.client.concept.ConceptChild;

import java.util.Date;
import se.vgregion.metaservice.keywordservice.MedicalTaxonomyService;
import se.vgregion.metaservice.keywordservice.domain.Identification;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode;
import se.vgregion.metaservice.keywordservice.exception.InvalidPropertyTypeException;
import se.vgregion.metaservice.keywordservice.exception.KeywordsException;
import se.vgregion.metaservice.keywordservice.exception.NodeNotFoundException;

/**
 * Mock implementation of the MedicalTaxonomyService abstract class. Only used for testing
 * @author tobias
 *
 */
public class MedicalTaxonomyServiceMock extends MedicalTaxonomyService {
        public String[] noHitWords = new String[] {"abab", "bullulu", "mumumu", "abab bullulu mumumu"};
	
	public String[] sourceIds = new String[] {"A", "C"}; 
	
	public Map<String, List<MedicalNode>> findKeywords(List<String> namespaceIds, String[] words, Map<Integer,String[]> sourceIds) {
                Map<String, List<MedicalNode>> keywords = new HashMap<String, List<MedicalNode>>();
		MedicalNode node = new MedicalNode("kalle","1234");
                node.setSourceId("A");
                node.setNamespaceId("123");
		for(String word : words) {
			List<MedicalNode> nodes = new ArrayList<MedicalNode>(3);
			if(!Arrays.asList(noHitWords).contains(word)) {
				nodes.add(node);
			}
			keywords.put(word,nodes);
			
		}
		
		return keywords;
	}

	public ConceptChild[] getNamespaceRoots(int namespaceId) {
		return null;
	}
	
	public MedicalNode getNodeByInternalId(String internalId, String namespaceId) {
		MedicalNode node = new MedicalNode();
		node.setInternalId(internalId);
                node.setNamespaceId("123");
		return node;
	}
	
	public List<MedicalNode> findNodes(String name, String namespaceId, boolean matchSynonyms) {
		return new ArrayList<MedicalNode>();
	}
	
	public boolean initConnection() {
		return true;
	}

	@Override
	public MedicalNode getChildNode(String namespaceId, MedicalNode node, String childName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MedicalNode> getChildNodes(MedicalNode node) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public List<MedicalNode> findNodesWithParents(String nodeName, String namespaceId, boolean matchSynonyms) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void moveNode(MedicalNode node, MedicalNode destinationParentNode) throws KeywordsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MedicalNode createNewConcept(MedicalNode node) throws KeywordsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void createNodeProperties(MedicalNode node, boolean overwriteProperties) throws KeywordsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long setLastChangeNow(String namespaceId) throws KeywordsException {
        return new Date().getTime();
    }

    @Override
    public long getLastChange(String namespaceId) throws KeywordsException {
        return new Date().getTime();
    }

    @Override
    public String findNamespaceById(int namespaceId) throws Exception {
        return "VGR";
    }

    @Override
    public String findNamespaceIdByName(String namespaceName) throws Exception {
        return "123";
    }

    @Override
    public void updateConcept(MedicalNode node) throws NodeNotFoundException, KeywordsException, InvalidPropertyTypeException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<MedicalNode> findNodes(String nodeName, String namespaceId, boolean matchSynonyms, int numberNodes) throws DTSException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<MedicalNode> findNodesByProperty(String namespaceId, String propertyKey, String propertyValue, int numberNodes) throws KeywordsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
