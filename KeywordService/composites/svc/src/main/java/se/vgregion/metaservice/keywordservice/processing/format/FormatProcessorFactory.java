package se.vgregion.metaservice.keywordservice.processing.format;

import java.util.Map;

import se.vgregion.metaservice.keywordservice.exception.UnsupportedFormatException;

/**
 * Factory class that should be used to retrieve a FormatProcessor. The factory needs to be provided with a
 * Map that maps a string with a FormatProcessor.
 * @author tobias
 *
 */
public class FormatProcessorFactory {

	private static Map<String,FormatProcessor> formatProcessors;

	/**
	 * Sets all formatProcessors that this factory should be able to provide. Each formatProcessor is mapped
	 * against a string value that is used by clients when they query for a formatProcessor
	 * @param formatProcessors the Map of processors that this factory should be able to provide
	 */
	public static void setFormatProcessors(Map<String,FormatProcessor> formatProcessors) {
		FormatProcessorFactory.formatProcessors = formatProcessors;
	}
	
	/**
	 * Returns a format processor if the provided format key matches a FormatProcessor for this factory
	 * @param format the type of format processor to use. Must match a string key value in this factory's formatProcessors Map.
	 * @return a FormatProcessor that can handle the given format.
	 * @throws UnsupportedFormatException if the given format does not map to a FormatProcessor
	 */
	public static FormatProcessor getFormatProcessor(String format) throws UnsupportedFormatException{
		FormatProcessor processor = formatProcessors.get(format); //TODO: Dynamic assertion
		if(processor == null) {
			
			throw new UnsupportedFormatException("The format is not supported");
		}
		
		return processor;
	}
}
