package se.vgregion.metaservice.keywordservice;

import java.util.logging.Level;
import java.util.logging.Logger;
import se.vgregion.metaservice.keywordservice.domain.document.AnalysisDocument;
import se.vgregion.metaservice.keywordservice.exception.ProcessingException;

public class AnalysisServiceTest extends BaseOpenJpaTest {

    //private static final String TEXT_TO_PROCESS = "Many people with agoraphobia also have a related panic disorder and a history of panic attacks. Their agoraphobia often develops as a result of a previous panic attack. They worry about being in an environment, or situation, from which escape, or help, would be impossible, or embarrassing, if they were to have a panic attack.Also, many people worry that if they are in a situation, or environment, that provokes a panic attack, it will be life-threatening; for example they will stop breathing, or their heart will beat too fast and they will have a heart attack.Agoraphobia without panic disorder. It used to be believed that all cases of agoraphobia were related to panic disorders and panic attacks. However, research carried out in the last two decades has shown that almost half of people with agoraphobia have no previous history of panic disorder, or attacks. In such circumstances, agoraphobia may be caused by different phobias, such as fear of crime, terrorism, or illness, or accident. However, those with agoraphobia without panic disorder, are often motivated by the same fears of experiencing their first panic attack if they place themselves in a situation or environment that provokes anxiety. How common is agoraphobia? Agoraphobia related to panic disorder is one of the most common mental health conditions. It is estimated that between 4-5% of the population are affected by panic disorder and agoraphobia. Although agoraphobia without panic disorder is less common, it is by no means rare. For example, in the UK, it is estimated that 1.7% of men, and 3.8% of women, have agoraphobia without related panic disorder.";
    private static final String TEXT_TO_PROCESS = "Treatment for agoraphobia involves psychotherapy; specifically a type of therapy known as cognitive behaviour therapy (CBT). Anti-depressants and anti-anxiety medicines can also be used to help relieve the symptoms of agoraphobia. Cognitive behaviour therapy (CBT)In recent years, cognitive behaviour therapy (CBT) has achieved some success in treating people with agoraphobia. CBT is a type of psychotherapy based on the principle that the way you feel is partly dependent on the way you think about things. CBT teaches you to behave in ways that challenge negative thoughts - for example - thinking that a panic attack will occur if you are in a certain environment. Cognitive Delivered Exposure (CDE). A particular type of CBT, known as cognitive delivered exposure (CDE), is used in the treatment of agoraphobia. CDE is designed to break what many psychologists describe as the 'vicious circle' of agoraphobic thought and behaviour. A vicious circle can occur because you are afraid that you will have a panic attack if you go to the supermarket, so you avoid going to the supermarket. This means that your anxieties about visiting the supermarket are not resolved, and become worse, leading to your fear about having a panic attack becoming worse. As a result of this, the circle keeps repeating, getting worse with each turn.CDE attempts to break the vicious circle by placing you in a situation where you test your predictions of possible symptoms in real-life environments. For example, if you were worried that you would hyperventilate if you had to walk to the corner shop, your therapist would accompany you to the corner shop to see if this was the case.Often, people's perceived predictions of how they will react in certain situations are a lot worse than how they actually react.CDE begins with setting small goals before moving on to more ambitious goals which you complete by yourself, as your confidence increases.Obviously, therapists are aware that many people with agoraphobia would be reluctant to visit a clinic, or office, so a therapist can visit you, or contact you by telephone. Also, an increasing number of CBT programmes can be conducted via the internet, or through the use of interactive software.Medicine therapy.Medicines are often used in combination with CBT in order to help treat the symptoms of agoraphobia. The preferred medicines are the type of anti-depressants known as selective serotonin reuptake inhibitors (SSRIs).SSRIs are designed to work by changing the levels of neurotransmitters in your brain in a way that reduces feelings of anxiety. Side effects of SSRIs include:* nausea,* sleep problems,* headache, and * sexual dysfunction.The side effects of SSRIs tend to improve with time. However, you may have to take them for several weeks before you notice any improvement in your mood.If you are experiencing a particularly severe episode of anxiety, or panic, you may be given a short-term course of benzodiazepines. Benzodiazepines are a type of sedative which help to ease the symptoms of anxiety within 30-90 minutes of taking the medication.However, it is not recommended to take benzodiazepines for more than four weeks. This is because they have the potential to be addictive. Also, their use can be counter-productive to your cognitive behavioural therapy. Although benzodiazepines may work in reducing your anxiety, they do not allow you to confront the psychological factors that are causing your anxiety.Side effects of benzodiazepines include:   * confusion,   * loss of balance,   * memory loss,   * drowsiness, and   * light-headedness. You should not drive or operate heavy machinery while taking benzodiazepines.";
    private AnalysisService analysisService;

    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        analysisService = (AnalysisService) applicationContext.getBean("metaservice.analysisService");
    }

    public void testExtractKeywords() {
        AnalysisDocument doc = new AnalysisDocument();
        doc.setTextContent(TEXT_TO_PROCESS);
        String[] keywords = null;
        try {
            keywords = analysisService.extractWords(doc, 10);
        } catch (ProcessingException ex) {
            //
        }
        assertNotNull(keywords);
        assertTrue(keywords.length > 0);
        /*for(String keyword : keywords)
        System.out.println(keyword); */
    }

    public void testExtractZeroKeywords() {
        AnalysisDocument doc = new AnalysisDocument();
        doc.setTextContent("");
        String[] keywords = null;
        try {
            keywords = analysisService.extractWords(doc, 10);
        } catch (ProcessingException ex) {
           //
        }
        assertNotNull(keywords);
        assertEquals(0, keywords.length);
    }

    public void testReturnLimit() {
        AnalysisDocument doc = new AnalysisDocument();
        doc.setTextContent(TEXT_TO_PROCESS);
        String[] keywords = null;
        try {
            keywords = analysisService.extractWords(doc, 10);
        } catch (ProcessingException ex) {
           //
        }
        assertNotNull(keywords);
        assertEquals(10, keywords.length);
    }
}
