package se.vgregion.metaservice.keywordservice.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.apelon.dts.client.concept.ConceptChild;

import se.vgregion.metaservice.keywordservice.MedicalTaxonomyService;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode;

/**
 * Mock implementation of the MedicalTaxonomyService abstract class. Only used for testing
 * @author tobias
 *
 */
public class MedicalTaxonomyServiceMock extends MedicalTaxonomyService {

	public String[] noHitWords = new String[] {"abab", "bullulu", "mumumu"};
	
	public String[] sourceIds = new String[] {"A", "C"}; 
	
	public Map<String, List<MedicalNode>> findKeywords(String[] words, String[] sourceIds) {
		Map<String, List<MedicalNode>> keywords = new HashMap<String, List<MedicalNode>>();
		MedicalNode node = new MedicalNode("kalle","1234");
        node.setSourceId("A");
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
	
	public MedicalNode getNodeByInternalId(String internalId) {
		MedicalNode node = new MedicalNode();
		node.setInternalId(internalId);
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

}
