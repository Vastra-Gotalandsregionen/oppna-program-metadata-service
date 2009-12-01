package se.vgregion.metaservice.vocabularyservice.intsvc;

import se.vgregion.metaservice.keywordservice.domain.LastChangeResponseObject;
import se.vgregion.metaservice.keywordservice.domain.LookupResponseObject;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode;
import se.vgregion.metaservice.keywordservice.domain.NodeListResponseObject;
import se.vgregion.metaservice.keywordservice.domain.ResponseObject;
import se.vgregion.metaservice.keywordservice.domain.XMLResponseObject;
import se.vgregion.metaservice.keywordservice.schema.IdentificationSdoHelper;
import se.vgregion.metaservice.keywordservice.schema.NodeSdoHelper;
import se.vgregion.metaservice.keywordservice.schema.OptionsSdoHelper;
import se.vgregion.metaservice.keywordservice.schema.ResponseObjectSdoHelper;
import se.vgregion.metaservice.keywordservice.schema.XMLResponseObjectSdoHelper;
import se.vgregion.metaservice.schema.domain.LastChangeResponseObjectType;
import se.vgregion.metaservice.schema.domain.LookupResponseObjectType;
import se.vgregion.metaservice.schema.domain.NodeListResponseObjectType;
import se.vgregion.metaservice.schema.domain.ResponseObjectType;
import se.vgregion.metaservice.schema.domain.XMLResponseObjectType;
import se.vgregion.metaservice.vocabularyservice.VocabularyService;
import se.vgregion.metaservice.wsdl.vocabularyservices.AddVocabularyNodeRequest;
import se.vgregion.metaservice.wsdl.vocabularyservices.GetNamespaceXmlRequest;
import se.vgregion.metaservice.wsdl.vocabularyservices.GetVocabularyRequest;
import se.vgregion.metaservice.wsdl.vocabularyservices.LastChangeRequest;
import se.vgregion.metaservice.wsdl.vocabularyservices.LookupWordRequest;
import se.vgregion.metaservice.wsdl.vocabularyservices.MoveVocabularyNodeRequest;
import se.vgregion.metaservice.wsdl.vocabularyservices.UpdateVocabularyNodeRequest;
import se.vgregion.metaservice.wsdl.vocabularyservices.FindNodesByNameRequest;

public class VocabularyServiceIntServiceImpl implements se.vgregion.metaservice.wsdl.vocabularyservices.VocabularyService {

    private VocabularyService vocabularyService;

    /**
     * Interface to getVocabulary in vocabularyService
     * @param parameters
     * @return
     */
    public LastChangeResponseObjectType getLastChange(LastChangeRequest parameters) {

        LastChangeResponseObject lastChangeResponseObject = vocabularyService.getLastChange(IdentificationSdoHelper.fromIdentificationType(parameters.getIdentification()),
                parameters.getRequestId(), parameters.getNamespace());
        return ResponseObjectSdoHelper.toLastChangeRepsonseObjectType(lastChangeResponseObject);
    }

    /**
     * Interface to getVocabulary in vocabularyService
     * @param parameters
     * @return
     */
    public NodeListResponseObjectType getVocabulary(GetVocabularyRequest parameters) {

        NodeListResponseObject nodeListResponseObject = vocabularyService.getVocabulary(
                parameters.getRequestId(),
                parameters.getPath(),
                OptionsSdoHelper.fromOptionsType(parameters.getOptions()));
        return ResponseObjectSdoHelper.toNodeListRepsonseObjectType(nodeListResponseObject);
    }

    /**
     * Interface to getVocabulary in vocabularyService
     * @param parameters
     * @return
     */
    public NodeListResponseObjectType findNodesByName(FindNodesByNameRequest parameters) {

        NodeListResponseObject nodeListResponseObject = vocabularyService.findNodesByName(
                IdentificationSdoHelper.fromIdentificationType(parameters.getIdentification()),
                parameters.getNameSpaceName(),parameters.getName(),parameters.getRequestId(),
                OptionsSdoHelper.fromOptionsType(parameters.getOptions()));

        return ResponseObjectSdoHelper.toNodeListRepsonseObjectType(nodeListResponseObject);
    }

    /**
     * Interface to addVocabularyNode in vocabularyService
     * @param parameters
     * @return
     */
    public ResponseObjectType addVocabularyNode(AddVocabularyNodeRequest parameters) {

        MedicalNode node = NodeSdoHelper.fromNodeType(parameters.getNode());

        ResponseObject responseObject = vocabularyService.addVocabularyNode(
                IdentificationSdoHelper.fromIdentificationType(parameters.getIdentification()),
                parameters.getRequestId(),
                node);

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
                NodeSdoHelper.fromNodeType(parameters.getNode()),
                NodeSdoHelper.fromNodeType(parameters.getDestParentNode()));

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

    /**
     * Interface to return a namespace as XML
     * @param parameters
     * @return
     */
    public XMLResponseObjectType getNamespaceXml(GetNamespaceXmlRequest parameters) {
        XMLResponseObject responseObject = vocabularyService.getNamespaceXml(
                IdentificationSdoHelper.fromIdentificationType(parameters.getIdentification()),
                parameters.getRequestId(),
                parameters.getNamespace());

        return XMLResponseObjectSdoHelper.toXMLRepsonseObjectType(responseObject);
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
                OptionsSdoHelper.fromOptionsType(parameters.getOptions()));

        return ResponseObjectSdoHelper.toLookupRepsonseObjectType(responseObject);
    }
}
