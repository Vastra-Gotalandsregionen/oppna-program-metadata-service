package se.vgregion.metaservice.keywordservice;

import java.util.Iterator;
import java.util.List;
//import java.util.Map;
//import java.util.Set;

import se.vgregion.metaservice.keywordservice.dao.BlacklistedWordDao;
import se.vgregion.metaservice.keywordservice.domain.Identification;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode;
import se.vgregion.metaservice.keywordservice.domain.NodeListResponseObject;
import se.vgregion.metaservice.keywordservice.domain.ResponseObject.StatusCode;
import se.vgregion.metaservice.keywordservice.domain.document.TextDocument;
import se.vgregion.metaservice.keywordservice.entity.BlacklistedWord;
import se.vgregion.metaservice.keywordservice.exception.UnsupportedFormatException;

public class KeywordServiceTest extends BaseOpenJpaTest {

	//private static final String TEXT_TO_PROCESS = "Treatment for agoraphobia involves psychotherapy; specifically a type of therapy known as cognitive behaviour therapy (CBT). Anti-depressants and anti-anxiety medicines can also be used to help relieve the symptoms of agoraphobia. Cognitive behaviour therapy (CBT)In recent years, cognitive behaviour therapy (CBT) has achieved some success in treating people with agoraphobia. CBT is a type of psychotherapy based on the principle that the way you feel is partly dependent on the way you think about things. CBT teaches you to behave in ways that challenge negative thoughts - for example - thinking that a panic attack will occur if you are in a certain environment. Cognitive Delivered Exposure (CDE). A particular type of CBT, known as cognitive delivered exposure (CDE), is used in the treatment of agoraphobia. CDE is designed to break what many psychologists describe as the 'vicious circle' of agoraphobic thought and behaviour. A vicious circle can occur because you are afraid that you will have a panic attack if you go to the supermarket, so you avoid going to the supermarket. This means that your anxieties about visiting the supermarket are not resolved, and become worse, leading to your fear about having a panic attack becoming worse. As a result of this, the circle keeps repeating, getting worse with each turn.CDE attempts to break the vicious circle by placing you in a situation where you test your predictions of possible symptoms in real-life environments. For example, if you were worried that you would hyperventilate if you had to walk to the corner shop, your therapist would accompany you to the corner shop to see if this was the case.Often, people's perceived predictions of how they will react in certain situations are a lot worse than how they actually react.CDE begins with setting small goals before moving on to more ambitious goals which you complete by yourself, as your confidence increases.Obviously, therapists are aware that many people with agoraphobia would be reluctant to visit a clinic, or office, so a therapist can visit you, or contact you by telephone. Also, an increasing number of CBT programmes can be conducted via the internet, or through the use of interactive software.Medicine therapy.Medicines are often used in combination with CBT in order to help treat the symptoms of agoraphobia. The preferred medicines are the type of anti-depressants known as selective serotonin reuptake inhibitors (SSRIs).SSRIs are designed to work by changing the levels of neurotransmitters in your brain in a way that reduces feelings of anxiety. Side effects of SSRIs include:* nausea,* sleep problems,* headache, and * sexual dysfunction.The side effects of SSRIs tend to improve with time. However, you may have to take them for several weeks before you notice any improvement in your mood.If you are experiencing a particularly severe episode of anxiety, or panic, you may be given a short-term course of benzodiazepines. Benzodiazepines are a type of sedative which help to ease the symptoms of anxiety within 30-90 minutes of taking the medication.However, it is not recommended to take benzodiazepines for more than four weeks. This is because they have the potential to be addictive. Also, their use can be counter-productive to your cognitive behavioural therapy. Although benzodiazepines may work in reducing your anxiety, they do not allow you to confront the psychological factors that are causing your anxiety.Side effects of benzodiazepines include:   * confusion,   * loss of balance,   * memory loss,   * drowsiness, and   * light-headedness. You should not drive or operate heavy machinery while taking benzodiazepines.";
	private static final String TEXT_TO_PROCESS = "Magnetisk resonanstomografi (MRT) eller Magnetic resonance imaging (MRI) är en medicinsk teknik för bildgivande diagnostik med en magnetkamera (MR-kamera). Tekniken används för att i undersökta patienter upptäcka, lägesbestämma och klassificera vissa sjukdomar och skador som är dolda eller svåra att se vid röntgen- eller datortomografiundersökning. Några exempel är tumörer, aneurysm, ögonsjukdomar, sjukdomar i hjärnans blodkärl, men också olika organ, mjukdelar och vid vissa skelettsjukdomar. MRT rekomenderas också som alternativ till röntgen, i de fallen så är möjligt, eftersom tekniken inte använder joniserande strålning [1]. MRT-undersökningar utförs vanligen på röntgenavdelningar. Bilderna granskas sedan av röntgenläkare som ställer diagnos.";
	//private static final String DOCUMENT_TITLE = "Agoraphobia";
	private static final String DOCUMENT_TITLE = "Magnetröntgen";
    private static final String REQUEST_ID = "12m45";
	private KeyWordService keywordService;

