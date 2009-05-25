package se.vgregion.metaservice.keywordservice.dao;

import java.text.MessageFormat;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import se.vgregion.metaservice.keywordservice.entity.BlacklistedWord;

public class BlacklistedWordJpaDao implements BlacklistedWordDao{

	EntityManager em;
	
	Logger log = Logger.getLogger(BlacklistedWordJpaDao.class);
	
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.em = entityManager;
	}

	public List<BlacklistedWord> getAllBlacklistedWords() {
		Query query = em.createQuery("SELECT bw FROM BlacklistedWord bw");
		return query.getResultList();
	}

	public void saveBlacklistedWord(BlacklistedWord word) {
		//Check if word exists
		Query query = em.createQuery("SELECT bw FROM BlacklistedWord bw WHERE bw.word=:word");
		query.setParameter("word", word.getWord());
		List result = query.getResultList();
		if(result.size() == 0) {
			em.persist(word);
		}
		else {
			log.debug(MessageFormat.format("Word {0} is already blacklisted, will not be blacklisted again",word.getWord()));
		}
		
	}

	public BlacklistedWord getBlacklistedWordByWord(String word) {
		Query query = em.createQuery("SELECT bw FROM BlacklistedWord bw WHERE bw.word=:word");
		query.setParameter("word", word);
		List<BlacklistedWord> result = query.getResultList();
		if(result.size() != 1)
			return null;
		else
			return result.get(0);
	}

}
