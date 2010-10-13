package se.vgregion.metaservice.keywordservice.dao;

import java.util.ArrayList;
import java.util.List;
import se.vgregion.metaservice.keywordservice.entity.BlacklistedWord;

/**
 * Does nothing, just returns empty objects and lists.
 * 
 * @author martin.johansson
 */
public class BlacklistedWordEmptyImpl implements BlacklistedWordDao {

	public void saveBlacklistedWord(BlacklistedWord result)
	{

	}

	public BlacklistedWord getBlacklistedWordByWord(String word)
	{
		return new BlacklistedWord();
	}

	public List<BlacklistedWord> getAllBlacklistedWords()
	{
		return new ArrayList<BlacklistedWord>(0);
	}

}
