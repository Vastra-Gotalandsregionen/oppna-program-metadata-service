package se.vgregion.metaservice.keywordservice.processing.text;

import se.vgregion.metaservice.keywordservice.BaseOpenJpaTest;
import se.vgregion.metaservice.keywordservice.domain.Document;

public class TextProcessorRemoveBlacklistedWordsTest extends BaseOpenJpaTest {

	public void testRemoveBlacklistedWords() {
		TextProcessor blacklistedWordRemoverProcessor = (TextProcessor) applicationContext
				.getBean("blacklistedWordsProcessor");
		Document document = new Document();
		document.setContent("hello");
		document.setTitle("test");

		blacklistedWordRemoverProcessor.process(document);

		String content = document.getContent();

		assertNotNull(content);

	}
}
