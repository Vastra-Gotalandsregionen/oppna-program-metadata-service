
package se.vgregion.metaservice.keywordservice.domain.document;

import java.io.InputStream;
import java.util.Map;

/**
 * DocumentImpl provides the full representation of a document.
 * Classes FileDocument and TextDocument provides a stripped
 * easy to use interface of a document. 
 */
public class DocumentImpl implements Document {
    InputStream inputStream = null;
    String filename = null;
    String encoding = null;
    String textContent = null;
    String title = null;
    Map<String,Object> properties = null;

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPropertyString(String key) {
		Object value = properties.get(key);
		return value == null ? null : value.toString();
	}

	public Object getPropertyObject(String key) {
		return properties.get(key);
	}

	public void setProperty(String key, Object value) {
		properties.put(key,value);
	}

    
}
