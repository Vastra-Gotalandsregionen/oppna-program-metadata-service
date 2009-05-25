package se.vgregion.metaservice.keywordservice.processing.text.stemming;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Peforms stemming of swedish words according to algorithm defined on
 * 
 * @link{http://snowball.tartarus.org/algorithms/swedish/stemmer.html
 * 
 * @author tobias
 * 
 */
public class TextProcessorStemmingSwedishImpl extends
		TextProcessorStemmingScandinavian {

	private Map<String, String> suffixes3Replace;
	private static final Logger log = Logger
			.getLogger(TextProcessorStemmingSwedishImpl.class);

	/**
	 * Creates a new instance of TextProcessorStemmingSwedishImpl
	 */
	public TextProcessorStemmingSwedishImpl() {
		suffixes3Replace = setReplaceSuffixes();

		setVowels(new HashSet<Character>(Arrays.asList('a', 'e', 'i', 'o', 'u',
				'y', 'å', 'ä', 'ö')));
		setSuffixes1(new String[] { "heterna", "hetens", "anden", "heten",
				"heter", "arnas", "ernas", "ornas", "andes", "arens", "andet",
				"arna", "erna", "orna", "ande", "arne", "aste", "aren", "ades",
				"erns", "ade", "are", "ern", "ens", "het", "ast", "ad", "en",
				"ar", "er", "or", "as", "es", "at", "a", "e" });
		setEndings(new HashSet<Character>(Arrays
				.asList('b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n',
						'o', 'p', 'r', 't', 'v', 'y')));
		setSuffixes2(new String[] { "dd", "gd", "nn", "dt", "gt", "kt", "tt" });
		setSuffixes3(new String[] { "lig", "els", "ig" });
	}

	/**
	 * Helper method to create hasMap containing the suffixes and their
	 * replacements used in rule 3 
	 * 
	 * @return a Map with suffixes and their replacements
	 */
	private Map<String, String> setReplaceSuffixes() {
		Map<String, String> suffixes = new HashMap<String, String>();
		suffixes.put("löst", "lös");
		suffixes.put("fullt", "full");

		return suffixes;
	}

	@Override
	protected String step1(String word, String r1) {
		log.debug("Entered step1()");
		String stem = deleteSuffix(word, r1, suffixes1);
		if (stem != null)
			return stem;
		// No suffix found, search for s-ending
		stem = deleteEnding1(word, r1);

		return stem == null ? word : stem;
	}

	protected String step3(String word, String r1) {
		log.debug("Entered step3()");
		String stem = deleteSuffix(word, r1, suffixes3);
		if (stem != null)
			return stem;
		// No suffixes to delete found, search for suffixes to replace.
		stem = replaceSuffix(word, r1, suffixes3Replace);

		return (stem == null ? word : stem);

	}

}
