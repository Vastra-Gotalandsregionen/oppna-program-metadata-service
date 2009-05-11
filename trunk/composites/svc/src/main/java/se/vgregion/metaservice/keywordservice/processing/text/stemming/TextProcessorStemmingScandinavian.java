package se.vgregion.metaservice.keywordservice.processing.text.stemming;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import se.vgregion.metaservice.keywordservice.domain.Document;
import se.vgregion.metaservice.keywordservice.processing.text.TextProcessor;

public abstract class TextProcessorStemmingScandinavian extends TextProcessor {

	private static Logger log = Logger.getLogger(TextProcessorStemmingScandinavian.class);
	protected HashSet<Character> vowels;
	protected String[] suffixes1;
	protected String[] suffixes2;
	protected String[] suffixes3;
	protected HashSet<Character> endings;
	
	/**
	 * Performs step1 of the algorithm by removing suffixes.
	 * @param word The word to stem
	 * @param r1 The R1 section of the word
	 * @return the given word without suffix, if suffix found.
	 */
	protected String step1(String word, String r1) {
		log.debug("Entered step1()");
		String stem = deleteSuffix(word, r1,suffixes1);
		if(stem != null)
			return stem;
		// No suffix found, search for s-ending
		stem = deleteEnding1(word, r1);
		
		return stem == null ? word : stem;
	}
	

	/**
	 * Performs step3 (the final step) in the algorithm by either remove or replace the suffix.
	 * @param word The word to stem after it has been processed by step2
	 * @param r1 The R1 secion of the word after it has been processed by step2
	 * @return The final stem of the given word.
	 */
	protected abstract String step3(String word, String r1);
	
	/**
	 * Performs step2 of the algorithm by removing the last character of the word if the condition is fullfilled
	 * @param word The word to be stemmed after it has been processed by step1
	 * @param r1 The R1 section of the word after it has been processed by step1
	 * @return The given word without its last character if the condition was fullfilled
	 */
	protected String step2(String word, String r1) {
		Assert.notNull(suffixes2);
		log.debug("Entered step2()");
		for (String suffix : suffixes2) { // Search for suffixes according to
			// rule 2
			if (r1.endsWith(suffix)) { // Remove last character
				String stem = word.substring(0, word.length() - 1);
				log
						.debug(MessageFormat
								.format(
										"Suffix {0} found, removing last character and returning stem {1}",
										suffix, stem));
				return stem;
			}
		}

		return word;
	}
	
	/**
	 * Process document by stemming its title and content
	 */
	public ProcessorStatus process(Document document) {
		StringTokenizer tokenizer = new StringTokenizer(document.getTitle(""));
		StringBuffer processedString = stemWords(tokenizer);
		document.setTitle(processedString.toString());

		tokenizer = new StringTokenizer(document.getContent(""));
		processedString = stemWords(tokenizer);
		document.setContent(processedString.toString());
		
		return ProcessorStatus.OK;

	}

	public void setInitDependencies(Map<String, String> args) {

	}
	
		
	
	/**
	 * Stems the given word by a 3-step algorithm
	 * 
	 * @param word
	 * @return a stemmed version of the given word
	 */
	protected String stem(String word) {
		log.debug(MessageFormat.format("== Stem() entered with word {0} ==",
				word));
		String r1 = findR(word);

		String stem = step1(word, r1);
		log.debug(MessageFormat.format("Stem is {0} after step 1", stem));
		if (!stem.endsWith(r1)) // Update r1 if stem has been modified
			r1 = findR(stem);

		stem = step2(stem, r1);
		log.debug(MessageFormat.format("Stem is {0} after step 2", stem));
		if (!stem.endsWith(r1)) // Update r1 if stem has been modified
			r1 = findR(stem);

		stem = step3(stem, r1);
		log.debug(MessageFormat.format("Stem is {0} after step 3", stem));

		return stem;
	}
	
	/**
	 * Finds "R" section of a word. The "R" section is defined as the region
	 * after the first non-vowel following a vowel, or the null region at the
	 * end of the word if there is no such non-vowel.
	 * 
	 * @param str
	 * @return
	 */
	protected String findR(String str) {
		Assert.notNull(vowels);
		
		for (int i = 0; i < str.length() - 1; i++) {
			if (vowels.contains(str.charAt(i))) { // First vowel found, look
				// for consonant in next
				// character
				if (!vowels.contains(str.charAt(i + 1))) { // If consonant
					// found (not vowel)
					// then we have R1
					String r1 = str.substring(i + 2);
					if (i == 0) { // Adjust R1 so region before it is at least
						// three characters
						if (r1.length() < 2)
							r1 = "";
						else
							r1 = r1.substring(1);
					}
					log.debug(MessageFormat.format("R1 is {0}", r1));
					return r1;
				}
			}
		}
		log.debug("No R1 found");
		return "";

	}
	protected String deleteSuffix(String word, String r1, String[] suffixes) {
		for (String suffix : suffixes) { // Search for suffixes 
			if (r1.endsWith(suffix)) { // Remove suffix
				String stem = word
						.substring(0, word.length() - suffix.length());
				log.debug(MessageFormat.format(
						"Suffix {0} found, returning stem {1}", suffix, stem));
				return stem;
			}
		}
		
		return null;
		
	}
	
	protected String deleteEnding1(String word, String r1) {
		Assert.notNull(endings);
		String suffix = "s";
		if (r1.endsWith(suffix)) { // Check for presceding valid s-ending
			if (endings.contains(word.charAt(word.length() - 2))) {
				String stem = word
						.substring(0, word.length() - suffix.length());
				log.debug(MessageFormat.format(
						"Suffix {0} found, returning stem {1},", suffix, stem));
				return stem;
			}
		}
		
		return null;
		
	}
	
	protected String replaceSuffix(String word, String r1, Map<String,String> suffixesReplace) {
		for (Map.Entry<String, String> suffixReplace : suffixesReplace
				.entrySet()) {
			if (r1.endsWith(suffixReplace.getKey())) {

				word = word.substring(0,
						word.length() - suffixReplace.getKey().length())
						.concat(suffixReplace.getValue());
				log.debug(MessageFormat.format(
						"Suffix {0} found, replacing with {1}, returning {2}",
						suffixReplace.getKey(), suffixReplace.getValue(),
						word));
				
				return word;
			}
		}
		return null;
	}
	
	protected void setVowels(HashSet<Character> vowels) {
		this.vowels = vowels;
	}
	protected void setSuffixes1(String[] suffixes1) {
		this.suffixes1 = suffixes1;
	}
	protected void setEndings(HashSet<Character> endings) {
		this.endings = endings;
	}

	protected void setSuffixes2(String[] suffixes2) {
		this.suffixes2 = suffixes2;
	}

	protected void setSuffixes3(String[] suffixes3) {
		this.suffixes3 = suffixes3;
	}

	private StringBuffer stemWords(StringTokenizer tokenizer) {
		StringBuffer processedString = new StringBuffer();
		String word;
		while (tokenizer.hasMoreTokens()) {
			word = tokenizer.nextToken();
			word = stem(word);
			processedString.append(word);
			processedString.append(" ");
		}

		return processedString;
	}

	
}
