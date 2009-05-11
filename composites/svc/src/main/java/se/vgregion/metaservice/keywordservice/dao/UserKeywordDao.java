package se.vgregion.metaservice.keywordservice.dao;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import se.vgregion.metaservice.keywordservice.entity.UserKeyword;


@Transactional
public interface UserKeywordDao {
	
	public List<UserKeyword> getUserKeywords(String uname); 
	
	public UserKeyword getUserKeyword(String uname, String keywordid);
	
	public void addUserKeyword(UserKeyword bookmark);
	
	public int deleteUserKeywordByID(String uname, String keywordid);
	
	public int deleteUserKeywordByName(String uname, String keywordname);
	
	public int deleteUserKeywords(String uname);
	
	public int deleteUserKeyword(UserKeyword ub);
	
	public int updateTagFlag(UserKeyword ub);
	
	public int updateBookmarkedFlag(UserKeyword uk);
	
	public List<UserKeyword> findAllKeywords();
	
}
