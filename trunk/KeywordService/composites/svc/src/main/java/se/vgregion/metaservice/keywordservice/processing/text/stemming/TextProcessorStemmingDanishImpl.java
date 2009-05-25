package se.vgregion.metaservice.keywordservice.processing.text.stemming;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;

public class TextProcessorStemmingDanishImpl extends
		TextProcessorStemmingScandinavian {

	private Map<String, String> suffixes3Replace;
	private static final Logger log = Logger
			.getLogger(TextProcessorStemmingDanishImpl.class);

	/**
	 * Creates a new instance of TextProcessorStemmingSwedishImpl
	 */
	public TextProcessorStemmingDanishImpl() {

		createSuffixes3Replace();

		setVowels(new HashSet<Character>(Arrays.asList('a', 'e', 'i', 'o', 'u',
				'y', 'å', 'æ', 'ø')));
		setSuffixes1(new String[] { "erendes", "erende", "hedens", "ethed",
				"erede", "heden", "heder", "endes", "ernes", "erens", "erets",
				"ered", "ende", "erne", "eren", "erer", "heds", "enes", "eres",
				"eret", "hed", "ene", "ere", "ens", "ers", "ets", "en", "er",
				"es", "et", "e"});
		setEndings(new HashSet<Character>(Arrays.asList('a', 'b', 'c', 'd',
				'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'r', 't',
				'v', 'y', 'z', 'å')));
		setSuffixes2(new String[] {"gd","dt","gt","kt"});
		setSuffixes3(new String[] { "elig", "lig", "els", "ig"});
	}
	
	

	@Override
	protected String stem(String word) {
		String stem = super.stem(word); //Perform step 1-3
		String r1 = findR(stem);
		stem = step4(stem, r1);
		log.debug(MessageFormat.format("Stem is {0} after step 4", stem));

		return stem;
	}


	protected String step3(String word, String r1) {
		log.debug("Entered step3() with word "+word);
		String stem = null;
		if(word.endsWith("igst")) { //If ends with igst, remove st
			word = word.substring(0,word.length()-2);
			r1 = findR(word);
			log.debug("Found suffix \"igst\", word is now "+word);
		}
		
		
		
		log.debug("Check for suffix to delete");
		stem = deleteSuffix(word, r1, suffixes3);
		if(stem != null) { //if suffix found and deleted, repeat step2
			log.debug("Suffix found and deleted, calling step2 again");
			r1 = findR(stem);
			stem = step2(stem, r1);
			return stem;
		}
		log.debug("Check for endings to replace");
		stem = replaceSuffix(word, r1, suffixes3Replace);
		

		return (stem == null ? word : stem);

	}
	
	protected String step4(String word, String r1) {
		log.debug("Entered step4()");
		if(word.length() > 1 && hasDoubleConsonantEnding(word, r1)) {
			log.debug("Word ends with double consonants, removing last consonant");
			String stem = word.substring(0,word.length()-1);
			return stem;
		}
		
		return word;
	}
	
	protected boolean hasDoubleConsonantEnding(String word, String r1) {
		boolean status = false;
		if(word.length() < 2 || r1.length() == 0)
			status = false;
		else {
			char lastChar = word.charAt(word.length()-1);
			char lastCharR1 = r1.charAt(r1.length()-1);
			log.debug("Last char in word is "+lastChar+", in r1: "+lastCharR1);
			if(!vowels.contains(lastChar) && (lastChar == word.charAt(word.length()-2)) && (lastChar == lastCharR1))
				status = true;
		}
		return status;
	}

	private void createSuffixes3Replace() {
		suffixes3Replace = new HashMap<String, String>(1);
		suffixes3Replace.put("løst", "løs");
	}

}
