package se.vgregion.metaservice.vocabularyservice.intsvc;

import se.vgregion.metaservice.keywordservice.domain.Identification;
import se.vgregion.metaservice.keywordservice.domain.LookupResponseObject;
import se.vgregion.metaservice.keywordservice.domain.NodeListResponseObject;
import se.vgregion.metaservice.keywordservice.domain.ResponseObject;
import se.vgregion.metaservice.keywordservice.schema.IdentificationSdoHelper;
import se.vgregion.metaservice.keywordservice.schema.NodeSdoHelper;
import se.vgregion.metaservice.keywordservice.schema.ResponseObjectSdoHelper;
import se.vgregion.metaservice.schema.domain.LookupResponseObjectType;
import se.vgregion.metaservice.schema.domain.NodeListResponseObjectType;
import se.vgregion.metaservice.schema.domain.ResponseObjectType;
import se.vgregion.metaservice.vocabularyservice.VocabularyService;
import se.vgregion.metaservice.wsdl.vocabularyservices.AddVocabularyNodeRequest;
import se.vgregion.metaservice.wsdl.vocabularyservices.GetVocabularyRequest;
import se.vgregion.metaservice.wsdl.vocabularyservices.LookupWordRequest;
import se.vgregion.metaservice.wsdl.vocabularyservices.MoveVocabularyNodeRequest;
import se.vgregion.metaservice.wsdl.vocabularyservices.UpdateVocabularyNodeRequest;


public class VocabularyServiceIntServiceImpl implements se.vgregion.metaservice.wsdl.vocabularyservices.VocabularyService {

    private VocabularyService vocabularyService;
    /**
     * Interface to getVocabulary in vocabularyService
     * @param parameters
     * @return
     */
    public NodeListResponseObjectType getVocabulary(GetVocabularyRequest parameters) {
    
        NodeListResponseObject nodeListResponseObject = vocabularyService.getVocabulary(parameters.getRequestId(), parameters.getPath());
        return ResponseObjectSdoHelper.toNodeListRepsonseObjectType(nodeListResponseObject);
    }

    /**
     * Interface to addVocabularyNode in vocabularyService
     * @param parameters
     * @return
     */
    public ResponseObjectType addVocabularyNode(AddVocabularyNodeRequest parameters) {

        ResponseObject responseObject = vocabularyService.addVocabularyNode(
                IdentificationSdoHelper.fromIdentificationType(parameters.getIdentification()),
                parameters.getRequestId(),
                NodeSdoHelper.fromNodeType(parameters.getNode()));

        return ResponseObjectSdoHelper.toRepsonseObjectType(responseObject);
    }

    /**
     * Interface to moveVocabulary in vocabularyService
     * @param parameters
     * @return
     */
    public ResponseObjectType moveVocabularyNode(MoveVocabularyNodeRequest parameters) {

        ResponseObject responseObject = vocabularyService.moveVocabularyNode(
                IdentificationSdoHelper.fromIdentificationType(parameters.getIdentification()),
                parameters.getRequestId(),
                parameters.getNodeId(),
                parameters.getDestParentNodeId());

        return ResponseObjectSdoHelper.toRepsonseObjectType(responseObject);
    }

    /**
     * Interface to updateVocabulary in vocabularyService
     * @param parameters
     * @return
     */
    public ResponseObjectType updateVocabularyNode(UpdateVocabularyNodeRequest parameters) {
        ResponseObject responseObject = vocabularyService.updateVocabularyNode(
                IdentificationSdoHelper.fromIdentificationType(parameters.getIdentification()),
                parameters.getRequestId(),
                NodeSdoHelper.fromNodeType(parameters.getNode()));

        return ResponseObjectSdoHelper.toRepsonseObjectType(responseObject);
    }

    public void dumpDbAsXML() {
        //TODO: Write spec and implement this method;
    }

    /**
     * set the vocaulbularyService
     * @param vocabularyService
     */
    public void setVocabularyService(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }

    public LookupResponseObjectType lookupWord(LookupWordRequest parameters) {
        LookupResponseObject responseObject = vocabularyService.lookupWord(
                IdentificationSdoHelper.fromIdentificationType(parameters.getIdentification()),
                parameters.getRequestId(),
                parameters.getWord(),
                parameters.getUrl());
                
        return ResponseObjectSdoHelper.toLookupRepsonseObjectType(responseObject);
    }
}
