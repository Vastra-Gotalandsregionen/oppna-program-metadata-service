package se.vgregion.metaservice.keywordservice.processing.text;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import se.vgregion.metaservice.keywordservice.BaseSpringDependencyInjectionTest;
import se.vgregion.metaservice.keywordservice.domain.Document;

public class TextProcessorSortWordsByFrequencyTest extends
		BaseSpringDependencyInjectionTest {

	public void testSortWords() {
		TextProcessor wordSorterProcessor = (TextProcessor) applicationContext
				.getBean("wordsSorterProcessor");
		Document document = new Document();
		Map<String, Integer> wordFreqs = new HashMap<String, Integer>();

		wordFreqs.put("sist", 25);
		wordFreqs.put("frekvent", 456);
		wordFreqs.put("delad", 120);
		wordFreqs.put("tvåa", 230);
		wordFreqs.put("trea", 120);

		document.setProperty("wordfrequency", wordFreqs);

		wordSorterProcessor.process(document);

		String content = document.getContent();
		assertNotNull(content);

		String[] words = content.split(" ");
		assertEquals(5, words.length);

		assertEquals("frekvent", words[0]);
		assertEquals("tvåa", words[1]);
		assertEquals("trea", words[2]);
		assertEquals("delad", words[3]);
        assertEquals("sist", words[4]);

	}
}
