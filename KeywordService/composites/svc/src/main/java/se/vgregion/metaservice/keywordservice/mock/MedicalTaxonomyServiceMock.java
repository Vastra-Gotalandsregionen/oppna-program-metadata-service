package se.vgregion.metaservice.keywordservice.mock;

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
import se.vgregion.metaservice.keywordservice.exception.KeywordsException;
import se.vgregion.metaservice.keywordservice.exception.NodeNotFoundException;

/**
 * Mock implementation of the MedicalTaxonomyService abstract class. Only used for testing
 * @author tobias
 *
 */
public class MedicalTaxonomyServiceMock extends MedicalTaxonomyService {
        public String[] noHitWords = new String[] {"abab", "bullulu", "mumumu"};
	
	public String[] sourceIds = new String[] {"A", "C"}; 
	
	public Map<String, List<MedicalNode>> findKeywords(String[] words, Map<Integer,String[]> sourceIds) {
		System.out.println("called mock findKeywords");
                Map<String, List<MedicalNode>> keywords = new HashMap<String, List<MedicalNode>>();
		MedicalNode node = new MedicalNode("kalle","1234");
                node.setSourceId("A");
                node.setNamespaceId("123");
                System.out.println(words.length);
		for(String word : words) {
                        System.out.println(word);
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
	
	public MedicalNode getNodeByInternalId(String internalId) {
		MedicalNode node = new MedicalNode();
		node.setInternalId(internalId);
                node.setNamespaceId("123");
		return node;
	}
	
	public List<MedicalNode> findNodes(String name, boolean matchSynonyms) {
		return new ArrayList<MedicalNode>();
	}
	
	public boolean initConnection() {
		return true;
	}

	@Override
	public MedicalNode getChildNode(MedicalNode node, String childName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MedicalNode> getChildNodes(MedicalNode node) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public List<MedicalNode> findNodesWithParents(String nodeName, boolean matchSynonyms) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void moveNode(String nodeId, String destinationParentNodeId) throws KeywordsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MedicalNode createNewConcept(MedicalNode node, String parentNodeId) throws KeywordsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateNodeProperties(MedicalNode node, boolean overwriteProperties) throws KeywordsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long setLastChangeNow() throws KeywordsException {
        return new Date().getTime();
    }

    @Override
    public long getLastChange(Identification id) throws KeywordsException {
        return new Date().getTime();
    }

    @Override
    public String findNamespaceById(int namespaceId) throws Exception {
        return "vgr";
    }


}
