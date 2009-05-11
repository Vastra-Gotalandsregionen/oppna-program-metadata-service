package se.vgregion.metaservice.keywordservice.dao;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import se.vgregion.metaservice.keywordservice.entity.BlacklistedWord;

@Transactional
/**
 * Provides methods to access blacklisted words stored in database. Persistence-specific classes should
 * implement this interface.
 */
public interface BlacklistedWordDao {

	/**
	 * Saves the given word to the database. 
	 * @param result - The word to save
	 */
	public void saveBlacklistedWord(BlacklistedWord result);
	
	/**
	 * Gets a blacklisted word with the specified word
	 * @param word the word
	 * @return The found Blacklistedword or null if no BlacklistedWord was found
	 */
	public BlacklistedWord getBlacklistedWordByWord(String word);
	
	/**
	 * Returns all blacklisted words
	 * @return a list of all blacklisted words
	 */
	public List<BlacklistedWord> getAllBlacklistedWords();
}
