package se.vgregion.metaservice.keywordservice;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import se.vgregion.metaservice.keywordservice.dao.BlacklistedWordDao;
import se.vgregion.metaservice.keywordservice.domain.Document;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode;
import se.vgregion.metaservice.keywordservice.domain.Metadata;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode.UserStatus;
import se.vgregion.metaservice.keywordservice.entity.BlacklistedWord;
import se.vgregion.metaservice.keywordservice.entity.UserKeyword;
import se.vgregion.metaservice.keywordservice.exception.UnsupportedFormatException;
import se.vgregion.metaservice.keywordservice.processing.format.FormatProcessor;
import se.vgregion.metaservice.keywordservice.processing.format.FormatProcessorFactory;



/**
 * Service for finding keywords in text content. Uses several helper classes to
 * extract all relevant information.
 * 
 * @author tobias
 * 
 */
public class KeyWordService {
	private AnalysisService analysisService;
	private MedicalTaxonomyService medicalTaxonomyService;
	private UserProfileService userProfileService;

	private BlacklistedWordDao bwd;
	private int blacklistLimit = 20; // If an input word to apelon results in
	// more
	// hits than the blacklistLimit, the word should
	// be blacklisted. Defaults to 20

	private static Logger log = Logger.getLogger(KeyWordService.class);

	/**
	 * Sets the medicalTaxonomyService that is used at the backend to retrieve
	 * keywords
	 * 
	 * @param medicalTaxonomyService
	 *            the medicalTaxonomyService to use
	 */
	public void setMedicalTaxonomyService(
			MedicalTaxonomyService medicalTaxonomyService) {
		this.medicalTaxonomyService = medicalTaxonomyService;
	}

	/**
	 * Sets the analysisService that is used to extract representative words
	 * from the provided text content. The extracted words are later used as
	 * input parameters to the medicalTaxonomyService
	 * 
	 * @param analysisService
	 */
	public void setAnalysisService(AnalysisService analysisService) {
		this.analysisService = analysisService;
	}

	/**
	 * Gets keywords from the given text content. The content is processed in a
	 * number of steps before keywords are returned. The first step is to strip
	 * formatting from the text, based on the given format parameter. When
	 * formatting is stripped, a Document is created from the content, title and
	 * metadata. This Document is given to the analysisService that returns an
	 * array of representative words for this Document. The returned words are
	 * used as input to the medicalTaxonomyService which returns the real
	 * keywords for the provided text content.
	 * 
	 * @param content
	 *            The content to get keywords for
	 * @param title
	 *            The title of the content. Also used in the keyword extracting
	 *            phase
	 * @param userId
	 *            The userId of the author of this document. Used to personalize
	 *            the response.
	 * @param format
	 *            The format of the provided content, eg. (text, html, pdf, etc)
	 * @param metadata
	 *            Additional metadata that might be of interest when parsing the
	 *            document
	 * @param includeSourceIds
	 * 			  concepts with IDs in the list of includeSourceIds should be 
	 * 			  included in the result
	 * @return A list of MedicalNodes which are hits
	 *         in the medicalTaxonomyService for the input content.
	 * @throws UnsupportedFormatException
	 */
	public List<MedicalNode> getKeywords(String content, String title,
			String userId, String format, Metadata[] metadata, int wordLimit,
			String includeSourceIds)
			throws UnsupportedFormatException {
		if (content == null || title == null) {
			// TODO: Log error message and return error to user
			return new ArrayList<MedicalNode>();
			// return new HashMap<String, Set<MedicalNode>>();
		}

		/** * Strip formatting ** */
		log.debug("Sending title and content to formatProcessor");
		FormatProcessor formatProcessor = FormatProcessorFactory
				.getFormatProcessor(format);
		String cleanTitle = formatProcessor.process(title);
		String cleanContent = formatProcessor.process(content);
		/** ********************* */

		/** * Extract keywords ** */
		log.debug("Getting keywords");
		Document document = new Document();
		document.setTitle(cleanTitle);
		document.setContent(cleanContent);
		String[] keywords = analysisService.extractWords(document,wordLimit);
		/** ********************* */

		/** * * ** */
		log.debug("From a list of includeSourceIds to a String array of sourceIds");
		if (includeSourceIds == null) {
			includeSourceIds = "A C";
		}
		String[] sourceIds = includeSourceIds.split(" ");
		/** ************ */
		
		/** * Find medical keywords ** */
		List<MedicalNode> nodes = findMedicalNodes(keywords, userId, sourceIds);
		
		return nodes;
	}

