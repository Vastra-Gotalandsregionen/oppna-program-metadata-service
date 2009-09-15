package se.vgregion.metaservice.keywordservice.processing.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import se.vgregion.metaservice.keywordservice.domain.document.AnalysisDocument;
import se.vgregion.metaservice.keywordservice.domain.document.Document;
import se.vgregion.metaservice.keywordservice.domain.document.TextDocument;
import se.vgregion.metaservice.keywordservice.exception.FormattingException;

/**
 * Format processor that can handle html formatted text. The html must be well-formatted for the processor to handle it.
 * @author tobias
 *
 */
public class FormatProcessorHtml implements FormatProcessor {

    //TODO: Make this better!
    public AnalysisDocument process(Document document) throws FormattingException {

        TextDocument textDocument = (TextDocument) document;
        AnalysisDocument analysisDocument = new AnalysisDocument();

        if (textDocument.hasTextContent()) {
            if (textDocument.getTextContent().length() > 2) {
                Pattern pattern = Pattern.compile("<.[^>]*>");
                Matcher matcher = pattern.matcher(textDocument.getTextContent());
                analysisDocument.setTextContent(matcher.replaceAll(""));
            }
        } else {
            throw new FormattingException("No textContent in the document");
        }
        if (textDocument.hasTitle()) {
            analysisDocument.setTitle(textDocument.getTitle());
        }

        return analysisDocument;
    }

    /**
     * This should no longer be used
     */
    public String process(String formattedString) {
        if (formattedString != null && formattedString.length() > 2) {
            Pattern pattern = Pattern.compile("<.[^>]*>");
            Matcher matcher = pattern.matcher(formattedString);

            return matcher.replaceAll("");
        } else {
            return formattedString;
        }
    }
}
