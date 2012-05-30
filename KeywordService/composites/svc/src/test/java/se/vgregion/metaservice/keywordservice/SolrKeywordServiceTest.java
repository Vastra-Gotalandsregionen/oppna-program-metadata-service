package se.vgregion.metaservice.keywordservice;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static se.vgregion.metaservice.keywordservice.SolrKeywordService.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.SolrParams;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.vgregion.metaservice.keywordservice.domain.MedicalNode;
import se.vgregion.metaservice.keywordservice.domain.NodeProperty;

public class SolrKeywordServiceTest {

	private static final String KEYWORD_QUERY = "diabetes";
	
	@Mock
	protected SolrServer server;

	private SolrKeywordService testee;

	private SolrDocument document;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		QueryResponse response = mock(QueryResponse.class);
		SolrDocumentList documentsList = new SolrDocumentList();
		document = createDocument();
		documentsList.add(document);
		documentsList.setNumFound(documentsList.size());
		when(response.getResults()).thenReturn(documentsList);
		when(server.query(any(SolrParams.class))).thenReturn(response);

		testee = new SolrKeywordService();
	}
	
	@Test
	public void testFindKeywordsMatchesOnFirst() throws Exception {
		testee.setSolrServer(server);

		// TEST
		String[] words = { KEYWORD_QUERY };
		Map<String, List<MedicalNode>> keywords = testee.findKeywords(words);

		// VERIFY
		assertEquals(1, keywords.size());
		List<MedicalNode> nodeList = keywords.get(KEYWORD_QUERY);
		assertEquals(1, nodeList.size());
		verifyNode(nodeList.get(0), document);
	}
	
	@Test
	public void testSolrConnectionIsNullShouldThrowRuntimeException() throws Exception {
		String[] words = { KEYWORD_QUERY };
		try {
			testee.findKeywords(words);
			fail();
		} catch(RuntimeException e) {
			assertEquals(MISSING_CONNECTION_STRING_ERR_MSG, e.getMessage());
		}
	}
	
	protected void verifyNode(MedicalNode node, SolrDocument document) {
		assertEquals(document.getFieldValue(SOLR_ID), node.getInternalId());
		assertEquals(document.getFieldValue(SOLR_NAME), node.getName());
		assertEquals(document.getFieldValue(SOLR_NAMESPACE_ID), node.getNamespaceId());
		// node.getParents();

		assertEquals(document.getFieldValue(SOLR_PROP_CODE), node.getSourceId());
		assertEquals(document.getFieldValue(SOLR_SYNONYMS), node.getSynonyms());

		List<NodeProperty> properties = node.getProperties();
		for (NodeProperty nodeProperty : properties) {
			if (SOLR_PROP_MN.equals(nodeProperty.getName())) {
				assertEquals(document.getFieldValue(SOLR_PROP_MN), nodeProperty.getValue());
			} else if (SOLR_PROP_SCOPE_NOTE_ENG.equals(nodeProperty.getName())) {
				assertEquals(document.getFieldValue(SOLR_PROP_SCOPE_NOTE_ENG), nodeProperty.getValue());
			} else if (SOLR_PROP_SCOPE_NOTE_SWE.equals(nodeProperty.getName())) {
				assertEquals(document.getFieldValue(SOLR_PROP_SCOPE_NOTE_SWE), nodeProperty.getValue());
			} else if (SOLR_PROP_CODE.equals(nodeProperty.getName())) {
				// Code should be prefixed by a 'C'
				assertEquals("C"+document.getFieldValue(SOLR_PROP_CODE), nodeProperty.getValue());
			}
		}
	}

	private SolrDocument createDocument() {
		SolrDocument document = mock(SolrDocument.class);
		when(document.getFieldValue(SOLR_NAME)).thenReturn("namnet");
		when(document.getFieldValue(SOLR_ID)).thenReturn("idt");
		when(document.getFieldValue(SOLR_SOURCE_ID)).thenReturn("source_idt");
		List<String> synList = new ArrayList<String>();
		synList.add("synonym");
		when(document.getFieldValue(SOLR_SYNONYMS)).thenReturn(synList);
		when(document.getFieldValue(SOLR_NAMESPACE_ID)).thenReturn("nsid");
		when(document.getFieldValue(SOLR_PROP_CODE)).thenReturn("code");
		when(document.getFieldValue(SOLR_PROP_MN)).thenReturn("mn");
		return document;
	}

	
	// Use as a integration test, just unIgnore and change solr connection 
	// in your development environment
	@Test
	@Ignore
	public void integrationtest() throws Exception {
		SolrKeywordService testee = new SolrKeywordService();
		testee.setSolrConnection("http://localhost:8081/solr/swemeshCore/");
		String[] words = { KEYWORD_QUERY };
		Map<String, List<MedicalNode>> keywords = testee.findKeywords(words);
		assertEquals(1, keywords.size());
		List<MedicalNode> nodes = keywords.get(KEYWORD_QUERY);
		assertTrue(nodes.size() > 0);
	}
	
}