	/**
	 * Helper method to find medical nodes in MedicalTaxonomyService
	 * @param keywords The extracted keywords from Analysis service
	 * @param userId User id of the user performing the search
	 * @param sourceIds the sourceIds of the concepts to be included
	 * @return List of Medical Nodes that were found in the MedicalTaxonomyService. The nodes has been enhanced with userstatus data
	 */
	private List<MedicalNode> findMedicalNodes(String[] keywords, String userId, String[] sourceIds) {
		Map<String, List<MedicalNode>> nodes = medicalTaxonomyService
				.findKeywords(keywords,sourceIds);
		log
				.debug(MessageFormat
						.format(
								"Keywords extracted from AnalysisService: {0}. Nodes from TaxonomyService: {1}",
								keywords.length, nodes.size()));
		/** ********************* */

		/**
		 * * Examine results from TaxonomyService and blacklist words and mark
		 * their user status **
		 */

		Set<Map.Entry<String, List<MedicalNode>>> nodesSet = nodes.entrySet();
		Map.Entry<String, List<MedicalNode>> nodeEntry;
		Set<MedicalNode> addedNodes = new HashSet<MedicalNode>();
		List<MedicalNode> nodesList = new ArrayList<MedicalNode>();
		
		for(String keyword : keywords) {
			List<MedicalNode> nodeHits = nodes.get(keyword);
			setUserStatus(userId, nodeHits);
			// Add the word to the blacklist database if it has no hits or to
			// many hits.
			log.debug(MessageFormat.format("Hits for keyword {0} is {1}",
					keyword, nodeHits.size()));
			if (nodeHits.size() == 0 || nodeHits.size() > blacklistLimit) {
				log.debug(MessageFormat.format("Blacklisting word {0}",
						keyword));
				bwd.saveBlacklistedWord(new BlacklistedWord(keyword));
			} else {
				for(MedicalNode node : nodeHits) {
					if(!addedNodes.contains(node)) {
						nodesList.add(node);
						addedNodes.add(node);
					}
				}
				log.debug(MessageFormat.format("Added {0} of {1} hits for keyword {2}. (Duplicates removed) ",nodesList.size(), nodeHits.size(),keyword));
				
			}
		}

		/** *********************************** */

		return nodesList;

	}
	
	/**
	 * Finds a specific medical node in MedicalTaxonomyService given its internal id
	 * @param internalId The internalId of a node from the Medical Taxonomy
	 * @return a Medical Node that was found in the MedicalTaxonomyService. 
	 *         The node has been enhanced with user status data
	 *         
	 * @author svetoslav
	 */

	public MedicalNode findMedicalNodeByInternalId(String internalId)
		throws UnsupportedFormatException {	
			if (internalId == null) {
				// TODO: Log error message and return error to user
				return new MedicalNode();
			}
			
		MedicalNode node = medicalTaxonomyService
			.getNodeByInternalId(internalId);	
		log
			.debug(MessageFormat
				.format(
						"InternalId provided by the user: {0}. The node name from TaxonomyService: {1}",
						internalId, node.getName()));	
		return node;
	}
	
	/**
	 * Adds a list of keywords as tagged. Should be called by the GUI to tell
	 * the service that keywords suggested by getKeywords has been used to tag
	 * the document.
	 * 
	 * @param userId
	 *            The userId of the person who tags the document
	 * @param keywordCodes
	 *            a list of codes that uniquely represents each keyword in the
	 *            taxonomy.
	 */
	public void addTaggedKeywords(String userId, List<String> keywordCodes) {
		userProfileService.addTaggedKeywords(userId, keywordCodes);
	}

	/**
	 * Adds a list of keywords as bookmarked.
	 * 
	 * @param userId
	 *            The userId of the person who bookmarks the keywords
	 * @param keywordCodes
	 *            a list of codes that uniquely represents each keyword in the
	 *            taxonomy.
	 */
	public void addBookmarkedKeywords(String userId, List<String> keywordCodes) {
		userProfileService.addBookmarkedKeywords(userId, keywordCodes);
	}

	/**
	 * Sets the blacklistedWordDao
	 * 
	 * @param bwd
	 *            the blacklistedWordDao
	 */
	public void setBlacklistedWordDao(BlacklistedWordDao bwd) {
		this.bwd = bwd;
	}

	/**
	 * Sets the minimum limit of responses from the medicalTaxonomyService to
	 * consider the word as blacklisted I.e if an extracted word gets more than
	 * hits in the taxonomy than the limit specifies, the word is considered
	 * ambiguous and is blacklisted. Default limit is 20.
	 * 
	 * @param limit
	 *            the limit
	 */
	public void setBlacklistLimit(int limit) {
		this.blacklistLimit = limit;
	}

	/**
	 * Sets the user profile to use in order to personalize the response.
	 * 
	 * @param userProfileService
	 *            the userProfileService
	 */
	public void setUserProfileService(UserProfileService userProfileService) {
		this.userProfileService = userProfileService;
	}

	private void setUserStatus(String userId, List<MedicalNode> nodes) {
		for (MedicalNode node : nodes) {
			UserKeyword keyword = userProfileService.getKeywordForUser(userId,
					node.getInternalId());
			List<UserStatus> statusList = new ArrayList<UserStatus>();
			if (keyword != null) {
				if (keyword.isTagged()) {

					statusList.add(UserStatus.TAGGED);
					node.setUserStatus(statusList);
					log.debug(MessageFormat.format(
							"Setting status tagged to node with internalId {0}", node
									.getInternalId()));
				}
				if (keyword.isBookmarked()) {
					statusList.add(UserStatus.BOOKMARKED);
					node.setUserStatus(statusList);
					log.debug(MessageFormat.format(
							"Setting status bookmarked to node with internalId {0}",
							node.getInternalId()));
				}
			}
		}
	}

}
