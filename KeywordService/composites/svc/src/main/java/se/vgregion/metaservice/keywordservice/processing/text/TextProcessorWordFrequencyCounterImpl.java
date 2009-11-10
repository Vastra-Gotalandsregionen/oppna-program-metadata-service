package se.vgregion.metaservice.keywordservice.processing.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.*;

import org.apache.log4j.Logger;

import se.vgregion.metaservice.keywordservice.domain.document.AnalysisDocument;

public class TextProcessorWordFrequencyCounterImpl extends TextProcessor {

	/*
	 * Obsolete: before the tdidf was introduced; uncomment setinitdependencies
	 * below public static final String TITLE_WEIGHT_KEY = "titleweight";
	 * 
	 * private int titleWeight = 5;
	 */

	public static final String DOCFREQ_LIST_KEY = "docfreqlist";
	private HashMap<String, Integer> docfreqs;
	private int titleWeight = 2;

	private Logger log = Logger.getLogger(this.getClass());

	public ProcessorStatus process(AnalysisDocument document) {
		HashMap<String, Integer> wordFreq = new HashMap<String, Integer>();
		HashMap<String, Double> tfidfTable = new HashMap<String, Double>();

		StringTokenizer tokenizer = new StringTokenizer(document.getTextContent());
		countFrequencies(tokenizer, wordFreq, 1);
		tokenizer = new StringTokenizer(document.getTitle());
		countFrequencies(tokenizer, wordFreq, titleWeight);

		int n = docfreqs.get("NUMBER_OF_FILES") + 1;
		docfreqs.put("NUMBER_OF_FILES", n);
		
		calculateTfidf(wordFreq, tfidfTable, docfreqs, n);

		document.setProperty("wordfrequency", tfidfTable);

		return ProcessorStatus.OK;
	}

	private void countFrequencies(StringTokenizer tokenizer,
			HashMap<String, Integer> freqTable, int weight) {
		String word;
		while (tokenizer.hasMoreTokens()) {
			word = tokenizer.nextToken();
			Integer count = freqTable.get(word);
			if (count == null)
				freqTable.put(word, new Integer(weight));
			else
				freqTable.put(word, count + weight);

			log.debug(MessageFormat.format("Frequency for word {0} : {1}",
					word, freqTable.get(word)));
		}
	}

	private void calculateTfidf(HashMap<String, Integer> wfHash,
			HashMap<String, Double> tfHash, HashMap<String, Integer> dfHash,
			int n) {

		for (String term : wfHash.keySet()) {

			double w = 1 + Math.log10(wfHash.get(term).doubleValue());

			int df; 

			if (dfHash.containsKey(term)) {
				df = dfHash.get(term) + 1;
				dfHash.put(term, df);
			} else {
				df = 1;
				dfHash.put(term, df);
			}

			double idf = Math.log10((double) n / df);
			w = w * idf;

			tfHash.put(term, w);

		}
	}

	public void setInitDependencies(Map<String, String> args) {

		docfreqs = new HashMap<String, Integer>();
		String filename = args.get(DOCFREQ_LIST_KEY);
		setDocFreqs(filename);
	}

	protected void setDocFreqs(String filename) {
		if (filename == null) {
			log.error(MessageFormat.format(
					"No such document frequency file \"{0}\" found", filename));
		} else {
			try {
				InputStream docFreqStream = this.getClass()
						.getResourceAsStream(filename);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(docFreqStream, "UTF-8"));
				String line = reader.readLine();
				while (line != null) {
					String[] arr = line.split(" ");
					log.debug(MessageFormat.format(
							"Word {0} added, document count {1}", arr[0],
							arr[1]));
					docfreqs.put(arr[0], Integer.parseInt(arr[1]));
					line = reader.readLine();

				}
			} catch (NullPointerException e) {
				log.error(MessageFormat.format(
						"Document frequency file {0} could not be found...",
						filename));

			} catch (IOException e) {
				docfreqs.clear();
				log
						.error(MessageFormat
								.format(
										"Error reading document frequencies file {0}. List will be cleared. No filtering will be performed",
										filename));
			}
		}
	}

	/*
	 * public void setInitDependencies(Map<String, String> args) { String weight
	 * = args.get(TITLE_WEIGHT_KEY); if (weight != null) { try { titleWeight =
	 * Integer.parseInt(weight); } catch (NumberFormatException nfe) { log
	 * .warn( MessageFormat .format(
	 * "Supplied weight {0} is not an integer, reverting to default value",
	 * weight), nfe); } } }
	 */

}
