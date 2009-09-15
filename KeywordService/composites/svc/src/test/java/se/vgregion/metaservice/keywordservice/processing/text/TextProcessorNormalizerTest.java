package se.vgregion.metaservice.keywordservice.processing.text;

import se.vgregion.metaservice.keywordservice.BaseSpringDependencyInjectionTest;
import se.vgregion.metaservice.keywordservice.domain.document.AnalysisDocument;

public class TextProcessorNormalizerTest extends BaseSpringDependencyInjectionTest {

	private TextProcessor normalizerProcessor;
	@Override
	protected void onSetUp() {
		normalizerProcessor = (TextProcessor) applicationContext.getBean("normalizerProcessor");
	}
	
	public void testNormalizer() {
		AnalysisDocument doc = new AnalysisDocument();
		String text = "The 1 quick brown fox. Jumped?  + over the fence";
		String title = "Fox ... pro";
		doc.setTitle(title);
		doc.setTextContent(text);
		
		normalizerProcessor.process(doc);
		assertEquals("fox pro", doc.getTitle());
		assertTrue(!doc.getTextContent().contains("."));
		assertTrue(!doc.getTextContent().contains("?"));
		assertTrue(!doc.getTextContent().contains("+"));
		assertEquals(9,doc.getTextContent().split(" ").length);
	}
	
	public void testNormalizeBadCharacters() {
		AnalysisDocument doc = new AnalysisDocument();
		String text = "...?((()))//{}}@£$∞§";
		String title = "*^^^¨¨````=={[]";
		doc.setTitle(title);
		doc.setTextContent(text);
		normalizerProcessor.process(doc);
		
		assertEquals(0,doc.getTitle().trim().length());
		assertEquals(0,doc.getTextContent().trim().length());
	}
}
