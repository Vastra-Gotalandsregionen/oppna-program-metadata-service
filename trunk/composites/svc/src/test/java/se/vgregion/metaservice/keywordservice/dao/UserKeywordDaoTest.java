package se.vgregion.metaservice.keywordservice.dao;

import java.util.List;
import java.text.MessageFormat;
import se.vgregion.metaservice.keywordservice.BaseOpenJpaTest;
import se.vgregion.metaservice.keywordservice.entity.UserKeyword;
import org.apache.log4j.Logger;;

public class UserKeywordDaoTest extends BaseOpenJpaTest {

	private UserKeywordDao userkeyword;
	private static Logger log = Logger.getLogger(UserKeywordDaoTest.class);
	
	@Override
	protected void onSetUp() throws Exception {
		super.onSetUp();
		userkeyword = (UserKeywordDao) applicationContext.getBean("userKeywordDao");		
	}
	
	public void testAddUserKeyword(){
		UserKeywordDao userkeyword = (UserKeywordDao) applicationContext.getBean("userKeywordDao");
		String uname="jatra1";
		String keywordname="kalle anka";
		String keywordid="disney";
		boolean tagged=false; 
		System.out.println("##########" + uname + " " + keywordname);
		UserKeyword uk = new UserKeyword();
		uk.setKeywordname(keywordname);
		uk.setKeywordid(keywordid);
		uk.setTagged(tagged);
		uk.setUname(uname);
		userkeyword.addUserKeyword(uk);
		
		assertEquals(uk, userkeyword.getUserKeyword(uname, keywordid));
	}
	
	public void testDeleteUserKeywordByID(){
		String uname="testuser";
		String keywordname="testname";
		String keywordid="testid";
		UserKeyword uk = new UserKeyword(uname,keywordid,keywordname);
		userkeyword.addUserKeyword(uk);
		int i=userkeyword.deleteUserKeywordByID(uname, keywordid);
		assertEquals(1,i);
	}
	
	public void testDeleteUserKeywordByName(){
		String uname="testuser";
		String keywordname="testname";
		String keywordid="testid";
		UserKeyword uk = new UserKeyword(uname,keywordid,keywordname);
		userkeyword.addUserKeyword(uk);
		int i=userkeyword.deleteUserKeywordByName(uname, keywordname);
		assertEquals(1,i);
	}

	public void testDeleteUserKeywords(){
		UserKeyword uk1 = new UserKeyword("testuser1","testid1", "testname1");
		UserKeyword uk2 = new UserKeyword("testuser1","testid2", "testname2");
		userkeyword.addUserKeyword(uk1);
		userkeyword.addUserKeyword(uk2);
		int i=userkeyword.deleteUserKeywords("testuser1");
		assertEquals(2,i);
	}
	
	public void testUpdatedTagFlag() {
		UserKeyword uk1 = new UserKeyword("testuser1","testid1", false);
		userkeyword.addUserKeyword(uk1);
		UserKeyword uk2 = new UserKeyword("testuser1","testid1", true);
		int i=userkeyword.updateTagFlag(uk2);
		log.debug(MessageFormat.format("testUpdatedTagFlag: Number of updated rows = {0}",i));
		assertEquals(1,i);
	}
	
	public void testGetKeyword() {
		String uname = "testuser1";
		String id = "testid1";
		UserKeyword uk = new UserKeyword(uname, id);
		userkeyword.addUserKeyword(uk);
		
		UserKeyword savedUk = userkeyword.getUserKeyword(uname, id);
		
		assertEquals(uk, savedUk);
	}
	
	public void testGetKeywordNotFound() {
		String uname ="nousernamewiththisname9944";
		String id = "codedoesnotexist3344";
		
		assertNull(userkeyword.getUserKeyword(uname, id));
	}
}
