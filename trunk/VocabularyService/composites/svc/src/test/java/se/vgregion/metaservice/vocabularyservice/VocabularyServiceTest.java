package se.vgregion.metaservice.vocabularyservice;

import java.util.List;

import org.apache.log4j.Logger;

import org.w3c.dom.NodeList;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode;
import se.vgregion.metaservice.keywordservice.domain.NodeListResponseObject;

public class VocabularyServiceTest extends BaseSpringDependencyInjectionTest {

	private VocabularyService vocabularyService;
	private static Logger log = Logger.getLogger(VocabularyServiceTest.class);

	@Override
	protected void onSetUp() throws Exception {
		super.onSetUp();
		vocabularyService = (VocabularyService) applicationContext
				.getBean("metaservice.vocabularyService");
	}

	public void testGetVocabulary() {
		String path = "Dokumenttyper/MIME/";
		NodeListResponseObject response = vocabularyService.getVocabulary("m123",path,null);
        List<MedicalNode> nodes = response.getNodeList();
		// assertNotNull(nodes);
		if (nodes != null) {
			for (MedicalNode node : nodes) {
				log.info("Node name: " + node.getName());
			}
		}
	}
}
