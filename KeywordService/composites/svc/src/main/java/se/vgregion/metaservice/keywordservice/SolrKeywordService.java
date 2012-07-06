package se.vgregion.metaservice.keywordservice;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
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
	static final String SOLR_PREFERRED_TERM = "preferred_term";
	static final String SOLR_ENTRY_TERM = "entry_term";
	
	static final String NODE_PREFERRED_TERM = "preferredSynonym";
	static final String NODE_CODE_IN_SOURCE = "Code in Source";

	private static final int MAX_NUM_RESULTS = 7; // QUES: Changeable?

	private static final String CODE_PREFIX = "C";

	private String solrConnection = null;
	private SolrServer server;
	private static Logger log = Logger.getLogger(SolrKeywordService.class);

	private Map<Integer, String[]> sourceIds;

	public void setSolrConnection(String connection) {
		solrConnection = connection;
	}

	/**
	 * Look up given keywords in a Solr instance that hold mesh information and
	 * return matching nodes. 
	 * 
	 * @param words
	 *            Array of keywords to look for. Must not be null
	 * @param sourceIds 
	 * @return A Map with a {@link List} of {@link MedicalNode}s for each
	 *         Keyword (the {@link String} key)
	 */
	public Map<String, List<MedicalNode>> findKeywords(String[] words, Map<Integer, String[]> sourceIds) {
		if (words == null) {
			throw new IllegalArgumentException("Argument words can't be null");
		}

		this.sourceIds = sourceIds;
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
						if(nodeMatchesSourceIds(node)) {
							medicalNodes.add(node);
						}
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

	private boolean nodeMatchesSourceIds(MedicalNode node) {
		String namespaceId = node.getNamespaceId();
		String[] ids = sourceIds.get(Integer.parseInt(namespaceId));
		if (ids == null) {
			System.out.println("no source ids to match on, returning true");
            return true;
        }
		for (NodeProperty property : node.getProperties()) {
			if(property.getName().equals(SOLR_PROP_MN.toUpperCase())) {
				String prop_mn = property.getValue();
				for (String id : ids) {
					System.out.println("comparing "+id+" to "+prop_mn);
					if(prop_mn.startsWith(id)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Populates given node with data from the SolrDocument
	 * TODO: add parents?
	 */
	private void populateNode(SolrDocument solrDocument, MedicalNode node) {
		Object field_Name = solrDocument.getFieldValue(SOLR_NAME);
		Object field_ID = solrDocument.getFieldValue(SOLR_ID);
		Object field_NamespaceID = solrDocument.getFieldValue(SOLR_NAMESPACE_ID);
//		Object field_SourceID = solrDocument.getFieldValue(SOLR_SOURCE_ID);
		Object field_Code = solrDocument.getFieldValue(SOLR_PROP_CODE);
		Object field_MN = solrDocument.getFieldValue(SOLR_PROP_MN);
		Object field_ScopeNoteEng = solrDocument.getFieldValue(SOLR_PROP_SCOPE_NOTE_ENG);
		Object field_ScopeNoteSwe = solrDocument.getFieldValue(SOLR_PROP_SCOPE_NOTE_SWE);
		Object field_PreferredTerm = solrDocument.getFieldValue(SOLR_PREFERRED_TERM);

		log.debug("Creating node with name " + field_Name);
		
		if (field_Name != null) {
			node.setName(field_Name.toString());
		}
		if(field_ID != null) {
			node.setInternalId(field_ID.toString());
		}
		if(field_NamespaceID != null) {
			field_NamespaceID = checkForCollection(field_NamespaceID, true);
			node.setNamespaceId(field_NamespaceID.toString());
		}
		if(field_Code != null) {
			node.setSourceId(field_Code.toString());
		}
		
		node.setSynonyms(getSynonyms(solrDocument));

		List<NodeProperty> properties = new ArrayList<NodeProperty>();

		if(field_PreferredTerm != null) {
			field_PreferredTerm = checkForCollection(field_PreferredTerm, true);
			properties.add(new NodeProperty(NODE_PREFERRED_TERM, field_PreferredTerm.toString()));
		}
		
		if(field_Code != null) {
			properties.add(new NodeProperty(SOLR_PROP_CODE, CODE_PREFIX + field_Code.toString()));
			properties.add(new NodeProperty(NODE_CODE_IN_SOURCE, field_Code.toString()));
		}
		if(field_MN != null) {
			field_MN = checkForCollection(field_MN, true);
			properties.add(new NodeProperty(SOLR_PROP_MN.toUpperCase(), field_MN.toString()));
		}
		if (field_ScopeNoteEng != null) {
			field_ScopeNoteEng = checkForCollection(field_ScopeNoteEng, true);
			properties.add(new NodeProperty(SOLR_PROP_SCOPE_NOTE_ENG, field_ScopeNoteEng.toString()));
		}
		if (field_ScopeNoteSwe != null) {
			field_ScopeNoteSwe = checkForCollection(field_ScopeNoteSwe, true);
			properties.add(new NodeProperty(SOLR_PROP_SCOPE_NOTE_SWE, field_ScopeNoteSwe.toString()));
		}

		node.setProperties(properties);
	}

	private Object checkForCollection(Object object, boolean returnFirst) {
		if(object instanceof Collection<?> ) {
			Collection<?> collection = ((Collection<?>)object);
			if(returnFirst) {
				if(collection.isEmpty()) {
					object = null;
				} else {
					object = collection.iterator().next();
				}
			} else {
				object = new ArrayList<Object>(collection);
			}
		}
		return object;
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
		query.addField(SOLR_PREFERRED_TERM);
		query.addField(SOLR_ENTRY_TERM);
		query.set("qf"," "+SOLR_ID+" "+SOLR_NAME+" "+" "+SOLR_NAMESPACE_ID+" "+SOLR_PROP_CODE+" "+SOLR_PROP_MN+" "+
				           /*SOLR_PROP_SCOPE_NOTE_ENG+" "+SOLR_PROP_SCOPE_NOTE_SWE+" "+*/
				           SOLR_SOURCE_ID+" "+SOLR_SYNONYMS+" "+
				           SOLR_PREFERRED_TERM+" "+SOLR_ENTRY_TERM);
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
