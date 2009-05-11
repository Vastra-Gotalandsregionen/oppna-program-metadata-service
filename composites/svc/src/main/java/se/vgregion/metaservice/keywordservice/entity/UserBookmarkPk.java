package se.vgregion.metaservice.keywordservice.entity;

import java.io.Serializable;
import javax.persistence.*;

@Embeddable
public class UserBookmarkPk implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String bookmark;
	private String uname;

	public String getBookmark() {
		return bookmark;
	}

	public void setBookmark(String bookmark) {
		this.bookmark = bookmark;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bookmark == null) ? 0 : bookmark.hashCode());
		result = prime * result + ((uname == null) ? 0 : uname.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final UserBookmarkPk other = (UserBookmarkPk) obj;
		if (bookmark == null) {
			if (other.bookmark != null)
				return false;
		} else if (!bookmark.equals(other.bookmark))
			return false;
		if (uname == null) {
			if (other.uname != null)
				return false;
		} else if (!uname.equals(other.uname))
			return false;
		return true;
	}

}
