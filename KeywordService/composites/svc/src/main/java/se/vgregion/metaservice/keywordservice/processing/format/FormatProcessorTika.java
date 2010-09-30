package se.vgregion.metaservice.keywordservice.processing.format;

import java.io.BufferedInputStream;
import java.io.StringWriter;
import se.vgregion.metaservice.keywordservice.domain.document.AnalysisDocument;
import se.vgregion.metaservice.keywordservice.domain.document.Document;
import se.vgregion.metaservice.keywordservice.domain.document.FileDocument;
import se.vgregion.metaservice.keywordservice.exception.FormattingException;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

/**
 * FormatProcessor that is to handle most of the fileformats 
 * @author sture.svensson
 */
public class FormatProcessorTika implements FormatProcessor {

    /**
     * Autodetects and parses the document.
     *
     * @param document the document
     * @return the newly created analysisDocument
     * @throws FormattingException
     */
    public AnalysisDocument process(Document document) throws FormattingException {
        AnalysisDocument analysisDocument = new AnalysisDocument();

        FileDocument fileDocument = (FileDocument) document;
        BufferedInputStream inStream = new BufferedInputStream(fileDocument.getInputStream());

        Metadata metadata = new Metadata();
        Parser parser = new AutoDetectParser();

		ParseContext parseCtx = new ParseContext();
		parseCtx.set(Parser.class, parser);

		StringWriter sw = new StringWriter();

        try {

			parser.parse(inStream, new BodyContentHandler(sw), metadata, parseCtx);
            analysisDocument.setTextContent(sw.toString());
            
            for (String name : metadata.names()) {
                if (name.equals(Metadata.TITLE)) {
                    analysisDocument.setTitle(metadata.get(name));
                } else {
                    analysisDocument.setProperty(name, metadata.get(name));
                }
            }
        } catch (Exception ex) {
            throw new FormattingException("Could not parse document");
        } finally {
            try {
				sw.close();
                inStream.close(); // close the stream
            } catch (Exception e) {
                //ok to ignore
            }
        }
        return analysisDocument;
    }

    /**
     * This should not be used
     * @param formattedString
     * @return the same string
     */
    public String process(String formattedString) {
        return formattedString;
    }
}
