package se.vgregion.metaservice.keywordservice.processing.text;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.vgregion.metaservice.keywordservice.domain.Document;

/**
 * Normalizes the text in the title and content of a Document. Normalizing is performed by 
 * making all text lowercase. After that, unwanted characters are replaced by a space in the text.
 * Unwanted characters are all but a-z,åäö,0-9,and - (dash-sign).
 * @author tobias
 *
 */
public class TextProcessorNormalizerImpl extends TextProcessor {
	
	public ProcessorStatus process(Document document) {
		String content = document.getContent("");
		if(content != null)
			content = content.toLowerCase();
		String title = document.getTitle("");
		if(title != null)
			title = title.toLowerCase();
		
		String patternStr = "[^a-zåäö0-9\\-]+";
		String replaceStr = " ";
		
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(content);
		String cleanContent = matcher.replaceAll(replaceStr);
		
		matcher = pattern.matcher(title);
		String cleanTitle = matcher.replaceAll(replaceStr);
		
		document.setContent(cleanContent);
		document.setTitle(cleanTitle);
		
		return ProcessorStatus.OK;

	}

	public void setInitDependencies(Map<String, String> args) {

	}

}
