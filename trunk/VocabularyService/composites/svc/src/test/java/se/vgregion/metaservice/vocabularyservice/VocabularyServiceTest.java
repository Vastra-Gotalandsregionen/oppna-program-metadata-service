package se.vgregion.metaservice.vocabularyservice;

import java.util.List;

import org.apache.log4j.Logger;

import se.vgregion.metaservice.keywordservice.domain.MedicalNode;

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
		List<MedicalNode> nodes = vocabularyService.getVocabulary(path);
		// assertNotNull(nodes);
		if (nodes != null) {
			for (MedicalNode node : nodes) {
				log.info("Node name: " + node.getName());
			}
		}
	}
}
