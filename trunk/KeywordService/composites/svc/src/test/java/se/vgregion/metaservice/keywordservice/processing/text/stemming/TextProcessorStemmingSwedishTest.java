package se.vgregion.metaservice.keywordservice.processing.text.stemming;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import se.vgregion.metaservice.keywordservice.domain.document.AnalysisDocument;

public class TextProcessorStemmingSwedishTest extends
		BaseSpringDependencyInjectionTest {

	private TextProcessorStemmingSwedishImpl processor = new TextProcessorStemmingSwedishImpl();
	private String WORDS_TO_STEM = "Hammartå är en felställning i någon av småtårna. Felställningen utvecklas sakta och risken att få hammartå ökar med åldern. Allra störst är risken mellan 50 och 70 år. Felställda tår är fyra gånger vanligare hos kvinnor än hos män. Hammartå är mycket ovanligt hos barn.";
	
	public void testFindREmptyString() {
		String r1 = processor.findR("");
		assertEquals("", r1);
	}

	public void testFindRNoMatch() {
		String r1 = processor.findR("rrfggeeee");
		assertEquals("", r1);
	}

	public void testFindREmptyR() {
		String r1 = processor.findR("tior");
		assertEquals("", r1);
	}

	public void testFindR() {
		String r1 = processor.findR("kretslopp");
		assertNotNull(r1);
		assertTrue(r1.length() == 5);
		assertEquals("slopp", r1);
	}

	public void testFindRShifted() {
		String r1 = processor.findR("irrationell");
		assertNotNull(r1);
		assertTrue(r1.length() == 8);
		assertEquals("ationell", r1);
	}

	public void testFindRShiftEnd() {
		String r1 = processor.findR("yra");
		assertNotNull(r1);
		assertEquals("", r1);
	}

	public void testFindRShiftFailed() {
		String r1 = processor.findR("yr");
		assertNotNull(r1);
		assertEquals("", r1);
	}

	public void testStep1Suffix1() {
		String word = "överheterna";
		String stem = processor.step1(word, processor.findR(word));
		assertTrue(stem.length() == 4);
		assertEquals("över", stem);
	}

	public void testStep1SuffixSValid() {
		String word = "fotbolls";
		String stem = processor.step1(word, processor.findR(word));
		assertTrue(stem.length() == 7);
		assertEquals("fotboll", stem);
	}

	public void testStep1SuffixSNotValid() {
		String word = "precis";
		String stem = processor.step1(word, processor.findR(word));
		assertEquals(word, stem);
	}

	public void testStep2() {
		String word = "friskt";
		String stem = processor.step2(word, processor.findR(word));
		assertTrue(stem.length() == (word.length() - 1));
		assertEquals("frisk", stem);
	}

	public void testStep2NoDelete() {
		String word = "kyckling";
		String stem = processor.step2(word, processor.findR(word));
		assertEquals(word, stem);
	}

	public void testStep3Delete() {
		String word = "reslig";
		String expected = "res";
		String stem = processor.step3(word, processor.findR(word));
		assertEquals(expected, stem);
	}

	public void testStep3Replace() {
		String word = "respektfullt";
		String expected = "respektfull";
		String stem = processor.step3(word, processor.findR(word));
		assertEquals(expected, stem);

		word = "respektlöst";
		expected = "respektlös";

		stem = processor.step3(word, processor.findR(word));
		assertEquals(expected, stem);

	}

	public void testStemSingleWord() {
		String word = "märker";
		String expected = "märk";
		String stem = processor.stem(word);
		assertEquals(expected, stem);
	}

	public void testStemAgainstVocabulary() throws IOException {
		InputStream vocabInput = this.getClass().getResourceAsStream(
				"/sample_vocab_se.txt");
		Map<String, String> correctStemmings = processVocabulary(vocabInput);
		Set<String> words = correctStemmings.keySet();
		String stem = "";
		for (String word : words) {
			stem = processor.stem(word);
			assertEquals(stem, correctStemmings.get(word));
		}
	}
	
	public void testStemWords() {
		AnalysisDocument doc = new AnalysisDocument();
		doc.setTextContent(WORDS_TO_STEM);
		processor.process(doc);
		String[] stemmedWords = doc.getTextContent().split(" ");
		String[] unStemmedWords = WORDS_TO_STEM.split(" ");
		assertNotNull(stemmedWords);
		assertTrue(stemmedWords.length == unStemmedWords.length);
	}

	private Map<String, String> processVocabulary(InputStream input)
			throws IOException {
		Logger log = Logger.getLogger(TextProcessorStemmingSwedishTest.class);
		Map<String, String> stemMap = new HashMap<String, String>();
		BufferedReader inputBuf = new BufferedReader(new InputStreamReader(
				input,"UTF-8"));
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
