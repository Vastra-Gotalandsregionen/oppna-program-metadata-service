package se.vgregion.metaservice.keywordservice.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;

/**
 * Entity representing the blacklisted word table. A blacklisted word consists of a word and an auto generated id.
 * @author tobias
 *
 */
@Entity
public class BlacklistedWord {

	private String word;
	private Integer id;
	
	/**
	 * Empty constructor for the blacklisted word
	 */
	public BlacklistedWord() {
		
	}
	
	/**
	 * Creates a new BlacklistedWord from the given word
	 * @param word The word
	 */
	public BlacklistedWord(String word) {
		this.word = word;
	}
	
	/**
	 * Returns the word for this blacklisted word
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * Sets the word in this blacklisted word
	 */ 
	public void setWord(String word) {
		this.word = word;
	}
	
	/**
	 * Returns the auto generated id for this blacklisted word
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	/**
	 * Sets the id for this blacklisted word. Should not be called since it's auto generated!
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
}
