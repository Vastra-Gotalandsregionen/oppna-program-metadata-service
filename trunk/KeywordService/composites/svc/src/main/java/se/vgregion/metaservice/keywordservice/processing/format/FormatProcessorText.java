package se.vgregion.metaservice.keywordservice.processing.format;

import se.vgregion.metaservice.keywordservice.domain.document.AnalysisDocument;
import se.vgregion.metaservice.keywordservice.domain.document.Document;
import se.vgregion.metaservice.keywordservice.domain.document.TextDocument;
import se.vgregion.metaservice.keywordservice.exception.FormattingException;

/**
 * FormatProcessor that handles clean text. Simply returns the given input strings.
 *
 */
public class FormatProcessorText implements FormatProcessor {

    /**
     * This function simply translates a TextDocument to a AnalysisDocument.
     * No modifications of the texts are performed.
     * @param document the Document
     * @return the newly created analysisDocument
     * @throws FormattingException
     */
    public AnalysisDocument process(Document document) throws FormattingException {

        TextDocument textDocument = (TextDocument) document;
        AnalysisDocument analysisDocument = new AnalysisDocument();
        if (textDocument.hasTextContent()) {
            analysisDocument.setTitle(textDocument.getTitle());
        } else {
            throw new FormattingException("No textContent in the document");
        }
        if (textDocument.hasTitle()) {
            analysisDocument.setTextContent(textDocument.getTextContent());
        }
        return analysisDocument;
    }

    /**
     * This is no longer supposed to be used
     */
    public String process(String formattedString) {

        return formattedString;
    }
}