	@Override
	protected void onSetUp() throws Exception {
		super.onSetUp();
		keywordService = (KeyWordService) applicationContext
		.getBean("metaservice.keywordService");
	}
	
	/* FIXME new test
	public void testGetKeywords() throws UnsupportedFormatException{
        TextDocument doc = new TextDocument();
        doc.setTitle(DOCUMENT_TITLE);
        doc.setTextContent(TEXT_TO_PROCESS);
        System.out.println("getting keywords");
        NodeListResponseObject response = keywordService.getKeywords(new Identification("123", "vgr"),REQUEST_ID,doc,
				null);
        System.out.println(response.getErrorMessage());
        assertEquals(StatusCode.ok, response.getStatusCode());
        List<MedicalNode> keywords = response.getNodeList();
		assertNotNull(keywords);
		assertTrue(keywords.size() > 0);

		Iterator<MedicalNode> it = keywords.iterator();
		int size = 0;
		while (it.hasNext()) {
			MedicalNode word = it.next();
			assertTrue( word.getSourceId().startsWith("A") || word.getSourceId().startsWith("C"));
		
			//Set<MedicalNode> nodes = keywords.get(word);
			//Iterator<MedicalNode> nodeIt = nodes.iterator();
			//System.out.println(MessageFormat.format("--- {0} ---", word));
			/*while (nodeIt.hasNext()) {
				MedicalNode node = nodeIt.next();
				//for (MedicalNode parent : node.getParents())
					//System.out.println(parent.getName().toUpperCase());
				//System.out.println("   " + node.getName());
			}* /
			//size += nodes.size();
		}
		System.out.println("Total hits: " + keywords.size());
	}*/

	public void testGetKeywordsWithNoContent() throws UnsupportedFormatException{
        TextDocument doc = new TextDocument();
        doc.setTitle(null);
        doc.setTextContent(null);
        NodeListResponseObject response = keywordService.getKeywords(new Identification("123", "vgr"), REQUEST_ID, doc,null);
		List<MedicalNode> keywords = response.getNodeList();
		assertNull(keywords);
	}
	
	public void testGetKeywordsWithNoHits() throws UnsupportedFormatException{
        String content = "abab bullulu mumumu";
        TextDocument doc = new TextDocument();
        doc.setTitle(null);
        doc.setTextContent(content);
		NodeListResponseObject response = keywordService.getKeywords(new Identification("123", "vgr"), REQUEST_ID,doc,null);
		List<MedicalNode> keywords = response.getNodeList();
		assertEquals(0, keywords.size());

//		BlacklistedWordDao bwd = (BlacklistedWordDao) applicationContext.getBean("blacklistedWordDao");
//		List<BlacklistedWord> bwords = bwd.getAllBlacklistedWords();
//		System.out.println("blacklisted word: "+bwords.get(0).getWord());
//		assertTrue(bwd.getAllBlacklistedWords().size() >= 3);
	}

	public void testFindNodeByInternalId() throws UnsupportedFormatException {
            NodeListResponseObject response = keywordService.getNodeByInternalId(new Identification("123", "vgr"), REQUEST_ID,"2149","VGR");
            System.out.println(response.getErrorMessage());
            assertEquals(StatusCode.ok,response.getStatusCode());
            MedicalNode node = response.getNodeList().get(0);
            assertEquals("2149",node.getInternalId());
            assertNotNull(node);
	}
	
}
