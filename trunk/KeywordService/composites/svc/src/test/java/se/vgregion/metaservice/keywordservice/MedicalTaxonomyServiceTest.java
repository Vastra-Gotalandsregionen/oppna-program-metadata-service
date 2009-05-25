package se.vgregion.metaservice.keywordservice;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.apelon.dts.client.concept.ConceptChild;

import se.vgregion.metaservice.keywordservice.domain.MedicalNode;

public class MedicalTaxonomyServiceTest extends BaseSpringDependencyInjectionTest {

	private static Logger log = Logger.getLogger(MedicalTaxonomyServiceTest.class);
	
	private static String[] sourceIds = {"A", "C"};
	
	private static String[] words = { "cbt", "agoraphobia", "benzodiazepin",
			"anxieti", "cognit", "behaviour", "cde" };
	private MedicalTaxonomyService mts;

	@Override
	protected void onSetUp() throws Exception {
		super.onSetUp();
		mts = (MedicalTaxonomyService) applicationContext.getBean("metaservice.medicalTaxonomyService");
		assertTrue(mts.initConnection());
	}

	public void testFindKeywords() {
		
		Map<String, List<MedicalNode>> concepts = mts.findKeywords(words, sourceIds);
		assertNotNull(concepts);
		assertTrue(concepts.size() > 0);
/*
		Iterator<Set<MedicalNode>> it = concepts.values().iterator();
		while (it.hasNext()) {
			Iterator<MedicalNode> nodeIt = it.next().iterator();
			while (nodeIt.hasNext()) {
				MedicalNode node = nodeIt.next();
				for (MedicalNode parent : node.getParents())
					System.out.println(parent.getName().toUpperCase());
				System.out.println("   " + node.getName());
			}
		}
		System.out.println("Total hits: " + concepts.size());
		*/
	}
	
	public void testFindNodeByInternalId() {
		MedicalNode node = mts.getNodeByInternalId("2149");
		assertNotNull(node);
		assertEquals("2149",node.getInternalId());
	}
}
