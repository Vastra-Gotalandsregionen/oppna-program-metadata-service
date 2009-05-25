package se.vgregion.metaservice.vocabularyservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import se.vgregion.metaservice.keywordservice.MedicalTaxonomyService;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode;

/**
 * Class for handling queries for a vocabulary
 * 
 * @author tobias
 * 
 */
public class VocabularyService {

	private MedicalTaxonomyService medicalTaxonomyService;

	public List<MedicalNode> getVocabulary(String path) {
		List<MedicalNode> nodes = new ArrayList<MedicalNode>();
		path = path.charAt(0) == '/' ? path.substring(1) : path;
		String[] hierarchy = path.split("/");
		LinkedList<String> q = new LinkedList<String>(Arrays.asList(hierarchy));
		MedicalNode n = null;
		while (!q.isEmpty()) {
			n = medicalTaxonomyService.getChildNode(n, q.removeFirst());
			if (n == null)
				return nodes;
		}
		nodes = medicalTaxonomyService.getChildNodes(n);
		return nodes;
	}

	public void setMedicalTaxonomyService(
			MedicalTaxonomyService medicalTaxonomyService) {
		this.medicalTaxonomyService = medicalTaxonomyService;
	}
	
	

}
