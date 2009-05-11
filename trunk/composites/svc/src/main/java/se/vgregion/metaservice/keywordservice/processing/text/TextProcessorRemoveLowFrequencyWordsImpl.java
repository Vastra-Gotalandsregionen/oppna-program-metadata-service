package se.vgregion.metaservice.keywordservice.processing.text;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import se.vgregion.metaservice.keywordservice.domain.Document;

public class TextProcessorRemoveLowFrequencyWordsImpl extends TextProcessor {

	private int wordLimit = 100;

	public static final String LIMIT_KEY = "wordlimit";

	private Logger log = Logger.getLogger(this.getClass());

	public ProcessorStatus process(Document document) {
		Map<String,Integer> wordFreq = (HashMap<String,Integer>) document.getPropertyObject("wordfrequency");
		if(wordFreq == null) {
			log.info("No word frequency information found for document. Returning...");
			return ProcessorStatus.OK;
		}
		List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(
				wordFreq.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> left,
					Map.Entry<String, Integer> right) {
				if (left.getValue() < right.getValue())
					return +1;
				else if (left.getValue() > right.getValue())
					return -1;
				else
					return 0;
			}
		});
		StringBuilder processedString = new StringBuilder();
		int limit = Math.min(wordLimit, list.size());
		for(int i=0; i<limit; i++) {
			Map.Entry<String, Integer> word = list.get(i);
			processedString.append(word.getKey());
			processedString.append(" ");
		}
		
		document.setContent(processedString.toString());
		
		return ProcessorStatus.OK;

	}

	public void setInitDependencies(Map<String, String> args) {
		String limit = args.get(LIMIT_KEY);
		if (limit != null) {
			try {
				wordLimit = Integer.parseInt(limit);
			} catch (NumberFormatException nfe) {
				log
						.warn(
								MessageFormat
										.format(
												"Supplied limit {0} is not an integer, reverting to default value",
												limit), nfe);
			}
		}

	}

}
