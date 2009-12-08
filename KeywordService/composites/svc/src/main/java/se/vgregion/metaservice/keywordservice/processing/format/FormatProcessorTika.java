package se.vgregion.metaservice.keywordservice.processing.format;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import se.vgregion.metaservice.keywordservice.domain.document.AnalysisDocument;
import se.vgregion.metaservice.keywordservice.domain.document.Document;
import se.vgregion.metaservice.keywordservice.domain.document.FileDocument;
import se.vgregion.metaservice.keywordservice.exception.FormattingException;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParsingReader;

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

        ParsingReader reader = null;

        try {
            reader = new ParsingReader(parser, inStream, metadata);

            StringBuffer textData = new StringBuffer(1000);
            BufferedReader bReader = new BufferedReader(reader);
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = bReader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                textData.append(readData);
                buf = new char[1024];
            }

            analysisDocument.setTextContent(textData.toString());
            
            for (String name : metadata.names()) {
                if (name.equals(Metadata.TITLE)) {
                    analysisDocument.setTitle(metadata.get(name));
                } else {
                    analysisDocument.setProperty(name, metadata.get(name));
                }
            }
        } catch (IOException ex) {
            throw new FormattingException("Could not parse document");
        } finally {
            try {
                reader.close();
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
