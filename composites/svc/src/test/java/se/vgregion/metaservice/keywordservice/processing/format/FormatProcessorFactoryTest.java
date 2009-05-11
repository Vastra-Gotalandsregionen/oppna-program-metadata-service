package se.vgregion.metaservice.keywordservice.processing.format;

import se.vgregion.metaservice.keywordservice.BaseSpringDependencyInjectionTest;
import se.vgregion.metaservice.keywordservice.exception.UnsupportedFormatException;

public class FormatProcessorFactoryTest extends BaseSpringDependencyInjectionTest {

	
	public void testCorrectFormats() {
		try {
			FormatProcessor processor = FormatProcessorFactory.getFormatProcessor("text");
			assertNotNull(processor);
			processor = FormatProcessorFactory.getFormatProcessor("html");
			assertNotNull(processor);
		} catch (UnsupportedFormatException e) {
			assert(false);
		}
	}
	
	public void testWrongFormat() {
		try {
			FormatProcessor processor = FormatProcessorFactory.getFormatProcessor("wrongProcessor");
			assert(false);
		} catch (UnsupportedFormatException e) {
			assert(true);
		}
		
	}
}
