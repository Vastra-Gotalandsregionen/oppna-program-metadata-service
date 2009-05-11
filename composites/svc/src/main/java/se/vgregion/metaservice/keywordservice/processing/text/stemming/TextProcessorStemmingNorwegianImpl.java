package se.vgregion.metaservice.keywordservice.processing.text.stemming;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Peforms stemming of norwegian words according to algorithm defined on
 * 
 * @link{http://snowball.tartarus.org/algorithms/norwegian/stemmer.html}
 * 
 * @author tobias
 * 
 */
public class TextProcessorStemmingNorwegianImpl extends
		TextProcessorStemmingScandinavian {

	private Map<String, String> suffixes1Replace;
	private static final Logger log = Logger
			.getLogger(TextProcessorStemmingNorwegianImpl.class);

	/**
	 * Creates a new instance of TextProcessorStemmingSwedishImpl
	 */
	public TextProcessorStemmingNorwegianImpl() {
		
		createSuffixes1Replace();
		
		setVowels(new HashSet<Character>(Arrays.asList('a', 'e', 'i', 'o', 'u',
				'y', 'å', 'æ', 'ø')));
		setSuffixes1(new String[] { "hetenes", "hetene", "hetens", "heten",
				"heter", "endes", "ande", "ende", "edes", "enes", "ede", "ane",
				"ene", "ens", "ers", "ets", "het", "ast", "en", "ar", "er",
				"as", "es", "et", "a", "e"});
		setEndings(new HashSet<Character>(Arrays
				.asList('b', 'c', 'd', 'f', 'g', 'h', 'j', 'l', 'm', 'n', 'o',
						'p', 'r', 't', 'v', 'y', 'z')));
		setSuffixes2(new String[] { "dt", "vt"});
		setSuffixes3(new String[] {"hetslov", "eleg", "elig", "elov", "slov", "leg", "eig", "lig", "els", "lov", "ig"});
	}

	@Override
	protected String step1(String word, String r1) {
		log.debug("Entered step1()");
		

		String stem = replaceSuffix(word, r1, suffixes1Replace);

		if(stem != null)
			return stem;
		
		stem = deleteSuffix(word, r1, suffixes1);
		if (stem != null)
			return stem;
		// No suffix found, search for s-ending
		stem = deleteEnding1(word, r1);
		if (stem != null)
			return stem;
		// Special case for norwegian, rule 1b
		if (r1.endsWith("s")) {
			if (word.length() >= 3) {
				if (word.charAt(word.length() - 2) == 'k'
						&& !vowels.contains(word.charAt(word.length() - 3)))
					stem = word.substring(0, word.length() - 1);
			}
		}

		
		return stem == null ? word : stem;
	}

	protected String step3(String word, String r1) {
		log.debug("Entered step3()");
		String stem = deleteSuffix(word, r1, suffixes3);

		return (stem == null ? word : stem);

	}
	
	private void createSuffixes1Replace() {
		suffixes1Replace = new HashMap<String, String>(2);
		suffixes1Replace.put("erte", "er");
		suffixes1Replace.put("ert", "er");
	}

}
