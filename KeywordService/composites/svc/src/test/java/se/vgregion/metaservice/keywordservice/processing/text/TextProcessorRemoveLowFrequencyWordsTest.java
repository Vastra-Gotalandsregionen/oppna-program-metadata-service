package se.vgregion.metaservice.keywordservice.processing.text;

import se.vgregion.metaservice.keywordservice.BaseSpringDependencyInjectionTest;
import se.vgregion.metaservice.keywordservice.domain.Document;

public class TextProcessorRemoveLowFrequencyWordsTest extends
		BaseSpringDependencyInjectionTest {
	
	private static final String TEXT_TO_PROCESS = "Many people with agoraphobia also have a related panic disorder and a history of panic attacks. Their agoraphobia often develops as a result of a previous panic attack. They worry about being in an environment, or situation, from which escape, or help, would be impossible, or embarrassing, if they were to have a panic attack.Also, many people worry that if they are in a situation, or environment, that provokes a panic attack, it will be life-threatening; for example they will stop breathing, or their heart will beat too fast and they will have a heart attack.Agoraphobia without panic disorder. It used to be believed that all cases of agoraphobia were related to panic disorders and panic attacks. However, research carried out in the last two decades has shown that almost half of people with agoraphobia have no previous history of panic disorder, or attacks. In such circumstances, agoraphobia may be caused by different phobias, such as fear of crime, terrorism, or illness, or accident. However, those with agoraphobia without panic disorder, are often motivated by the same fears of experiencing their first panic attack if they place themselves in a situation or environment that provokes anxiety. How common is agoraphobia? Agoraphobia related to panic disorder is one of the most common mental health conditions. It is estimated that between 4-5% of the population are affected by panic disorder and agoraphobia. Although agoraphobia without panic disorder is less common, it is by no means rare. For example, in the UK, it is estimated that 1.7% of men, and 3.8% of women, have agoraphobia without related panic disorder.";

	
	public void testRemoveLowFrequencyWords() {
		TextProcessor wordRemoverProcessor = (TextProcessor) applicationContext.getBean("wordRemoverProcessor");
		Document document = new Document();
		document.setContent(TEXT_TO_PROCESS);
		
		wordRemoverProcessor.process(document);
		
		String content = document.getContent();
		
		assertNotNull(content);
		
	}
}
