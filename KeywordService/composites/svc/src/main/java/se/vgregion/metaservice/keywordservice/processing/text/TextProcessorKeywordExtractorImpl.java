package se.vgregion.metaservice.keywordservice.processing.text;

import java.util.Map;
import org.apache.log4j.Logger;
import se.vgregion.metaservice.keywordservice.domain.document.AnalysisDocument;
import se.findwise.thesis.keywordextraction.KeyEx;
import se.findwise.thesis.keywordextraction.candidateselection.SelectionMethods;
import se.findwise.thesis.keywordextraction.common.Document;
import se.findwise.thesis.keywordextraction.common.Languages;
import se.findwise.thesis.keywordextraction.common.KeyexException;
import se.findwise.thesis.keywordextraction.keywordselection.Algorithms;

/**
 * 
 * @author martin.johansson
 */
public class TextProcessorKeywordExtractorImpl extends TextProcessor
{
	private Logger log = org.apache.log4j.LogManager.getLogger(TextProcessorKeywordExtractorImpl.class);
	private KeyEx kx = null;
	private boolean initialized = false;

	private String nrKeywordsProperty = "nrKeywords";
	private String outputProperty = "keywords";
	private Languages language = Languages.SWEDISH;
	private Algorithms algorithm = Algorithms.DEFAULT;
	private SelectionMethods candidates = SelectionMethods.DEFAULT;

	public TextProcessorKeywordExtractorImpl()
	{
		kx = new KeyEx();

		try
		{
			kx.init();
			initialized = true;
		}
		catch(KeyexException e)
		{
			log.error("Failed to initialize keyword extraction module: " + e.getMessage());
		}
	}

	@Override
	public ProcessorStatus process(AnalysisDocument document)
	{
		if(!initialized)
		{
			log.error("Attempting to extract keywords when initializing the keyword extraction module failed.");
			return ProcessorStatus.FAILED;
		}

		// Get the number of keywords to extract
		Object o = document.getPropertyObject(nrKeywordsProperty);
		int nrKeywords = (o != null && o instanceof Integer ? (Integer)o : 10);

		String title = document.getTitle(), content = document.getTextContent();

		// 30 chars is the approximate length of the metatags used by the
		// keyword extraction module to interpret text and title content
		StringBuilder sb = new StringBuilder(title.length() + content.length() + 30);
		sb.append("<title>"); sb.append(title);
		sb.append("</title><text>"); sb.append(content); sb.append("</text>");

		try
		{
			Document doc = new Document(sb.toString(), language);
			String[] keywords = kx.classify(nrKeywords, doc, algorithm, candidates, true);

			document.setProperty(outputProperty, keywords);
		}
		catch(Exception e)
		{
			log.error(e.getMessage());
			return ProcessorStatus.FAILED;
		}

		return ProcessorStatus.OK;
	}

	@Override
	public void setInitDependencies(Map<String, String> args)
	{
		// No dependencies needed
	}

	public void setAlgorithm(Algorithms algorithm)
	{
		this.algorithm = algorithm;
	}

	public void setCandidates(SelectionMethods candidates)
	{
		this.candidates = candidates;
	}

	public void setLanguage(Languages language)
	{
		this.language = language;
	}

	public void setOutputProperty(String outputProperty)
	{
		this.outputProperty = outputProperty;
	}

	public void setNrKeywordsProperty(String nrKeywordsProperty)
	{
		this.nrKeywordsProperty = nrKeywordsProperty;
	}
}
