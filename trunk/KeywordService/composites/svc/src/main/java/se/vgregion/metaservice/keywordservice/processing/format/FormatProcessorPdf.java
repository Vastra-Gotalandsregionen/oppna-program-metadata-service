package se.vgregion.metaservice.keywordservice.processing.format;

import org.pdfbox.cos.COSDocument;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.util.PDFTextStripper;



/*
 * Format processor that handles pdf text. Simply returns a clean representation of the 
 * input string.
 * @author svet
 */

public class FormatProcessorPdf implements FormatProcessor {

	public String process (String formattedString) {
		String parsedText = "";
		COSDocument cosDoc = null;
		PDDocument doc = null;
		
		try {
			PDFParser parser = new PDFParser(f);
			parser.parse();
			cosDoc = parser.getDocument();
			PDFTextStripper ps = new PDFTextStripper();
		    doc = new PDDocument(cosDoc);
		    PDDocumentInformation di = doc.getDocumentInformation();
		    String title = di.getTitle();
		    System.out.println("PDF: -->" + title);
			parsedText = ps.getText(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		cosDoc.close();
		doc.close();
		return parsedText; 
		return formattedString;
	}
}
