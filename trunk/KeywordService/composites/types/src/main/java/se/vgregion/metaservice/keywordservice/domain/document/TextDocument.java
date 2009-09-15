package se.vgregion.metaservice.keywordservice.domain.document;

/**
 * TextDocument provides a document implementation
 * respresenting a text document. The text can be
 * either raw text or text with markup (e.g., html). 
 */
public class TextDocument implements Document {

    DocumentImpl document = new DocumentImpl();

    public TextDocument(){
    }

    public void setTitle(String title) {
        document.setTitle(title);
    }

    public void setTextContent(String textContent) {
        document.setTextContent(textContent);
    }

    public String getTitle() {
        return document.getTitle();
    }

    public String getTextContent() {
        return document.getTextContent();
    }

    public boolean hasTitle() {
        return document.getTitle() != null;
    }

    public boolean hasTextContent() {
        return document.getTextContent() != null;
    }
}
