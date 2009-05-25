package se.vgregion.metaservice.keywordservice;

import java.util.List;

import se.vgregion.metaservice.keywordservice.entity.UserKeyword;

public interface UserProfileService {

	public void addUserKeyword(UserKeyword mark);
	
	public List<UserKeyword> getKeywordsForUser(String uname);
	
	public UserKeyword getKeywordForUser(String uname, String keywordCode);
	
	public List<UserKeyword> getAllKeywords();

	public void addTaggedKeywords(String userId, List<String> keywordCodes);
	
	public void addBookmarkedKeywords(String userId, List<String> keywordCodes);
}
