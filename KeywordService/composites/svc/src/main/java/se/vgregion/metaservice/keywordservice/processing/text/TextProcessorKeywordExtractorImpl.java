package se.vgregion.metaservice.keywordservice.processing.text;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import se.vgregion.metaservice.keywordservice.domain.document.AnalysisDocument;

import com.findwise.linguistics.keywordextraction.KeyEx;
import com.findwise.linguistics.keywordextraction.enums.ClassificationMethods;
import com.findwise.linguistics.keywordextraction.enums.ExtractionMethods;
import com.findwise.linguistics.keywordextraction.enums.Languages;

/**
 * 
 * @author martin.johansson
 */
public class TextProcessorKeywordExtractorImpl extends TextProcessor
{
	private Logger log = Logger.getLogger(TextProcessorKeywordExtractorImpl.class);
	private KeyEx kx = null;

	private String nrKeywordsProperty = "nrKeywords";
	private String outputProperty = "keywords";
	private Languages language = Languages.SWEDISH;
	private ClassificationMethods algorithm = ClassificationMethods.NBC;
	private ExtractionMethods candidates = ExtractionMethods.NPCHUNK;

	public TextProcessorKeywordExtractorImpl() { }

	@Override
	public ProcessorStatus process(AnalysisDocument document)
	{
		// Get the number of keywords to extract
		Object o = document.getPropertyObject(nrKeywordsProperty);
		int nrKeywords = (o != null && o instanceof Integer ? (Integer)o : 10);

		String title = document.getTitle();
		String content = document.getTextContent();

		try {
			List<String> keywords = kx.classify(content, title, language, nrKeywords, candidates, algorithm);
			
			document.setProperty(outputProperty, keywords.toArray(new String[0]));

			String kws = StringUtils.arrayToDelimitedString(keywords.toArray(), ", ");
			log.debug("Extracted keywords: " + kws);
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

	public void setKx(KeyEx kx)
	{
		this.kx = kx;
	}

	public void setAlgorithm(ClassificationMethods algorithm)
	{
		this.algorithm = algorithm;
	}

	public void setCandidates(ExtractionMethods candidates)
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
