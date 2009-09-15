package se.vgregion.metaservice.keywordservice;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import se.vgregion.metaservice.keywordservice.dao.BlacklistedWordDao;
import se.vgregion.metaservice.keywordservice.domain.Identification;
import se.vgregion.metaservice.keywordservice.domain.document.AnalysisDocument;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode;
import se.vgregion.metaservice.keywordservice.domain.Metadata;
import se.vgregion.metaservice.keywordservice.domain.Options;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode.UserStatus;
import se.vgregion.metaservice.keywordservice.domain.NodeListResponseObject;
import se.vgregion.metaservice.keywordservice.domain.ResponseObject.StatusCode;
import se.vgregion.metaservice.keywordservice.domain.document.Document;
import se.vgregion.metaservice.keywordservice.domain.document.FileDocument;
import se.vgregion.metaservice.keywordservice.domain.document.TextDocument;
import se.vgregion.metaservice.keywordservice.entity.BlacklistedWord;
import se.vgregion.metaservice.keywordservice.entity.UserKeyword;
import se.vgregion.metaservice.keywordservice.exception.FormattingException;
import se.vgregion.metaservice.keywordservice.exception.KeywordsException;
import se.vgregion.metaservice.keywordservice.exception.ProcessingException;
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
     * formatting is stripped, a Document is created from the content and title
     * This Document is given to the analysisService that returns an
     * array of representative words for this Document. The returned words are
     * used as input to the medicalTaxonomyService which returns the real
     * keywords for the provided text content.
     *
     * @param id
     *            Identification data, the identificationobject contains both
     *            userId and profile. Used to personalize the responce
     * @param requestId
     *            The unique id that is to be associated with this request
     * @param document
     *            The content to classify
     * @param options
     *            Additional options
     * @return A NodeListResponseObject that contains a list of MedicalNodes 
     *         which are hits in the medicalTaxonomyService for the input content.
     *         Check the responseObjects statusCode to see if the operation
     *         was successful.
     */

    public NodeListResponseObject getKeywords(Identification id, String requestId, Document document, Options options) {

        try {
            /** * Determine format ** */
            String format = "";
            // if the document is a file
            if (document instanceof FileDocument) {
                FileDocument fileDocument = (FileDocument) document;
                int index = fileDocument.getFilename().lastIndexOf(".");
                if (index > 0) {
                    format = fileDocument.getFilename().substring(index);
                }
                // if the document is a text
            } else if (document instanceof TextDocument) {
                format = "html_text";
            }
            /** * Strip formatting ** */
            log.debug(requestId + ":Sending title and content to formatProcessor");
            FormatProcessor formatProcessor = FormatProcessorFactory.getFormatProcessor(format);
            AnalysisDocument analysisDocument = formatProcessor.process(document);

            /** * Extract keywords ** */
            log.debug(requestId + ":Getting keywords");
            String[] keywords = analysisService.extractWords(analysisDocument, options.getWordLimit());

            /** * Find medical keywords ** */
            log.debug(requestId + ":Translating keywords to medicalNodes");
            List<MedicalNode> nodes = findMedicalNodes(keywords, id.getUserId(), options.getSourceIds());

            /** * Return an NodeListResponseObject with statusCode ok (200) * **/
            return new NodeListResponseObject(requestId, nodes);

        } catch (UnsupportedFormatException ex) {
            log.error(requestId + ":" + StatusCode.unsupported_text_format.code() +
                    ":The supplied textformat or fileType is not supported");
            return new NodeListResponseObject(requestId, StatusCode.unsupported_text_format,
                    "The supplied textformat or fileType is not supported");
        } catch (FormattingException ex) {
            log.error(requestId + ":" + StatusCode.error_formatting_content.code() +
                    ":The formatting of the file or text failed", ex);
            return new NodeListResponseObject(requestId, StatusCode.error_formatting_content,
                    "The formatting of the file or text failed");
        } catch (ProcessingException ex) {
            log.error(requestId + ":" + StatusCode.error_processing_content.code() +
                    ":The processing (generation of keywords) of the file or text failed", ex);
            return new NodeListResponseObject(requestId, StatusCode.error_processing_content,
                    "The processing (generation of keywords) of the file or text failed");
        } catch (KeywordsException ex) {
            log.error(requestId + ":" + StatusCode.error_getting_keywords_from_taxonomy.code() +
                    ":The translation of keywords for the file or text failed", ex);
            return new NodeListResponseObject(requestId, StatusCode.error_getting_keywords_from_taxonomy,
                    "The translation of keywords for the file or text failed");
        }
    }

    /**
     * Helper method to find medical nodes in MedicalTaxonomyService
     * @param keywords The extracted keywords from Analysis service
     * @param userId User id of the user performing the search
     * @param sourceIds the sourceIds of the concepts to be included
     * @return List of Medical Nodes that were found in the MedicalTaxonomyService. The nodes has been enhanced with userstatus data
     */
    private List<MedicalNode> findMedicalNodes(String[] keywords, String userId, String[] sourceIds) throws KeywordsException{

        //TODO:Make so this method actualy throws a KeywordsException!


        Map<String, List<MedicalNode>> nodes = medicalTaxonomyService.findKeywords(keywords, sourceIds);
        log.debug(MessageFormat.format(
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

        for (String keyword : keywords) {
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
                for (MedicalNode node : nodeHits) {
                    if (!addedNodes.contains(node)) {
                        nodesList.add(node);
                        addedNodes.add(node);
                    }

                }
                log.debug(MessageFormat.format("Added {0} of {1} hits for keyword {2}. (Duplicates removed) ", nodesList.size(), nodeHits.size(), keyword));

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
    public MedicalNode findMedicalNodeByInternalId(
            String internalId)
            throws UnsupportedFormatException {
        if (internalId == null) {
            // TODO: Log error message and return error to user
            return new MedicalNode();
        }

        MedicalNode node = medicalTaxonomyService.getNodeByInternalId(internalId);
        log.debug(MessageFormat.format(
                "InternalId provided by the user: {0}. The node name from TaxonomyService: {1}",
                internalId, node.getName()));
        return node;
    }

    /**
     * Adds a list of keywords as tagged. Should be called by the GUI to tell
     * the service that keywords suggested by getKeywords has been used to tag
     * the document.
     *
     * @param id
     *            The identification of the person who tags the document
     * @param requestId
     *            The unique id that is to be associated with this request
     * @param keywordIds
     *            a list of ids that uniquely represents each keyword in the
     *            taxonomy.
     */
    public void addTaggedKeywords(Identification id, String requestId, List<String> keywordIds) {
        userProfileService.addTaggedKeywords(id.getUserId(), keywordIds);
    }

    /**
     * Adds a list of keywords as bookmarked.
     *
     * @param id
     *            The identification of the person who tags the document
     * @param requestId
     *            The unique id that is to be associated with this request
     * @param keywordIds
     *            a list of ids that uniquely represents each keyword in the
     *            taxonomy.
     */
    public void addBookmarkedKeywords(Identification id, String requestId, List<String> keywordIds) {
        userProfileService.addBookmarkedKeywords(id.getUserId(), keywordIds);
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
                            "Setting status tagged to node with internalId {0}", node.getInternalId()));
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
