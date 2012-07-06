package se.vgregion.metaservice.keywordservice.intsvc;

import se.vgregion.metaservice.keywordservice.KeyWordService;
import se.vgregion.metaservice.keywordservice.domain.NodeListResponseObject;
import se.vgregion.metaservice.keywordservice.domain.ResponseObject;
import se.vgregion.metaservice.keywordservice.schema.DocumentSdoHelper;
import se.vgregion.metaservice.keywordservice.schema.ResponseObjectSdoHelper;
import se.vgregion.metaservice.keywordservice.schema.IdentificationSdoHelper;
import se.vgregion.metaservice.keywordservice.schema.OptionsSdoHelper;
import se.vgregion.metaservice.schema.domain.ResponseObjectType;
import se.vgregion.metaservice.schema.domain.NodeListResponseObjectType;
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

        NodeListResponseObject responseObject = keywordService.getKeywords(
                IdentificationSdoHelper.fromIdentificationType(parameters.getIdentification()),
                parameters.getRequestId(),
                DocumentSdoHelper.fromDocumentType(parameters.getDocument()),
                OptionsSdoHelper.fromOptionsType(parameters.getOptions()));

        return ResponseObjectSdoHelper.toNodeListRepsonseObjectType(responseObject);
    }

    /**
     * Interface to the getNodeByInternalId method in KeyWordService
     * @param parameters
     * @return
     */
    public NodeListResponseObjectType getNodeByInternalId(GetNodeByInternalIdRequest parameters) {

        NodeListResponseObject responseObject = keywordService.getNodeByInternalId(
                IdentificationSdoHelper.fromIdentificationType(parameters.getIdentification()),
                parameters.getRequestId(),
                parameters.getInternalId(),
                parameters.getNamespaceName());

        return ResponseObjectSdoHelper.toNodeListRepsonseObjectType(responseObject);
    }

    /**
     * Interface to the tagKeywords method in KeyWordService
     * @param parameters
     */
    public ResponseObjectType tagKeywords(TagKeywordsRequest parameters) {

        ResponseObject responseObject = keywordService.tagKeywords(
                IdentificationSdoHelper.fromIdentificationType(parameters.getIdentification()),
                parameters.getRequestId(),
                parameters.getKeywordIds().getKeywordId());

        return ResponseObjectSdoHelper.toRepsonseObjectType(responseObject);

    }

    /**
     * Interface to the bookmarkKeywords method in KeyWordService
     * @param parameters
     */
    public ResponseObjectType bookmarkKeywords(BookmarkKeywordsRequest parameters) {

        ResponseObject responseObject = keywordService.bookmarkKeywords(
                IdentificationSdoHelper.fromIdentificationType(parameters.getIdentification()),
                parameters.getRequestId(),
                parameters.getKeywordIds().getKeywordIds());

        return ResponseObjectSdoHelper.toRepsonseObjectType(responseObject);
    }

    public void setKeywordService(KeyWordService keywordService) {
        this.keywordService = keywordService;
    }
}
