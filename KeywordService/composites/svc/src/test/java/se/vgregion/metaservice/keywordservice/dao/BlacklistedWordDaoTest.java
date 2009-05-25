package se.vgregion.metaservice.keywordservice.dao;

import java.util.List;

import se.vgregion.metaservice.keywordservice.BaseOpenJpaTest;
import se.vgregion.metaservice.keywordservice.entity.BlacklistedWord;

public class BlacklistedWordDaoTest extends BaseOpenJpaTest {

		BlacklistedWordDao bwd;
	
		@Override
		protected void onSetUp() throws Exception {
			super.onSetUp();
			bwd = (BlacklistedWordDao) applicationContext
			.getBean("blacklistedWordDao");
			
		}
		
		public void testSaveBlacklistedWord() {
		String word = "huppe";
		BlacklistedWord bw = new BlacklistedWord(word);

		bwd.saveBlacklistedWord(bw);
		assertEquals(bw, bwd.getBlacklistedWordByWord(word));
	}

	public void testGetBlacklistedWordByWordNotFound() {
		String notFoundWord ="wordnotfoundinbwd";
		BlacklistedWord bw = bwd.getBlacklistedWordByWord(notFoundWord);
		assertNull(bw);
	}

}
