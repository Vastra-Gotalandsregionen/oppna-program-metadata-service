package se.vgregion.metaservice.keywordservice.processing.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.openoffice.odf.doc.OdfDocument;
import org.openoffice.odf.doc.OdfFileDom;
import org.openoffice.odf.dom.OdfNamespace;
import org.openoffice.odf.dom.element.OdfElement;



/*
 * Format processor that handles odf text. Simply returns a clean representation of the 
 * input string.
 * @author svet
 */

public class FormatProcessorOdf implements FormatProcessor {

	public String process (File f) {
		
		String formattedString = "";

		try {
			OdfDocument odfDoc = OdfDocument.loadDocument(f);
			OdfFileDom odfContent = odfDoc.getContentDom();
			XPath xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(new OdfNamespace());

			OdfElement body = (OdfElement) xpath.evaluate("//office:body",
					odfContent, XPathConstants.NODE);

			Pattern pattern = Pattern.compile("<.[^>]*>");
			Matcher matcher = pattern.matcher(body.toString());

			text = matcher.replaceAll("\n");
			String arr [] = text.split("\n");
			String title = "";
			int cnt = 0;
			for (String a : arr) {
				if (a.length() > 1) {
					cnt++;
					if (cnt == 1) {
						title = a;
					}
					else {
						text+= a + " ";
					}
				}				
			}
			System.out.println("ODF: --> " + title);
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return formattedString;
	}
}
