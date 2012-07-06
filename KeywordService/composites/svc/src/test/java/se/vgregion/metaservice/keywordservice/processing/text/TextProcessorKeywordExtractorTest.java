package se.vgregion.metaservice.keywordservice.processing.text;

import java.util.List;

import se.vgregion.metaservice.keywordservice.BaseSpringDependencyInjectionTest;

import com.findwise.linguistics.keywordextraction.KeyEx;
import com.findwise.linguistics.keywordextraction.documents.Document;
import com.findwise.linguistics.keywordextraction.enums.ClassificationMethods;
import com.findwise.linguistics.keywordextraction.enums.ExtractionMethods;
import com.findwise.linguistics.keywordextraction.enums.Languages;
import com.findwise.linguistics.keywordextraction.exceptions.KeyexException;

/**
 * Tests basic functionality of the keyword extraction module.
 * Tests:
 *		* External resources are available (some, not all)
 *		* Lemmatization
 *		* PoS tagging
 *		* NP-chunking
 *
 * @author martin.johansson
 */
public class TextProcessorKeywordExtractorTest extends BaseSpringDependencyInjectionTest
{
	public void testModule()
	{
		String text = "Röd bil";
		KeyEx kx = applicationContext.getBean(KeyEx.class);

		try
		{
//			Document doc = new Document(text, Languages.SWEDISH);
//			List<String> kws = kx.classify(doc, ClassificationMethods.NBC, ExtractionMethods.NPCHUNK, 1);
			List<String> kws = kx.classify(text, "", Languages.SWEDISH, 1, ExtractionMethods.NPCHUNK, ClassificationMethods.NBC);

			assert(kws.size() == 1 && kws.get(0).equals(text));
		}
		catch(KeyexException e)
		{
			assert(false);
		}
	}
}
