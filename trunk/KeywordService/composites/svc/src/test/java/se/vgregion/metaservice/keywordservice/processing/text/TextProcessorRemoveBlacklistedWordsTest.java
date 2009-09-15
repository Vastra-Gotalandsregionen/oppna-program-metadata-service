package se.vgregion.metaservice.keywordservice.processing.text;

import se.vgregion.metaservice.keywordservice.BaseOpenJpaTest;
import se.vgregion.metaservice.keywordservice.domain.document.AnalysisDocument;

public class TextProcessorRemoveBlacklistedWordsTest extends BaseOpenJpaTest {

	public void testRemoveBlacklistedWords() {
		TextProcessor blacklistedWordRemoverProcessor = (TextProcessor) applicationContext
				.getBean("blacklistedWordsProcessor");
		AnalysisDocument document = new AnalysisDocument();
		document.setTextContent("hello");
		document.setTitle("test");

		blacklistedWordRemoverProcessor.process(document);

		String content = document.getTextContent();

		assertNotNull(content);

	}
}
