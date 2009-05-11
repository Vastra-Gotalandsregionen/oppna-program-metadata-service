package se.vgregion.metaservice.keywordservice.processing.format;



/*
 * Format processor that handles pdf text. Simply returns a clean representation of the 
 * input string.
 * @author svet
 */

public class FormatProcessorPdf implements FormatProcessor {

	public String process (String formattedString) {
		
		return formattedString;
	}
}
