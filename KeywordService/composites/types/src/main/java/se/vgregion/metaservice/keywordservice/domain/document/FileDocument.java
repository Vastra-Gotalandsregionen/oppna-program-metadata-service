
package se.vgregion.metaservice.keywordservice.domain.document;

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

 
    public InputStream getInputStream() {
        return document.getInputStream();
    }

 
    public void setEncoding(String encoding) {
        document.setEncoding(encoding);
    }

 
    public void setFilename(String filename) {
        document.setFilename(filename);
    }

 
    public void setInputStream(InputStream inputStream) {
        document.setInputStream(inputStream);
    }

}
