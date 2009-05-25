package se.vgregion.metaservice.keywordservice.processing.format;

/**
 * FormatProcessor that handles clean text. Simply returns the given input string.
 * @author tobias
 *
 */
public class FormatProcessorText implements FormatProcessor {

	/**
	 * @see FormatProcessor
	 */
	public String process(String formattedString) {
		
		return formattedString;
	}
	
	
	
}
