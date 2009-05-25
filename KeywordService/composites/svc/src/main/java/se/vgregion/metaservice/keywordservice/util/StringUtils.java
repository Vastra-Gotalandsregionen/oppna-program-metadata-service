package se.vgregion.metaservice.keywordservice.util;

public class StringUtils {

	public static String getByteString(String str) {
		byte[] bytes = str.getBytes();
		StringBuffer buf = new StringBuffer();
		for(byte b : bytes) {
			buf.append(b);
			buf.append(" ");
		}

		return buf.toString();
	}
	
}
