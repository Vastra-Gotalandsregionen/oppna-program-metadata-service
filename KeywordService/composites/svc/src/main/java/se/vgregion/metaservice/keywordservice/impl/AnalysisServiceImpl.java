package se.vgregion.metaservice.keywordservice.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import se.vgregion.metaservice.keywordservice.AnalysisService;
import se.vgregion.metaservice.keywordservice.domain.document.AnalysisDocument;
import se.vgregion.metaservice.keywordservice.exception.ProcessingException;
import se.vgregion.metaservice.keywordservice.processing.text.TextProcessor;
import se.vgregion.metaservice.keywordservice.processing.text.TextProcessor.ProcessorStatus;

public class AnalysisServiceImpl extends AnalysisService {

	private static Logger log = Logger.getLogger(AnalysisServiceImpl.class);
	
	/**
	 * Overrides the extractWords method in super abstract class. This implementation sends the Document through each
	 * of the TextProcessors provided by the setTextProcessors method. After the document has been processed, short words are removed if a minWordLenght has been
	 * provided by the setMinWordLength method. After the short words have been removed, an array of keywords with maximum size given
	 * by setResultLimit is returned. Returned keywords is taken from the content and title field in the Document.
	 */
	@Override
	public String[] extractWords(AnalysisDocument document,int wordLimit) throws ProcessingException{

            //TODO:Make this method throw ProcessingException


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
		String newContent = document.getTextContent()+document.getTitle();
		
		if(newContent.trim().equals(""))
			return new String[0];
		
		String[] words = newContent.split(" ");
		if(minWordLength > 0) {
			List<String> tempWords = new ArrayList<String>();
			for(String word : words) {
				if(word.length() >= minWordLength)
					tempWords.add(word);
			}
			words = tempWords.toArray(new String[0]);
		}
		String[] returnWords = new String[Math.min(words.length,wordLimit)];
		System.arraycopy(words, 0, returnWords, 0, returnWords.length);
		
		return returnWords;
	}
	
}