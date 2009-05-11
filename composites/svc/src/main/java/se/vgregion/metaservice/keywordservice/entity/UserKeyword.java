package se.vgregion.metaservice.keywordservice.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"uname","keywordid"}))

public class UserKeyword {
	private String keywordid;
	private String keywordname;
	private boolean tagged;
	private boolean bookmarked;
	private String uname;
	private Integer id;
	
    public UserKeyword() {
		super();
		this.keywordid = "";
		this.uname = "";
		this.keywordname="";
		this.id=0;
		this.tagged=false;
		this.bookmarked=false;
	}

    public UserKeyword(String uname, String keywordid) {
		super();
		this.keywordid = keywordid;
		this.keywordname = "";
		this.uname = uname;
		this.tagged=false;
		this.bookmarked=false;

	}
 
    public UserKeyword(String uname, String keywordid, boolean tagged) {
		super();
		this.keywordid = keywordid;
		this.keywordname = "";
		this.uname = uname;
		this.tagged=tagged;
		this.bookmarked=false;

	}
 
   public UserKeyword(String uname, String keywordid, boolean tagged, boolean bookmarked) {
		super();
		this.keywordid = keywordid;
		this.keywordname = "";
		this.uname = uname;
		this.tagged=tagged;
		this.bookmarked=bookmarked;

	}

    public UserKeyword(String uname, String keywordid, String keywordname) {
		super();
		this.keywordid = keywordid;
		this.keywordname = keywordname;
		this.uname = uname;
		this.tagged=false;
		this.bookmarked=false;
	}

   public UserKeyword(String uname, String keywordid, String keywordname, boolean tagged) {
		super();
		this.keywordid = keywordid;
		this.keywordname = keywordname;
		this.uname = uname;
		this.tagged=tagged;
		this.bookmarked=false;
	}


    @Column(nullable=false)
    public String getKeywordid() {
		return keywordid;
	}

	public void setKeywordid(String keywordid) {
		this.keywordid = keywordid;
	}

	@Column(nullable=false)
	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getKeywordname() {
		return keywordname;
	}

	public void setKeywordname(String keywordname) {
		this.keywordname = keywordname;
	}

	public boolean isTagged() {
		return tagged;
	}

	public void setTagged(boolean tagged) {
		this.tagged = tagged;
	}
	
	public boolean isBookmarked() {
		return bookmarked;
	}

	public void setBookmarked(boolean bookmarked) {
		this.bookmarked = bookmarked;
	}

	

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	
	public void setid(Integer id) {
		this.id = id;
	}


}
