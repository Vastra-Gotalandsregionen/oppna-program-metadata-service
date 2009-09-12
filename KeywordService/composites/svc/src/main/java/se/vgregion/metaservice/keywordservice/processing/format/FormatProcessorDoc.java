package se.vgregion.metaservice.keywordservice.processing.format;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;





/*
 * Format processor that handles word files text. Simply returns a clean representation of the 
 * input string.
 * @author svet
 */

public class FormatProcessorDoc implements FormatProcessor {

	public String process (String formattedString) {
		return formattedString;
		/*ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		OutputStreamWriter out = new OutputStreamWriter(outputStream);

		POIFSFileSystem fileSystem = null;
		try {
            fileSystem = new POIFSFileSystem(new ByteArrayInputStream(formattedString.getBytes()));
		} catch (IOException ex) {

			System.out.println("Error");
		}
		HWPFDocument document = null;
		try {
			document = new HWPFDocument(fileSystem);
		} catch (IOException ex) {
		}

		// Extract the text

		WordExtractor wordExtractor = null;
		try {
			wordExtractor = new WordExtractor(document);
		} catch (IOException ex) {
			System.out.println("Error");
		}
		String[] paragraphs = wordExtractor.getParagraphText();

		try {
			for (int i = 0; i < paragraphs.length; i++) {
				paragraphs[i] = WordExtractor.stripFields(paragraphs[i]);
				out.write(paragraphs[i]);
			}
			out.close();
		} catch (IOException ex) {
			System.out.println("Error");
		}
		
		SummaryInformation si = wordExtractor.getSummaryInformation();
		String title =  si.getTitle();
		System.out.println("DOC: --> " + title);

		return outputStream.toString();*/
	}
}
