package se.vgregion.metaservice.keywordservice.impl;

import java.util.ArrayList;
import java.util.List;
import se.vgregion.metaservice.keywordservice.UserProfileService;
import se.vgregion.metaservice.keywordservice.entity.UserKeyword;

/**
 * No methods are implemented in this class!
 * 
 * @author martin.johansson
 */
public class UserProfileServiceEmptyImpl implements UserProfileService {

	public void addUserKeyword(UserKeyword mark) {
		
	}

	public List<UserKeyword> getKeywordsForUser(String uname) {
		return new ArrayList<UserKeyword>(0);
	}

	public UserKeyword getKeywordForUser(String uname, String keywordCode) {
		return new UserKeyword();
	}

	public List<UserKeyword> getAllKeywords() {
		return new ArrayList<UserKeyword>(0);
	}

	public void addTaggedKeywords(String userId, List<String> keywordCodes) {
		
	}

	public void addBookmarkedKeywords(String userId, List<String> keywordCodes)
	{
		
	}
}
