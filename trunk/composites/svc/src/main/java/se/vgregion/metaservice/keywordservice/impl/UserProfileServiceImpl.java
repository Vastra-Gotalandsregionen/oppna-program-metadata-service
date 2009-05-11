package se.vgregion.metaservice.keywordservice.impl;

import java.util.List;

import se.vgregion.metaservice.keywordservice.UserProfileService;
import se.vgregion.metaservice.keywordservice.dao.UserKeywordDao;
import se.vgregion.metaservice.keywordservice.entity.UserKeyword;

public class UserProfileServiceImpl implements UserProfileService {

	public void addUserKeyword(UserKeyword mark) {
		userKeywordDao.addUserKeyword(mark);

	}

	private UserKeywordDao userKeywordDao;

	public void setUserKeywordDao(UserKeywordDao userKeywordDao) {
		this.userKeywordDao = userKeywordDao;
	}

	public List<UserKeyword> getKeywordsForUser(String uname) {
		List<UserKeyword> uk = userKeywordDao.getUserKeywords(uname);
		return uk;
	}

	public List<UserKeyword> getAllKeywords() {
		return userKeywordDao.findAllKeywords();
	}

	public void addTaggedKeywords(String userId, List<String> newKeywordCodes) {
		List<UserKeyword> currentList = getKeywordsForUser(userId);

		if (currentList != null) {
			if (currentList.isEmpty()) {
				for (String newvalue : newKeywordCodes) {
					UserKeyword uk = new UserKeyword(userId, newvalue, true);
					userKeywordDao.addUserKeyword(uk);
				}
			} else {
				for (String newvalue : newKeywordCodes) {
					UserKeyword uk = new UserKeyword(userId, newvalue, true);
					if (containsKeywordWithCode(currentList, newvalue)) {
						userKeywordDao.updateTagFlag(uk);
					} else {
						userKeywordDao.addUserKeyword(uk);
					}
				}
			}
		}
	}

	public UserKeyword getKeywordForUser(String uname, String keywordCode) {
		return userKeywordDao.getUserKeyword(uname, keywordCode);
	}

	public void addBookmarkedKeywords(String userId, List<String> keywordCodes) {
		List<UserKeyword> currentList = getKeywordsForUser(userId);

		if (currentList != null) {
			if (currentList.isEmpty()) {
				for (String newvalue : keywordCodes) {
					UserKeyword uk = new UserKeyword(userId, newvalue, false,
							true);
					userKeywordDao.addUserKeyword(uk);
				}
			} else {
				for (String newvalue : keywordCodes) {
					UserKeyword uk = new UserKeyword(userId, newvalue, false,
							true);
					if (containsKeywordWithCode(currentList, newvalue)) {
						userKeywordDao.updateBookmarkedFlag(uk);
					} else {
						userKeywordDao.addUserKeyword(uk);
					}
				}
			}
		}

	}

	private static boolean containsKeywordWithCode(List<UserKeyword> list,
			String code) {
		for (UserKeyword uk : list) {
			if (uk.getKeywordid().equals(code))
				return true;
		}
		return false;
	}

}
