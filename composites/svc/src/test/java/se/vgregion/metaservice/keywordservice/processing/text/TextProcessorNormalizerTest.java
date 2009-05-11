package se.vgregion.metaservice.keywordservice.processing.text;

import se.vgregion.metaservice.keywordservice.BaseSpringDependencyInjectionTest;
import se.vgregion.metaservice.keywordservice.domain.Document;

public class TextProcessorNormalizerTest extends BaseSpringDependencyInjectionTest {

	private TextProcessor normalizerProcessor;
	@Override
	protected void onSetUp() {
		normalizerProcessor = (TextProcessor) applicationContext.getBean("normalizerProcessor");
	}
	
	public void testNormalizer() {
		Document doc = new Document();
		String text = "The 1 quick brown fox. Jumped?  + over the fence";
		String title = "Fox ... pro";
		doc.setTitle(title);
		doc.setContent(text);
		
		normalizerProcessor.process(doc);
		assertEquals("fox pro", doc.getTitle());
		assertTrue(!doc.getContent().contains("."));
		assertTrue(!doc.getContent().contains("?"));
		assertTrue(!doc.getContent().contains("+"));
		assertEquals(9,doc.getContent().split(" ").length);
	}
	
	public void testNormalizeBadCharacters() {
		Document doc = new Document();
		String text = "...?((()))//{}}@£$∞§";
		String title = "*^^^¨¨````=={[]";
		doc.setTitle(title);
		doc.setContent(text);
		normalizerProcessor.process(doc);
		
		assertEquals(0,doc.getTitle().trim().length());
		assertEquals(0,doc.getContent().trim().length());
	}
}
