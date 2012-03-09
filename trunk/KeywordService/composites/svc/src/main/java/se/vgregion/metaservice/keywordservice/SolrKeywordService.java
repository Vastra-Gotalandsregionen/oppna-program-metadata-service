package se.vgregion.metaservice.keywordservice;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import se.vgregion.metaservice.keywordservice.domain.MedicalNode;
import se.vgregion.metaservice.keywordservice.domain.NodeProperty;

/**
 * Used for looking up keywords in a Solr server
 * 
 */
public class SolrKeywordService {

	static final String MISSING_CONNECTION_STRING_ERR_MSG = "No connection string given for Keyword Solr server";

	static final String SOLR_ID = "id";
	static final String SOLR_NAME = "name";
	static final String SOLR_PROP_MN = "mn";
	static final String SOLR_PROP_SCOPE_NOTE_ENG = "scope_note_eng";
	static final String SOLR_PROP_SCOPE_NOTE_SWE = "scope_note_swe";
	static final String SOLR_PROP_CODE = "code";
	static final String SOLR_SOURCE_ID = "code_in_source";
	static final String SOLR_SYNONYMS = "synonyms"; // TODO Use synonyms or
													// synonyms_org?
	static final String SOLR_NAMESPACE_ID = "namespace_id";

	private static final int MAX_NUM_RESULTS = 10;

	private String solrConnection = null;
	private SolrServer server;
	private static Logger log = Logger.getLogger(SolrKeywordService.class);

	public void setSolrConnection(String connection) {
		solrConnection = connection;
	}

	/**
	 * Look up given keywords in a Solr instance that hold mesh information and
	 * return matching nodes. 
	 * 
	 * @param words
	 *            Array of keywords to look for. Must not be null
	 * @return A Map with a {@link List} of {@link MedicalNode}s for each
	 *         Keyword (the {@link String} key)
	 */
	public Map<String, List<MedicalNode>> findKeywords(String[] words) {

		if (words == null) {
			throw new IllegalArgumentException("Argument words can't be null");
		}

		Map<String, List<MedicalNode>> resultMap = new HashMap<String, List<MedicalNode>>();
		
		try {
			for (int i = 0; i < words.length; i++) {
				SolrServer server = getSolrServer();
				String word = words[i];
				List<MedicalNode> medicalNodes = new ArrayList<MedicalNode>();

				SolrQuery query = createQuery(word);
				QueryResponse response = server.query(query);
				SolrDocumentList results = response.getResults();

				log.debug("found "+results.getNumFound()+" matches for keyword " + word);
				
				for (SolrDocument solrDocument : results) {
					MedicalNode node = new MedicalNode();
					try {
						populateNode(solrDocument, node);
						medicalNodes.add(node);
					} catch (Exception e) {
						log.error("Could not copy solr document to medical node", e);
					}
				}
				resultMap.put(word, medicalNodes);
			}

		} catch (MalformedURLException e) {
			throw new RuntimeException("SolrKeywordService: Problem with Solr connection URL", e);
		} catch (SolrServerException e) {
			throw new RuntimeException("SolrKeywordService: Problem with Solr Server", e);
		}

		return resultMap;
	}

	/**
	 * Populates given node with data from the SolrDocument
	 * TODO: add parents?
	 */
	private void populateNode(SolrDocument solrDocument, MedicalNode node) {
		Object field_Name = solrDocument.getFieldValue(SOLR_NAME);
		Object field_ID = solrDocument.getFieldValue(SOLR_ID);
		Object field_NamespaceID = solrDocument.getFieldValue(SOLR_NAMESPACE_ID);
		Object field_SourceID = solrDocument.getFieldValue(SOLR_SOURCE_ID);
		Object field_Code = solrDocument.getFieldValue(SOLR_PROP_CODE);
		Object field_MN = solrDocument.getFieldValue(SOLR_PROP_MN);
		Object field_ScopeNoteEng = solrDocument.getFieldValue(SOLR_PROP_SCOPE_NOTE_ENG);
		Object field_ScopeNoteSwe = solrDocument.getFieldValue(SOLR_PROP_SCOPE_NOTE_SWE);

		log.debug("Creating node with name " + field_Name);

		if (field_Name != null) {
			node.setName(field_Name.toString());
		}
		if(field_ID != null) {
			node.setInternalId(field_ID.toString());
		}
		if(field_NamespaceID != null) {
			node.setNamespaceId(field_NamespaceID.toString());
		}
		if(field_SourceID != null) {
			node.setSourceId(field_SourceID.toString());
		}
		
		node.setSynonyms(getSynonyms(solrDocument));

		List<NodeProperty> properties = new ArrayList<NodeProperty>();

		if(field_Code != null) {
			properties.add(new NodeProperty(SOLR_PROP_CODE, field_Code.toString()));
		}
		if(field_MN != null) {
			properties.add(new NodeProperty(SOLR_PROP_MN, field_MN.toString()));
		}
		if (field_ScopeNoteEng != null) {
			properties.add(new NodeProperty(SOLR_PROP_SCOPE_NOTE_ENG, field_ScopeNoteEng.toString()));
		}
		if (field_ScopeNoteSwe != null) {
			properties.add(new NodeProperty(SOLR_PROP_SCOPE_NOTE_SWE, field_ScopeNoteSwe.toString()));
		}

		node.setProperties(properties);
	}

	@SuppressWarnings("unchecked")
	private List<String> getSynonyms(SolrDocument solrDocument) {
		List<String> synonyms;
		Object value = solrDocument.getFieldValue(SOLR_SYNONYMS);
		if (value instanceof List) {
			synonyms = (List<String>) value;
		} else {
			synonyms = new ArrayList<String>();
			synonyms.add((String) value);
		}
		return synonyms;
	}

	private SolrQuery createQuery(String word) {
		SolrQuery query = new SolrQuery();
		query.setQuery(word);
		query.addField(SOLR_ID);
		query.addField(SOLR_NAME);
		query.addField(SOLR_NAMESPACE_ID);
		query.addField(SOLR_PROP_CODE);
		query.addField(SOLR_PROP_MN);
		query.addField(SOLR_PROP_SCOPE_NOTE_ENG);
		query.addField(SOLR_PROP_SCOPE_NOTE_SWE);
		query.addField(SOLR_SOURCE_ID);
		query.addField(SOLR_SYNONYMS);
		query.setRows(MAX_NUM_RESULTS);
		query.setStart(0);
		return query;
	}

	protected SolrServer getSolrServer() throws MalformedURLException {
		if (server == null) {
			if (solrConnection != null) {
				server = new CommonsHttpSolrServer(solrConnection);
			} else {
				throw new RuntimeException(MISSING_CONNECTION_STRING_ERR_MSG);
			}
		}
		return server;
	}

	void setSolrServer(SolrServer server) {
		this.server = server;
	}

}
