package se.vgregion.metaservice.keywordservice.processing.text;

import java.text.MessageFormat;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import se.vgregion.metaservice.keywordservice.domain.document.AnalysisDocument;

/**
 * Removes short words from a text. The minimum length for a word is specified
 * as a parameter to the setInitDependencies method
 * 
 * @author tobias
 * 
 */
public class TextProcessorRemoveShortWordsImpl extends TextProcessor {

	private int minWordLength = 3;
	public static final String MIN_WORD_LENGTH = "minwordlength";
	Logger log = Logger.getLogger(this.getClass());

	public void setInitDependencies(Map<String, String> args) {
		String minLength = args.get(MIN_WORD_LENGTH);
		if (minLength != null) {
			try {
				minWordLength = Integer.parseInt(minLength);
			} catch (NumberFormatException nfe) {
				log
						.warn(
								MessageFormat
										.format(
												"Supplied minLength {0} is not an integer, reverting to default value",
												minLength), nfe);
			}
		}
	}

	/**
	 * Processes the given document by removing short words in the title and content
	 * 
	 * @param Document the document to process
	 * 
	 */
	public ProcessorStatus process(AnalysisDocument document) {
		StringTokenizer tokenizer = new StringTokenizer(document.getTextContent());
		StringBuffer processedString = removeShortWords(tokenizer);
		document.setTextContent(processedString.toString());

		tokenizer = new StringTokenizer(document.getTitle());
		processedString = removeShortWords(tokenizer);
		document.setTitle(processedString.toString());

		return ProcessorStatus.OK;
	}

	private StringBuffer removeShortWords(StringTokenizer tokenizer) {
		StringBuffer processedString = new StringBuffer();
		String word;
		while (tokenizer.hasMoreTokens()) {
			word = tokenizer.nextToken();
			if (word.length() >= minWordLength) {
				processedString.append(word);
				processedString.append(" ");
			}
		}

		return processedString;
	}

}
