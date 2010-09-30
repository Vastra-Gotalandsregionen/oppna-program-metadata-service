package se.vgregion.metaservice.keywordservice.impl;

import java.text.MessageFormat;

import org.apache.log4j.Logger;

import se.vgregion.metaservice.keywordservice.AnalysisService;
import se.vgregion.metaservice.keywordservice.domain.document.AnalysisDocument;
import se.vgregion.metaservice.keywordservice.exception.ProcessingException;
import se.vgregion.metaservice.keywordservice.processing.text.TextProcessor;
import se.vgregion.metaservice.keywordservice.processing.text.TextProcessor.ProcessorStatus;

public class AnalysisServiceImpl extends AnalysisService {

	private static Logger log = Logger.getLogger(AnalysisServiceImpl.class);
	private String nrKeywordsProperty = "";
	private String finalResultProperty = "";
	
	/**
	 * The new chain of text processors work in a different way than the old
	 * described at the bottom. It does not alter the text of a document, but
	 * rather adds properties of documents with names that are configurable.
	 * The name of the property with the final result is also configurable
	 * through spring and used by this class to retrieve the result.
	 *
	 * OLD:
	 * Overrides the extractWords method in super abstract class. This implementation sends the Document through each
	 * of the TextProcessors provided by the setTextProcessors method. After the document has been processed, short words are removed if a minWordLenght has been
	 * provided by the setMinWordLength method. After the short words have been removed, an array of keywords with maximum size given
	 * by setResultLimit is returned. Returned keywords is taken from the content and title field in the Document.
	 */
	@Override
	public String[] extractWords(AnalysisDocument document,int inputWords) throws ProcessingException{

		document.setProperty(nrKeywordsProperty, inputWords);

		// TODO: Make this method throw ProcessingException
		for(TextProcessor processor : processors) {
			log.info(MessageFormat.format("Calling processor {0}",processor.getClass().getSimpleName()));
			try {
				ProcessorStatus status = processor.process(document);
				log.debug(MessageFormat.format("### {0} ###",processor.getClass().getSimpleName()));
				log.debug(MessageFormat.format("Title: {0} ",document.getTitle()));
				log.debug(MessageFormat.format("Content: {0} ",document.getTextContent()));

				if(status.equals(ProcessorStatus.FAILED)) {
					log.warn("Processing failed, aborting");
					return new String[0];
				}
			}
			catch(RuntimeException e) {
				log.error("Exception occured in Processor, aborting");
				log.error(e.getMessage());
				//TODO: Catch and throw new exception that propagates up to response
			}
		}

		return (String[])document.getPropertyObject(finalResultProperty);
	}

	public void setNrKeywordsProperty(String nrKeywordsProperty)
	{
		this.nrKeywordsProperty = nrKeywordsProperty;
	}

	public void setFinalResultProperty(String finalResultProperty)
	{
		this.finalResultProperty = finalResultProperty;
	}
}