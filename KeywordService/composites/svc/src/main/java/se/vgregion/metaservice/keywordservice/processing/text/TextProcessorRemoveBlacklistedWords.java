package se.vgregion.metaservice.keywordservice.processing.text;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import se.vgregion.metaservice.keywordservice.dao.BlacklistedWordDao;
import se.vgregion.metaservice.keywordservice.domain.Document;
import se.vgregion.metaservice.keywordservice.entity.BlacklistedWord;

public class TextProcessorRemoveBlacklistedWords extends TextProcessor {

	private BlacklistedWordDao blacklistedWordDao;
	
	private static Logger log = Logger.getLogger(TextProcessorRemoveBlacklistedWords.class);
	
	public ProcessorStatus process(Document document) {
		List<BlacklistedWord> blacklistedWords = blacklistedWordDao.getAllBlacklistedWords();
		String title = removeBlacklistedWords(blacklistedWords, document.getTitle());
		String content = removeBlacklistedWords(blacklistedWords, document.getContent());
		
		document.setTitle(title);
		document.setContent(content);
		
		return ProcessorStatus.OK;

	}

	private String removeBlacklistedWords(List<BlacklistedWord> blacklistedWords, String content) {
		log.debug("RemoveBlacklistedWords called");
		String[] allWords = content.split(" ");
		StringBuilder cleanWords = new StringBuilder();
		for(String word : allWords) {
			if(!blacklistedWords.contains(word)) { 
				cleanWords.append(word);
				cleanWords.append(" ");
				log.debug(MessageFormat.format("Word {0} is ok, adding",word));
			}
			else {
				log.info(MessageFormat.format("Word {0} is blacklisted, removing.", word));
			}
		}
		return cleanWords.toString();
	}
	
	public void setInitDependencies(Map<String, String> args) {

	}

	public void setBlacklistedWordDao(BlacklistedWordDao blacklistedWordDao) {
		this.blacklistedWordDao = blacklistedWordDao;
	}


}
