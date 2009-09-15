package se.vgregion.metaservice.keywordservice.processing.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import se.vgregion.metaservice.keywordservice.domain.document.AnalysisDocument;
import se.vgregion.metaservice.keywordservice.util.StringUtils;

/**
 * Removes stop words from title and content in a Document. Stop words are taken from a file, which
 * location is specified in the setInitDependencies method.
 * 
 * @author tobias
 *
 */
public class TextProcessorStopWordImpl extends TextProcessor {

	private Set<String> stopwords;
	public static final String STOPWORD_LIST_KEY = "stopwordlist";
	private static Logger log = Logger.getLogger(TextProcessorStopWordImpl.class);
	
	public void setInitDependencies(Map<String, String> args) {
		stopwords = new HashSet<String>();
		String filename = args.get(STOPWORD_LIST_KEY);
		setStopWordList(filename);
	}
	/**
	 * Processes the given string by checking each word against a stopword list. Builds a new string where all stopwords are removed.
	 * @param String content The string to check for stopwords in
	 * @return String a String where all stopwords has been removed from the input string.
	 */
	public ProcessorStatus process(AnalysisDocument document) {
		StringTokenizer tokenizer = new StringTokenizer(document.getTextContent());
		StringBuffer processedString = removeStopWords(tokenizer);
		document.setTextContent(processedString.toString());
		
		tokenizer = new StringTokenizer(document.getTitle());
		processedString = removeStopWords(tokenizer);
		document.setTitle(processedString.toString());
		
		return ProcessorStatus.OK;
	}
	
	private StringBuffer removeStopWords(StringTokenizer tokenizer) {
		StringBuffer processedString = new StringBuffer();
		String word;
		while(tokenizer.hasMoreTokens()) {
			word = tokenizer.nextToken();
			if(!stopwords.contains(word)) {
				processedString.append(word);
				processedString.append(" ");
				log.debug(MessageFormat.format("Word {0} ok, bytecode {1}",word, StringUtils.getByteString(word)));
			}
			else {
				log.debug(MessageFormat.format("Word {0} dropped", word));
			}
		}
		
		return processedString;
	}
	
	protected void setStopWordList(String filename) {
		if(filename == null) {
			log.warn("No stopword file given. No filtering will be performed");
		}
		else {
			try {
				InputStream stopWordStream = this.getClass().getResourceAsStream(filename);
				BufferedReader reader = new BufferedReader(new InputStreamReader(stopWordStream,"UTF-8"));
				String line = reader.readLine();
				while(line != null) {
					
					log.debug(MessageFormat.format("Word {0} added, bytecode {1}",line,StringUtils.getByteString(line)));
					stopwords.add(line);
					line = reader.readLine();
				
				}
			} catch (NullPointerException e) {
				log.error(MessageFormat.format("Stopword file {0} could not be found. No filtering will be performed",filename));
				
			} catch (IOException e) {
				stopwords.clear();
				log.error(MessageFormat.format("Error reading stopword file {0}. List will be cleared. No filtering will be performed", filename));
			}
		}
	}

}
