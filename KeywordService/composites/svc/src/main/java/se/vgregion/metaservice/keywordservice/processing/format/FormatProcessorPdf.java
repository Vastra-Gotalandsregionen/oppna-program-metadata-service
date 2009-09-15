package se.vgregion.metaservice.keywordservice.processing.format;

import java.io.ByteArrayInputStream;
import se.vgregion.metaservice.keywordservice.domain.document.AnalysisDocument;
import se.vgregion.metaservice.keywordservice.domain.document.Document;
import se.vgregion.metaservice.keywordservice.exception.FormattingException;




/*
 * Format processor that handles pdf text. Simply returns a clean representation of the 
 * input string.
 * @author svet
 */

public class FormatProcessorPdf implements FormatProcessor {

        public AnalysisDocument process(Document document) throws FormattingException {

        //TODO: Implement this function!
        return new AnalysisDocument();

    }

	public String process (String formattedString) {
		return formattedString;
        /*String parsedText = "";
		COSDocument cosDoc = null;
		PDDocument doc = null;
		
		try {
            PDFParser parser = new PDFParser(new ByteArrayInputStream(formattedString.getBytes()));
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
		 
		return formattedString; */
	}
}
