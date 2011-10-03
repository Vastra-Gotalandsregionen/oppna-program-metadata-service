package se.vgregion.metaservice.keywordservice;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import se.vgregion.metaservice.keywordservice.dao.BlacklistedWordDao;
import se.vgregion.metaservice.keywordservice.domain.Identification;
import se.vgregion.metaservice.keywordservice.domain.document.AnalysisDocument;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode;
import se.vgregion.metaservice.keywordservice.domain.Options;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode.UserStatus;
import se.vgregion.metaservice.keywordservice.domain.NodeListResponseObject;
import se.vgregion.metaservice.keywordservice.domain.ResponseObject;
import se.vgregion.metaservice.keywordservice.domain.ResponseObject.StatusCode;
import se.vgregion.metaservice.keywordservice.domain.SearchProfile;
import se.vgregion.metaservice.keywordservice.domain.document.Document;
import se.vgregion.metaservice.keywordservice.domain.document.FileDocument;
import se.vgregion.metaservice.keywordservice.domain.document.TextDocument;
import se.vgregion.metaservice.keywordservice.entity.BlacklistedWord;
import se.vgregion.metaservice.keywordservice.entity.UserKeyword;
import se.vgregion.metaservice.keywordservice.exception.FormattingException;
import se.vgregion.metaservice.keywordservice.exception.KeywordsException;
import se.vgregion.metaservice.keywordservice.exception.NodeNotFoundException;
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

    //TODO: put this enum somewhere more apropriate?
    public enum ListType {blackList,whiteList,none};
    private AnalysisService analysisService;
    private MedicalTaxonomyService medicalTaxonomyService;
    private UserProfileService userProfileService;
    private BlacklistedWordDao bwd;
    private static Logger log = Logger.getLogger(KeyWordService.class);
    
    /** If an input word to apelon results in more hits than the
     * blacklistLimit, the word should be blacklisted. Defaults to 20 */
    private int blacklistLimit = 20;

    /** Caches namespaceId to namespace-name and namespace-name to namespaceId resolutions. */
    private Map<String, String> namespaceCache = new HashMap<String, String>();

    /** Map of searchprofiles indexed by profileId */
    private Map<String, SearchProfile> searchProfiles;


    /**
     * Sets the medicalTaxonomyService that is used at the backend to retrieve
     * keywords
     *
     * @param medicalTaxonomyService the medicalTaxonomyService to use
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
     * @param id Identification data, the identificationobject contains both
     * userId and profile. Used to personalize the responce
     * @param requestId The unique id that is to be associated with this request
     * @param document The content to classify
     * @param options Additional options
     * @return A NodeListResponseObject that contains a list of MedicalNodes
     * which are hits in the medicalTaxonomyService for the input content.
     * Check the responseObjects statusCode to see if the operation
     * was successful.
     */
    public NodeListResponseObject getKeywords(Identification id, String requestId, Document document, Options options) {
        if(options == null)
            options = new Options();

        try {
            /** * Determine format ** */
            String format = "";
            // if the document is a file
            if (document instanceof FileDocument) {
                FileDocument fileDocument = (FileDocument) document;
                int index = fileDocument.getFilename().lastIndexOf(".");
                if (index > 0) {
                    format = fileDocument.getFilename().substring(index+1);
                }
                // if the document is a text
            } else if (document instanceof TextDocument) {
                format = "html";
            }
            /** * Strip formatting ** */
            log.debug(MessageFormat.format("{0}:Sending title and content to formatProcessor",requestId));
            log.debug("Format is: " + format);
            FormatProcessor formatProcessor = FormatProcessorFactory.getFormatProcessor(format);
            AnalysisDocument analysisDocument = formatProcessor.process(document);

            /** * Extract keywords ** */
            log.debug(MessageFormat.format("{0}:Getting keywords",requestId));
            String[] keywords = analysisService.extractWords(analysisDocument, options.getInputWords());

            /** * Find medical keywords ** */
            log.debug(MessageFormat.format("{0}:Translating keywords to medicalNodes",requestId));
            List<MedicalNode> allNodes = findMedicalNodes(keywords, id, options.getIncludeSourceIds());
            List<MedicalNode> nodes = new LinkedList<MedicalNode>();
            // create a new list with the X first nodes
            int maxNodes = Math.min(options.getWordsToReturn(),allNodes.size());
            for (int i =0; i< maxNodes; i++){
                nodes.add(allNodes.get(i));
            }

            System.out.println("wtr:" + options.getWordsToReturn() + ", iw:" + options.getInputWords());


            /** * Ensure user has read access to the namespace ** */
            if (!nodes.isEmpty()) {
                MedicalNode firstNode = nodes.get(0);

                if (!hasNamespaceReadAccess(firstNode.getNamespaceId(), id.getProfileId(), requestId)) {

                    /** * Return an NodeListResponseObject with an error code * **/
                    return new NodeListResponseObject(requestId, StatusCode.insufficient_namespace_privileges,
                            "The profile is invalid or does not have read privileges to target namespace");
                }
            }

            /** * Return an NodeListResponseObject with statusCode ok (200) * **/
            return new NodeListResponseObject(requestId, nodes);

        } catch (UnsupportedFormatException ex) {
            log.error(MessageFormat.format("{0}:{1}:The supplied textformat or fileType is not supported",
                    requestId,StatusCode.unsupported_text_format.code()),ex);
            return new NodeListResponseObject(requestId, StatusCode.unsupported_text_format,
                    "The supplied textformat or fileType is not supported");
        } catch (FormattingException ex) {
            log.error(MessageFormat.format("{0}:{1}:The formatting of the file or text failed",
                    requestId,StatusCode.error_formatting_content.code()),ex);
            return new NodeListResponseObject(requestId, StatusCode.error_formatting_content,
                    "The formatting of the file or text failed");
        } catch (ProcessingException ex) {
            log.error(MessageFormat.format("{0}:{1}:The processing (generation of keywords) of the file or text failed",
                    requestId, StatusCode.error_processing_content.code()),ex);
            return new NodeListResponseObject(requestId, StatusCode.error_processing_content,
                    "The processing (generation of keywords) of the file or text failed");
        } catch (KeywordsException ex) {
            log.error(MessageFormat.format("{0}:{1}:The translation of keywords for the file or text failed",
                    requestId + ":" + StatusCode.error_getting_keywords_from_taxonomy.code()),ex);
            return new NodeListResponseObject(requestId, StatusCode.error_getting_keywords_from_taxonomy,
                    "The translation of keywords for the file or text failed");
        }
    }

    /**
     * Helper method to find medical nodes in MedicalTaxonomyService
     * @param keywords The extracted keywords from Analysis service
     * @param Identification Identification for the request
     * @param sourceIds the sourceIds of the concepts to be included
     * @return List of Medical Nodes that were found in the MedicalTaxonomyService. The nodes has been enhanced with userstatus data
     */
    private List<MedicalNode> findMedicalNodes(String[] keywords, Identification identification, Map<Integer, String[]> sourceIds) throws KeywordsException {

        //TODO:Make so this method actualy throws a KeywordsException!

        List<Map<String, List<MedicalNode>>> allNodes = new ArrayList<Map<String, List<MedicalNode>>>();
        Map<String, List<MedicalNode>> nodes = new HashMap<String,List<MedicalNode>>();
        SearchProfile profile = searchProfiles.get(identification.getProfileId());
        if(profile == null) {
            throw new KeywordsException("Specified profile "+identification.getProfileId()+" does not exist");
        }
        List<String> namespaceIds = new ArrayList<String>();
        for(String namespaceName : profile.getSearchableNamespaces()) {
            String namespaceId = getNamespaceIdByName(namespaceName);
            if(namespaceId != null)
                namespaceIds.add(namespaceId);
        }
        nodes = (medicalTaxonomyService.findKeywords(namespaceIds, keywords, sourceIds));
        log.debug(MessageFormat.format(
                "Keywords extracted from AnalysisService: {0}. Nodes from TaxonomyService: {1}",
                keywords.length, nodes.size()));

        /** ********************* */

        // Examine results from TaxonomyService and blacklist
        // words and mark their user status
        Set<Map.Entry<String, List<MedicalNode>>> nodesSet = nodes.entrySet();
        Map.Entry<String, List<MedicalNode>> nodeEntry;
        Set<MedicalNode> addedNodes = new HashSet<MedicalNode>();
        List<MedicalNode> nodesList = new ArrayList<MedicalNode>();

        for (String keyword : keywords) {
            List<MedicalNode> nodeHits = nodes.get(keyword);
            setUserStatus(identification.getUserId(), nodeHits);

            // Add the word to the blacklist database if it has no hits or to many hits.
            log.debug(MessageFormat.format("Hits for keyword {0} is {1}", keyword, nodeHits.size()));
            if (nodeHits.size() == 0 || nodeHits.size() > blacklistLimit) {
                log.debug(MessageFormat.format("Blacklisting word {0}", keyword));
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
     * @param id  The identification of the person who looks for the node
     * @param requestId The unique id that is to be associated with this request
     * @param internalId The internalId of a node from the Medical Taxonomy
     * @return A NodeListResponseObject that contains a list of MedicalNodes
     * containing the requested node
     * Check the responseObjects statusCode to see if the operation
     * was successful.
     */
    public NodeListResponseObject getNodeByInternalId(
            Identification id,
            String requestId,
            String internalId,
            String namespaceName) {

        NodeListResponseObject response = new NodeListResponseObject();
        response.setRequestId(requestId);

        if (internalId == null) {
            log.error(MessageFormat.format("{0}:{1}:No internalId supplied"
                    ,requestId,StatusCode.invalid_parameter.code()));
            return new NodeListResponseObject(requestId,
                    StatusCode.invalid_parameter,
                    "No internalId supplied");
        }
        if( namespaceName == null) {
            log.error(MessageFormat.format("{0}:{1}:No namespace name supplied"
                    ,requestId,StatusCode.invalid_parameter.code()));
            return new NodeListResponseObject(requestId,
                    StatusCode.invalid_parameter,
                    "No namespace name supplied");
        }
        String namespaceId = getNamespaceIdByName(namespaceName);
        if(namespaceId == null) {
            return new NodeListResponseObject(requestId,StatusCode.error_getting_keywords_from_taxonomy, "Invalid namespace name");
        }
        try {
            MedicalNode node = medicalTaxonomyService.getNodeByInternalId(internalId, namespaceId);

            if (node != null) {
                if (hasNamespaceReadAccess(node.getNamespaceId(), id.getProfileId(), requestId)) {
                    List<MedicalNode> list = new ArrayList<MedicalNode>();
                    list.add(node);
                    response.setNodeList(list);
                    response.setStatusCode(StatusCode.ok);

                    log.debug(MessageFormat.format("{0}:{1}:InternalId provided by the user: {2}. The node name from TaxonomyService: {3}",
                            requestId, StatusCode.ok.code(), internalId, node.getName()));

                } else {
                    response.setStatusCode(StatusCode.insufficient_namespace_privileges);
                    response.setErrorMessage("The profile is invalid or does not have read privileges to target namespace");
                    response.setNodeList(new ArrayList<MedicalNode>());
                }

            }
        } catch (NodeNotFoundException ex) {
            log.warn(MessageFormat.format("{0}:{1}:Error retrieving node from medicalTaxonomyService",
                    requestId, StatusCode.error_getting_keywords_from_taxonomy.code()));
            response.setStatusCode(StatusCode.error_getting_keywords_from_taxonomy);
            response.setErrorMessage("Node with id " + internalId + " not found in namespace with id " + namespaceId);
            response.setNodeList(new ArrayList<MedicalNode>());
        }

        return response;
    }



    /**
     * Adds a list of keywords as tagged. Should be called by the GUI to tell
     * the service that keywords suggested by getKeywords has been used to tag
     * the document.
     *
     * @param id  The identification of the person who tags the document
     * @param requestId The unique id that is to be associated with this request
     * @param keywordIds a list of ids that uniquely represents each keyword in the
     * taxonomy.
     * @return a responseobject containg statuscodes that tells if the operation
     * was sucessfull or not
     */
    public ResponseObject tagKeywords(Identification id, String requestId, List<String> keywordIds) {
        userProfileService.addTaggedKeywords(id.getUserId(), keywordIds);
        //TODO: make this responseobject reflect the success or failure of the operation
        return new ResponseObject(requestId);
    }

    /**
     * Adds a list of keywords as bookmarked.
     *
     * @param id The identification of the person who tags the document
     * @param requestId The unique id that is to be associated with this request
     * @param keywordIds a list of ids that uniquely represents each keyword in the
     * taxonomy.
     * @return a responseobject containg statuscodes that tells if the operation
     * was sucessfull or not
     */
    public ResponseObject bookmarkKeywords(Identification id, String requestId, List<String> keywordIds) {
        userProfileService.addBookmarkedKeywords(id.getUserId(), keywordIds);
        //TODO: make this responseobject reflect the success or failiure of the operation
        return new ResponseObject(requestId);
    }

    /**
     * Check if a word is present in the whitelist or blacklist
     * @param id The identification of the person who looks up the word
     * @param requestId The unique id that is to be associated with this request
     * @param word the word to look up
     * @return whiteList, blackList or none depending on where the word was found
     */
    public ListType lookupWord(Identification id, String requestId, String word) {
        //TODO: remove this, the real one is in vocabulary-service!
        if (bwd.getBlacklistedWordByWord(word) != null){
            return ListType.blackList;
        }
        return ListType.none;

    }

    private void setUserStatus(String userId, List<MedicalNode> nodes) {
        for (MedicalNode node : nodes) {
            UserKeyword keyword = userProfileService.getKeywordForUser(userId, node.getInternalId());
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


   /**
     * Check if a used has read access to the given namespace. This routine
     * makes use of the namespace cache and updates it where neccessary.
     *
     * @param namespaceId The id of the namespace used in the request
     * @param profileId The id of the profile used in the request
     * @param requestId The request identifier
     * @return True if the profile has read access to the namespace
     */
    private boolean hasNamespaceReadAccess(String namespaceId, String profileId, String requestId) {
        log.debug("Checking if profile "+profileId+" has access to namespace "+namespaceId);
        String namespace = getNamespaceById(namespaceId, requestId);
        if (namespace != null) {
            SearchProfile profile = searchProfiles.get(profileId);

            if (profile != null) {
                if (profile.getSearchableNamespaces().contains(namespace)) {
                    System.out.println("namespace ok");
                    return true;

                } else {
                    log.warn(MessageFormat.format("{0}:{1}: Submitted profileId {2} does not have read privileges to namespace {3}",
                        requestId, StatusCode.insufficient_namespace_privileges.code(), profileId, namespace));
                }
            } else {
                log.warn(MessageFormat.format("{0}:{1}: Submitted profileId {2} does not match any predefined search profile",
                        requestId, StatusCode.insufficient_namespace_privileges.code(), profileId));
            }
        } else {
            log.warn(MessageFormat.format("{0}:{1}: Error locating namespace for namespaceId {2}",
                        requestId, StatusCode.invalid_parameter.code(), namespaceId));
        }

        return false;
    }


    /**
     * Check if a used has write access to the given namespace. This routine
     * makes use of the namespace cache and updates it where neccessary.
     *
     * @param namespaceId The id of the namespace used in the request
     * @param profileId The id of the profile used in the request
     * @param requestId The request identifier
     * @return True if the profile has write access to the namespace
     */
    private boolean hasNamespaceWriteAccess(String namespaceId, String profileId, String requestId) {
        String namespace = getNamespaceById(namespaceId, requestId);
        if (namespace != null) {
            SearchProfile profile = searchProfiles.get(profileId);

            if (profile != null) {
                if (profile.getWriteableNamespaces().contains(namespace)) {
                    return true;

                } else {
                    log.warn(MessageFormat.format("{0}:{1}: Submitted profileId {2} does not have read privileges to namespace {3}",
                        requestId, StatusCode.insufficient_namespace_privileges.code(), profileId, namespace));
                }
            } else {
                log.warn(MessageFormat.format("{0}:{1}: Submitted profileId {2} does not match any predefined search profile",
                        requestId, StatusCode.insufficient_namespace_privileges.code(), profileId));
            }
        } else {
            log.warn(MessageFormat.format("{0}:{1}: Error locating namespace for namespaceId {2}",
                        requestId, StatusCode.invalid_parameter.code(), namespaceId));
        }

        return false;
    }


    /**
     * A utility routine to get the namespace name from a namespaceId.
     * This routine initially checks the namespaceCache. If no match is
     * found it retrieves the namespace name and updates the namespaceCache.
     *
     * @param namespaceId The id of the namespace to lookup. Must be capable
     * of being converted to an integer.
     * @param requestId The request identifier
     * @return The namespace or null if an error occured
     */
    private String getNamespaceById(String namespaceId, String requestId) {
        String namespace = namespaceCache.get(namespaceId);

        if (namespace != null) {
            log.debug("Namespace with id "+namespaceId+" has name "+namespace);
            return namespace;
        }

        try {
            // Query the MedicaTaxonomyService for the namespace and update the cache
            namespace = medicalTaxonomyService.findNamespaceById(Integer.parseInt(namespaceId));
            namespaceCache.put(namespaceId, namespace);
            return namespace;

        } catch (NumberFormatException ex) {
            log.warn(MessageFormat.format("{0}:{1}:Unable to locate namespace name. NamespaceId {2} cannot be converted to an integer.",
                    requestId, StatusCode.invalid_parameter.code(), namespaceId));
        } catch (Exception ex) {
            log.warn(MessageFormat.format("{0}:{1}:Error retrieving namespace by namespaceId", requestId, StatusCode.error_locating_namespace.code()), ex);
        }

        return null;
    }

        /**
     * A utility routine to get the namespace id from a namespace name.
     * This routine initially checks the namespaceCache. If no match is
     * found it retrieves the namespace id from the taxonomy service and updates the namespaceCache.
     *
     * @param namespaceName The name of the namespace to lookup.
     * @param requestId The request identifier
     * @return The namespace id or null if an error occured
     */
    private String getNamespaceIdByName(String namespaceName) {
        String namespaceId = namespaceCache.get(namespaceName);

        if (namespaceId != null) {
            return namespaceId;
        }

        try {
            // Query the MedicaTaxonomyService for the namespaceId and update the cache
            namespaceId = medicalTaxonomyService.findNamespaceIdByName(namespaceName);
            namespaceCache.put(namespaceName, namespaceId);
            return namespaceId;

        } catch (Exception ex) {
            log.warn(MessageFormat.format("{0}:Error retrieving namespaceId {1} by name", StatusCode.error_locating_namespace.code(), namespaceName), ex);
        }

        return null;
    }


    public void setSearchProfiles(List<SearchProfile> searchProfiles) {
        // Simplify spring configuration by creating list instead of map
        this.searchProfiles = new HashMap<String, SearchProfile>();
        for (SearchProfile profile : searchProfiles) {
            this.searchProfiles.put(profile.getProfileId(), profile);
        }
    }


    /**
     * Sets the blacklistedWordDao
     * @param bwd the blacklistedWordDao
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
     * @param limit  the limit
     */
    public void setBlacklistLimit(int limit) {
        this.blacklistLimit = limit;
    }

    /**
     * Sets the user profile to use in order to personalize the response.
     *
     * @param userProfileService the userProfileService
     */
    public void setUserProfileService(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }
}
