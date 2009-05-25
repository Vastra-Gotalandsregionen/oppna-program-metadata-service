package se.vgregion.metaservice.keywordservice.processing.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Format processor that can handle html formatted text. The html must be well-formatted for the processor to handle it.
 * @author tobias
 *
 */
public class FormatProcessorHtml implements FormatProcessor {

	/**
	 * @see FormatProcessor
	 */
	public String process(String formattedString) {
		if (formattedString != null && formattedString.length() > 2) {
			Pattern pattern = Pattern.compile("<.[^>]*>");
			Matcher matcher = pattern.matcher(formattedString);

			return matcher.replaceAll("");
		}
		else
			return formattedString;
	}
}
