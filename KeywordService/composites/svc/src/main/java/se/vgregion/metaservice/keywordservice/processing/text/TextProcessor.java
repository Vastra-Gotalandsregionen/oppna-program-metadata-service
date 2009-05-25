package se.vgregion.metaservice.keywordservice.processing.text;

import java.util.Map;

import se.vgregion.metaservice.keywordservice.domain.Document;

/**
 * The TextProcessor is used to process text. 
 * @author tobias
 *
 */
public abstract class TextProcessor {

	public abstract ProcessorStatus process(Document document);
	public abstract void setInitDependencies(Map<String,String> args);

	
	public static enum ProcessorStatus {OK,FAILED};
}
