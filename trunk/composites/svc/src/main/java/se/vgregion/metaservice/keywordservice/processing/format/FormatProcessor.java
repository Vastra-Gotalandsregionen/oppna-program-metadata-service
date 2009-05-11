package se.vgregion.metaservice.keywordservice.processing.format;

/**
 * Interface for handle text in different formats. For each format, a format-specific class should be
 * implemented. The process method should be use to strip out all formatting from the content and return
 * a clean text representation of the content.
 * @author tobias
 *
 */
public interface FormatProcessor {

	/**
	 * Processes the given string by stripping out all formatting information and returns a clean textual representation of the string.
	 * @param formattedString the string to process
	 * @return a clean textual representation of the string
	 */
	public String process(String formattedString);
}
