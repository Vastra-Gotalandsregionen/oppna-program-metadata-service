package se.vgregion.metaservice.keywordservice.processing.text;

import se.findwise.thesis.keywordextraction.KeyEx;
import se.findwise.thesis.keywordextraction.candidateselection.SelectionMethods;
import se.findwise.thesis.keywordextraction.common.Document;
import se.findwise.thesis.keywordextraction.common.KeyexException;
import se.findwise.thesis.keywordextraction.common.Languages;
import se.findwise.thesis.keywordextraction.keywordselection.Algorithms;
import se.vgregion.metaservice.keywordservice.BaseSpringDependencyInjectionTest;

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
		KeyEx kx = new KeyEx();

		try
		{
			kx.init();

			Document doc = new Document(text, Languages.SWEDISH);
			String[] kws = kx.classify(1, doc, Algorithms.NBC, SelectionMethods.NPCHUNK, true);

			assert(kws.length == 1 && kws[0].equals(text));
		}
		catch(KeyexException e)
		{
			assert(false);
		}
	}
}
