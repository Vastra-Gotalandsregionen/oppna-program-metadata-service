package se.vgregion.metaservice.keywordservice.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import se.vgregion.metaservice.keywordservice.entity.UserKeyword;

public class UserKeywordJpaDao implements UserKeywordDao {

	private EntityManager em;
	
	public void addUserKeyword(UserKeyword ub) {
		em.persist(ub);	
	}

	public int deleteUserKeywords(String uname) {
		Query query = em.createQuery("delete from UserKeyword uk "
                + "where uk.uname=:name");
        query.setParameter("name", uname);
        return query.executeUpdate();		
	}

	public int deleteUserKeyword(UserKeyword ub) {
		String uname=ub.getUname();
		String keywordid=ub.getKeywordid();
		String keywordname=ub.getKeywordname();
		
		Query query = em.createQuery("delete from UserKeyword uk "
                + "where uk.uname=:name and uk.keywordid=:keywordid "
                + "and uk.keywordname=:keywordname");
        query.setParameter("name", uname);
		query.setParameter("keywordid", keywordid);
		query.setParameter("keywordname", keywordname);
      return query.executeUpdate();		
	}

	public int deleteUserKeywordByID(String uname, String keywordid) {
		
		Query query = em.createQuery("delete from UserKeyword uk "
                + "where uk.uname=:name and uk.keywordid=:keywordid");
        query.setParameter("name", uname);
		query.setParameter("keywordid", keywordid);
        return query.executeUpdate();		
	}

	public int deleteUserKeywordByName(String uname, String keywordname) {
		
		Query query = em.createQuery("delete from UserKeyword uk "
                + "where uk.uname=:name and uk.keywordname=:keywordname");
        query.setParameter("name", uname);
		query.setParameter("keywordname", keywordname);
        return query.executeUpdate();		
	}

	public int updateTagFlag(UserKeyword ub) {
		String uname=ub.getUname();
		String keyid=ub.getKeywordid();
		boolean tagged=ub.isTagged();
		Query query = em.createQuery("update UserKeyword uk set uk.tagged=:tagged "
					+ "where uk.uname=:name and uk.keywordid=:keywordid");
		query.setParameter("tagged", tagged);
		query.setParameter("name", uname);
		query.setParameter("keywordid", keyid);
		return query.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	public List<UserKeyword> getUserKeywords(String uname) {
		Query query = em.createQuery("select uk from UserKeyword uk "
                + "where uk.uname=:name");
		query.setParameter("name", uname);
        return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<UserKeyword> findAllKeywords() {
		Query query = em.createQuery("SELECT ub FROM UserKeyword ub");
		return query.getResultList();
	}
	
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.em = entityManager;
	}

	public UserKeyword getUserKeyword(String uname, String keywordid) {
		Query query = em.createQuery("SELECT uk FROM UserKeyword uk WHERE uk.uname=:name AND uk.keywordid = :keywordid");
		query.setParameter("name", uname);
		query.setParameter("keywordid", keywordid);
		
		List<UserKeyword> result = query.getResultList();
		if(result.size() != 1)
			return null;
		else
			return result.get(0);
	}

	public int updateBookmarkedFlag(UserKeyword uk) {
		String uname=uk.getUname();
		String keyid=uk.getKeywordid();
		boolean bookmarked = uk.isBookmarked();
		Query query = em.createQuery("update UserKeyword uk set uk.bookmarked=:bookmarked "
					+ "where uk.uname=:name and uk.keywordid=:keywordid");
		query.setParameter("bookmarked", bookmarked);
		query.setParameter("name", uname);
		query.setParameter("keywordid", keyid);
		return query.executeUpdate();
	}

}
