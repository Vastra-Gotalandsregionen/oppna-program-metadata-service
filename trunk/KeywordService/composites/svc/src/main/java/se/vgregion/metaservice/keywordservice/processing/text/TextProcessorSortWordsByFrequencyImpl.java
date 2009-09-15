package se.vgregion.metaservice.keywordservice.processing.text;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import se.vgregion.metaservice.keywordservice.domain.document.AnalysisDocument;

public class TextProcessorSortWordsByFrequencyImpl extends TextProcessor {

	private static Logger log = Logger.getLogger(TextProcessorSortWordsByFrequencyImpl.class);
	
	@Override
	public ProcessorStatus process(AnalysisDocument document) {
		Object wordFreqProp = document.getPropertyObject("wordfrequency");
		if(wordFreqProp == null) {
			log.info("No word frequency found. Returning...");
			return ProcessorStatus.OK;
		}
		
		Map wordFreq = (Map)wordFreqProp;
		for(Object entryObj : wordFreq.entrySet()) {
			Map.Entry entry = (Map.Entry) entryObj;
			log.info(MessageFormat.format("{0} : {1}",entry.getKey(),entry.getValue()));
		}
		ArrayList<Map.Entry<String, Double>> sortedWords = sortMap(wordFreq);
		StringBuffer buf = new StringBuffer();
		for(Map.Entry<String, Double> wordInfo : sortedWords) {
			log.debug(MessageFormat.format("Word {0} has frequency {1}",wordInfo.getKey(),wordInfo.getValue()));
			buf.append(wordInfo.getKey());
			buf.append(" ");
		}
		
		document.setTextContent(buf.toString());
		
		return ProcessorStatus.OK;
		
		
	}

	@Override
	public void setInitDependencies(Map<String, String> args) {
		// TODO Auto-generated method stub

	}

	/**
	 * This method will use Arrays.sort for sorting Map
	 * 
	 * @param map
	 * @return outputList of Map.Entries
	 */
	private ArrayList sortMap(Map map) {
		ArrayList outputList = null;
		int count = 0;
		Set set = null;
		Map.Entry[] entries = null;
		// Logic:
		// get a set from Map
		// Build a Map.Entry[] from set
		// Sort the list using Arrays.sort
		// Add the sorted Map.Entries into arrayList and return

		set = (Set) map.entrySet();
		Iterator iterator = set.iterator();
		entries = new Map.Entry[set.size()];
		while (iterator.hasNext()) {
			entries[count++] = (Map.Entry) iterator.next();
		}
		
		// Sort the entries with your own comparator for the values:
		Comparator reverseComparator = Collections.reverseOrder(new Comparator() {
			public int compareTo(Object lhs, Object rhs) {
				Map.Entry le = (Map.Entry) lhs;
				Map.Entry re = (Map.Entry) rhs;
				return ((Comparable) le.getValue()).compareTo((Comparable) re
						.getValue());
			}

			public int compare(Object lhs, Object rhs) {
				Map.Entry le = (Map.Entry) lhs;
				Map.Entry re = (Map.Entry) rhs;
				return ((Comparable) le.getValue()).compareTo((Comparable) re
						.getValue());
			}
		});
		
		Arrays.sort(entries, reverseComparator);


		outputList = new ArrayList();
		for (int i = 0; i < entries.length; i++) {
			outputList.add(entries[i]);
		}
		
		return outputList;
	}// End of sortMap

}
