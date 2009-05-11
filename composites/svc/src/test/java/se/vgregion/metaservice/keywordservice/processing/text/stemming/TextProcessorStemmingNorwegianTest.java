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

public class TextProcessorStemmingNorwegianTest extends
		BaseSpringDependencyInjectionTest {

	private static Logger log = Logger.getLogger(TextProcessorStemmingNorwegianTest.class);
	TextProcessorStemmingNorwegianImpl processor = new TextProcessorStemmingNorwegianImpl();

	public void testStemAgainstVocabulary() throws IOException {
		InputStream vocabInput = this.getClass().getResourceAsStream(
				"/sample_vocab_no.txt");
		Map<String, String> correctStemmings = processVocabulary(vocabInput);
		Set<String> words = correctStemmings.keySet();
		String stem = "";
		for (String word : words) {
			stem = processor.stem(word);
			log.debug("Checking stem for "+word);
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
