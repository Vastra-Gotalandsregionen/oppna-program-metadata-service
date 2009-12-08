/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.vgregion.metaservice.keywordservice.schema;

import se.vgregion.metaservice.keywordservice.domain.document.Document;
import se.vgregion.metaservice.keywordservice.domain.document.FileDocument;
import se.vgregion.metaservice.keywordservice.domain.document.TextDocument;
import se.vgregion.metaservice.schema.domain.DocumentType;
import se.vgregion.metaservice.schema.domain.FileDocumentType;
import se.vgregion.metaservice.schema.domain.TextDocumentType;



/**
 *
 * @author tobias
 */
public class DocumentSdoHelper {

    public DocumentSdoHelper() {
        
    }

    public static DocumentType toDocumentType(Document document) {
        DocumentType documentType = null;
        if(document instanceof FileDocument) {
            documentType = toFileDocumentType((FileDocument)document);

        }
        else if(document instanceof TextDocument) {
            documentType = toTextDocumentType((TextDocument)document);
        }

        return documentType;
    }

    private static DocumentType toFileDocumentType(FileDocument document) {
        FileDocumentType fileDocumentType = new FileDocumentType();
        fileDocumentType.setFilename(document.getFilename());
        fileDocumentType.setEncoding(document.getEncoding());
        fileDocumentType.setData(document.getData());
        return fileDocumentType;
    }

    private static DocumentType toTextDocumentType(TextDocument document) {
        TextDocumentType textDocumentType = new TextDocumentType();
        textDocumentType.setTitle(document.getTitle());
        textDocumentType.setTextContent(document.getTextContent());
        return textDocumentType;
    }

    public static Document fromDocumentType(DocumentType documentType) {
        Document document = null;
        if(documentType instanceof FileDocumentType) {
            document = fromFileDocumentType((FileDocumentType)documentType);
        }
        else if(documentType instanceof TextDocumentType) {
            document = fromTextDocumentType((TextDocumentType)documentType);
        }
        return document;
    }

    private static Document fromFileDocumentType(FileDocumentType documentType) {
        FileDocument fileDoc = new FileDocument();
        fileDoc.setFilename(documentType.getFilename());
        fileDoc.setEncoding(documentType.getEncoding());
        fileDoc.setData(documentType.getData());
        return fileDoc;
    }

    private static Document fromTextDocumentType(TextDocumentType documentType) {
        TextDocument textDoc = new TextDocument();
        textDoc.setTitle(documentType.getTitle());
        textDoc.setTextContent(documentType.getTextContent());
        return textDoc;
    }

}
