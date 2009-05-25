package se.vgregion.metaservice.keywordservice.processing.text.stemming;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import se.vgregion.metaservice.keywordservice.BaseSpringDependencyInjectionTest;

public class TextProcessorStemmingDanishTest extends
		BaseSpringDependencyInjectionTest {

	private static Logger log = Logger.getLogger(TextProcessorStemmingDanishTest.class);
	TextProcessorStemmingDanishImpl processor = new TextProcessorStemmingDanishImpl();

	public void testEndsWithDoubleConsonants() {
		String word = "bestemm";
		
		assertTrue(processor.hasDoubleConsonantEnding(word,processor.findR(word)));
	
		
	}
	
	public void testEndsWithDoubleConsonantsToShort() {
		String word = "";
		assertFalse(processor.hasDoubleConsonantEnding(word,""));
		
		word = "b";
		assertFalse(processor.hasDoubleConsonantEnding(word,""));
	}
	
	public void testEndsWithDoubleConsonantsFalse() {
		String word = "bestemmer";
		assertFalse(processor.hasDoubleConsonantEnding(word, processor.findR(word)));
		
		word = "bestemt";
		assertFalse(processor.hasDoubleConsonantEnding(word,processor.findR(word)));
	}
	
	public void testStep4() {
		String word = "bestemm";
		String expected ="bestem";
		String r1 = processor.findR(word);
		assertEquals(expected, processor.step4(word, r1));
	}
	
	public void testStep4ToShort() {
		String word = "";
		String r1 = processor.findR(word);
		assertEquals(word, processor.step4(word, r1));
		
		word = "b";
		r1 = processor.findR(word);
		assertEquals(word, processor.step4(word, r1));
	}
	
	public void testStep4False() {
		String word ="bestemmelse";
		String r1 = processor.findR(word);
		assertEquals(word, processor.step4(word, r1));
	}
	
	public void testStep3A() {
		log.debug("Entered testStep3A()");
		String[] words = new String[]{"indvendig","adelig","almindelig","akitofels","skrøbeligst"};
		String[] expecteds = new String[]{"indvend","ade","almind","akitof","skrøb"};
		
		
		
		for(int i=0; i<words.length; i++) {
			String r1 = processor.findR(words[i]);
			assertEquals(expecteds[i], processor.step3(words[i],r1));
		}
	}
	
	public void testStep3B() {
		String word = "barnløst";
		String expected ="barnløs";
		String r1 = processor.findR(word);
		assertEquals(expected, processor.step3(word, r1));
	}
	
	public void testStep2() {
		String[] words = new String[]{"menneskemængd","adlydt","adspurgt","betænkt"};
		String[] expecteds = new String[]{"menneskemæng","adlyd","adspurg","betænk"};
		
		for(int i=0; i<words.length; i++) {
			log.debug("Testing word "+words[i]);
			String r1 = processor.findR(words[i]);
			assertEquals(expecteds[i], processor.step2(words[i],r1));
		}
	}
	
	public void testStemAgainstVocabulary() throws IOException {
		InputStream vocabInput = this.getClass().getResourceAsStream(
				"/sample_vocab_dk.txt");
		Map<String, String> correctStemmings = processVocabulary(vocabInput);
		Set<String> words = correctStemmings.keySet();
		String stem = "";
		for (String word : words) {
			log.debug("Checking stem for "+word);
			stem = processor.stem(word);
			assertEquals(stem, correctStemmings.get(word));
		}
	}

	private Map<String, String> processVocabulary(InputStream input)
			throws IOException {
		Map<String, String> stemMap = new HashMap<String, String>();
		BufferedReader inputBuf = new BufferedReader(new InputStreamReader(
				input, "UTF-8"));
		String line = inputBuf.readLine();
		StringTokenizer tokenizer = null;
		String token1 = "";
		String token2 = "";
		while (line != null) {
			tokenizer = new StringTokenizer(line);
			assertTrue(tokenizer.countTokens() == 2);
			token1 = tokenizer.nextToken().trim();
			token2 = tokenizer.nextToken().trim();
			log.debug(MessageFormat.format("Putting pair {0} : {1} in map",
					token1, token2));
			stemMap.put(token1, token2);
			line = inputBuf.readLine();
		}

		inputBuf.close();
		return stemMap;
	}

}
