package se.vgregion.metaservice.keywordservice.processing.format;

import se.vgregion.metaservice.keywordservice.BaseSpringDependencyInjectionTest;

public class FormatProcessorHtmlTest extends BaseSpringDependencyInjectionTest {

	private FormatProcessor htmlProcessor;
	
	@Override
	protected void onSetUp() {
		htmlProcessor = (FormatProcessor)applicationContext.getBean("formatProcessorHtml");
	}
	
	
	public void testRemoveHtml() {
		String htmlString = "<b>hej</b> alla <br /> testare";
		String expectedString = "hej alla  testare";
		
		
		String cleanString = htmlProcessor.process(htmlString);
		
		assertEquals(expectedString, cleanString);
	}
	
	public void testBrokenHtml() {
		String brokenHtmlString = "testar < med vilka som helst <"; //Should not be altered
		String processedString = htmlProcessor.process(brokenHtmlString);
		
		assertEquals(brokenHtmlString,processedString);
		
	}
}
