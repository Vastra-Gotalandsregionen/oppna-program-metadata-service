package se.vgregion.metaservice.keywordservice.domain;

import java.util.HashMap;
import java.util.Map;

public class Document {

	private String content;
	private String title;
	
	private Map<String,Object> properties;
	
	public Document() {
		properties = new HashMap<String, Object>();
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
	
	public String getContent() {
		return content;
	}
	
	public String getContent(String defaultValue) {
		return content == null ? defaultValue : content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public String getTitle(String defaultValue) {
		return title == null ? defaultValue : title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
}
