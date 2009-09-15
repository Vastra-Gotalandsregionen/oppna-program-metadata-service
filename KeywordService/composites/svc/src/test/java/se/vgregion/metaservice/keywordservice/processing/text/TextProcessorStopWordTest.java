package se.vgregion.metaservice.keywordservice.processing.text;

import java.util.HashMap;
import java.util.Map;

import se.vgregion.metaservice.keywordservice.BaseSpringDependencyInjectionTest;
import se.vgregion.metaservice.keywordservice.domain.document.AnalysisDocument;

public class TextProcessorStopWordTest extends BaseSpringDependencyInjectionTest {

	private static final String TEXT_TO_PROCESS = "a the have dependency";
	private static final String TEXT_TO_PROCESS_SE = "är alla och stol";

	private static final String STOPWORD_FILENAME_SE = "/stop_words_se.txt";
	
	public void testRemoveStopWords() {
		TextProcessor stopWordProcessor = (TextProcessor)applicationContext.getBean("stopwordProcessor");
		AnalysisDocument doc = createDocument(TEXT_TO_PROCESS);
		stopWordProcessor.process(doc);
		String processedText = doc.getTextContent();
		assertEquals("dependency", processedText.trim());
	}
	
	public void testRemoveStopWordsSe() {
		TextProcessorStopWordImpl stopWordProcessor = (TextProcessorStopWordImpl)applicationContext.getBean("stopwordProcessor");
		stopWordProcessor.setStopWordList(STOPWORD_FILENAME_SE);
		
		AnalysisDocument doc = createDocument(TEXT_TO_PROCESS_SE);
		stopWordProcessor.process(doc);
		String processedText = doc.getTextContent();
		assertEquals("stol", processedText.trim());
		
		
	}
	
	private static AnalysisDocument createDocument(String content) {
		AnalysisDocument doc = new AnalysisDocument();
		doc.setTextContent(content);
		
		return doc;
	}
}
