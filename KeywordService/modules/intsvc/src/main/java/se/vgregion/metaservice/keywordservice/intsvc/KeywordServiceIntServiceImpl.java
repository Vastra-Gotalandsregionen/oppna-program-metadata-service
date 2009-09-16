package se.vgregion.metaservice.keywordservice.intsvc;

import org.apache.log4j.Logger;

import se.vgregion.metaservice.keywordservice.KeyWordService;
import se.vgregion.metaservice.keywordservice.domain.NodeListResponseObject;
import se.vgregion.metaservice.keywordservice.domain.ResponseObject;
import se.vgregion.metaservice.schema.medicalnode.MedicalNodeType;
import se.vgregion.metaservice.schema.medicalnode.MedicalNodeListType;
import se.vgregion.metaservice.wsdl.keywordservices.BookmarkKeywordsRequest;
import se.vgregion.metaservice.wsdl.keywordservices.TagKeywordsRequest;
import se.vgregion.metaservice.wsdl.keywordservices.GetKeywordsRequest;
import se.vgregion.metaservice.wsdl.keywordservices.GetNodeByInternalIdRequest;


public class KeywordServiceIntServiceImpl implements
        se.vgregion.metaservice.wsdl.keywordservices.KeywordService {

    KeyWordService keywordService;


    //TODO: The wsdl must be updated in order for this to work!

    /**
     * Interface to the getKeywords method in KeyWordService
     * @param parameters
     * @return
     */
    public NodeListResponseObjectType getKeywords(GetKeywordsRequest parameters) {

        Logger log = Logger.getLogger(KeywordServiceIntServiceImpl.class);
        NodeListResponseObject responseObject = keywordService.getKeywords(
                parameters.getIdentification(),
                parameters.getRequestId(),
                parameters.getDocument(),
                parameters.getOptions());

        return ResponseObjectSdoHelper.toNodeListResponseObjectType(ResponseObject);
    }

    /**
     * Interface to the getNodeByInternalId method in KeyWordService
     * @param parameters
     * @return
     */
    public NodeListResponseObjectType getNodeByInternalId(GetNodeByInternalIdRequest parameters) {

        Logger log = Logger.getLogger(KeywordServiceIntServiceImpl.class);
        NodeListResponseObject responseObject = keywordService.getNodeByInternalId(
                parameters.getIdentification(),
                parameters.getRequestId(),
                parameters.getInternalId());

        return ResponseObjectSdoHelper.toNodeListResponseObjectType(responseObject);
    }

    /**
     * Interface to the tagKeywords method in KeyWordService
     * @param parameters
     */
    public ResponseObjectType tagKeywords(TagKeywordsRequest parameters) {

        ResponseObject responseObject = keywordService.tagKeywords(
                parameters.getIdentification(),
                parameters.getRequestId(),
                parameters.getKeywordIds());

        return ResponseObjectSdoHelper.toResponseObjectType(ResponseObject);

    }

    /**
     * Interface to the bookmarkKeywords method in KeyWordService
     * @param parameters
     */
    public ResponseObjectType bookmarkeKeywords(BookmarkKeywordsRequest parameters) {

        ResponseObject responseObject = keywordService.bookmarkKeywords(
                parameters.getIdentification(),
                parameters.getRequestId(),
                parameters.getKeywordIds());

        return ResponseObjectSdoHelper.toResponseObjectType(ResponseObject);
    }

    public void setKeywordService(KeyWordService keywordService) {
        this.keywordService = keywordService;
    }
}
