package se.vgregion.metaservice.keywordservice.processing.text;

import java.util.HashMap;
import java.util.Map;

import se.vgregion.metaservice.keywordservice.BaseSpringDependencyInjectionTest;
import se.vgregion.metaservice.keywordservice.domain.Document;

public class TextProcessorStopWordTest extends BaseSpringDependencyInjectionTest {

	private static final String TEXT_TO_PROCESS = "a the have dependency";
	private static final String TEXT_TO_PROCESS_SE = "är alla och stol";

	private static final String STOPWORD_FILENAME_SE = "/stop_words_se.txt";
	
	public void testRemoveStopWords() {
		TextProcessor stopWordProcessor = (TextProcessor)applicationContext.getBean("stopwordProcessor");
		Document doc = createDocument(TEXT_TO_PROCESS);
		stopWordProcessor.process(doc);
		String processedText = doc.getContent();
		assertEquals("dependency", processedText.trim());
	}
	
	public void testRemoveStopWordsSe() {
		TextProcessorStopWordImpl stopWordProcessor = (TextProcessorStopWordImpl)applicationContext.getBean("stopwordProcessor");
		stopWordProcessor.setStopWordList(STOPWORD_FILENAME_SE);
		
		Document doc = createDocument(TEXT_TO_PROCESS_SE);
		stopWordProcessor.process(doc);
		String processedText = doc.getContent();
		assertEquals("stol", processedText.trim());
		
		
	}
	
	private static Document createDocument(String content) {
		Document doc = new Document();
		doc.setContent(content);
		
		return doc;
	}
}
