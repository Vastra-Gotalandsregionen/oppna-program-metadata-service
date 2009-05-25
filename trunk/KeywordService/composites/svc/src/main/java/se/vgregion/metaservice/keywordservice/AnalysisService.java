package se.vgregion.metaservice.keywordservice;

import java.util.List;

import se.vgregion.metaservice.keywordservice.domain.Document;
import se.vgregion.metaservice.keywordservice.processing.text.TextProcessor;

public abstract class AnalysisService {

	protected List<TextProcessor> processors;
	protected int minWordLength = 0;
	
	public void setTextProcessors(List<TextProcessor> processors) {
		this.processors = processors;
	}
	
	public abstract String[] extractWords(Document document,int wordLimit);

	public void setMinWordLength(int minWordLength) {
		this.minWordLength = minWordLength;
	}
	
}
