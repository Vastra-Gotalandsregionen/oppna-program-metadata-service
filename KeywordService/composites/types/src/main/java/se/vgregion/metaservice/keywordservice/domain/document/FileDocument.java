package se.vgregion.metaservice.keywordservice.domain.document;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * FileDocument provides a document implementation
 * representing a file document. 
 */
public class FileDocument implements Document {

    DocumentImpl document = new DocumentImpl();

    public String getEncoding() {
        return document.getEncoding();
    }

    public String getFilename() {
        return document.getFilename();
    }

    // this should never be used, if this changes, implement a rel one
    public byte[] getData() {
        return new byte[12];
    }

    public InputStream getInputStream() {
        return document.getInputStream();
    }

    public void setEncoding(String encoding) {
        document.setEncoding(encoding);
    }

    public void setFilename(String filename) {
        document.setFilename(filename);
    }

    public void setData(byte[] b) {
        document.setInputStream(new ByteArrayInputStream(b));
    }
}
