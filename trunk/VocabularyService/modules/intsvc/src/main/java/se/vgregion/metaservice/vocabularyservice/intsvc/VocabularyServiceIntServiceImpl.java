package se.vgregion.metaservice.vocabularyservice.intsvc;

import se.vgregion.metaservice.keywordservice.domain.NodeListResponseObject;
import se.vgregion.metaservice.keywordservice.domain.ResponseObject;
import se.vgregion.metaservice.keywordservice.schema.ResponseObjectSdoHelper;
import se.vgregion.metaservice.schema.medicalnode.MedicalNodeListType;
import se.vgregion.metaservice.vocabularyservice.VocabularyService;
import se.vgregion.metaservice.wsdl.vocabularyservices.GetVocabularyRequest;

public class VocabularyServiceIntServiceImpl implements se.vgregion.metaservice.wsdl.vocabularyservices.VocabularyService {

    private VocabularyService vocabularyService;

    //TODO: The wsdl must be updated in order for this to work!
    /**
     * Interface to getVocabulary in vocabularyService
     * @param parameters
     * @return
     */
    public MedicalNodeListType getVocabulary(GetVocabularyRequest parameters) {

        NodeListResponseObject nodeListResponseObject = vocabularyService.getVocabulary(parameters.getRequestId(), parameters.getPath());

        return ResponseObjectSdoHelper.toNodeListResponseObjectType(nodeListResponseObject);
    }

    /**
     * Interface to addVocabularyNode in vocabularyService
     * @param parameters
     * @return
     */
    public ResponseObjectType addVocabularyNode(AddVocabularyRequest parameters) {

        ResponseObject responseObject = vocabularyService.addVocabularyNode(
                parameters.getIdentification(),
                parameters.getRequestId(),
                parameters.getMedicalNode());

        return ResponseObjectSdoHelper.toResponseObjectType(responseObject);
    }

    /**
     * Interface to moveVocabulary in vocabularyService
     * @param parameters
     * @return
     */
    public ResponseObjectType moveVocabularyNode(MoveVocabularyRequest parameters) {

        ResponseObject responseObject = vocabularyService.moveVocabularyNode(
                parameters.getIdentification(),
                parameters.getRequestId(),
                parameters.getNodeId(),
                parameters.getDestNodeId());

        return ResponseObjectSdoHelper.toResponseObjectType(responseObject);
    }

    /**
     * Interface to updateVocabulary in vocabularyService
     * @param parameters
     * @return
     */
    public ResponseObjectType updateVocabularyNode(UpdateVocabularyRequest parameters) {
        i ResponseObject responseObject = vocabularyService.updateVocabularyNode(
                parameters.getIdentification(),
                parameters.getRequestId(),
                parameters.getMedicalNode());

        return ResponseObjectSdoHelper.toResponseObjectType(responseObject);
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
}
