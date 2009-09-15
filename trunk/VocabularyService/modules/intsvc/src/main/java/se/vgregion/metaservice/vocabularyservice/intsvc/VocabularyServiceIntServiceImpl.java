package se.vgregion.metaservice.vocabularyservice.intsvc;

import java.util.List;

import se.vgregion.metaservice.keywordservice.domain.MedicalNode;
import se.vgregion.metaservice.keywordservice.domain.NodeListResponseObject;
import se.vgregion.metaservice.keywordservice.schema.MedicalNodeSdoHelper;
import se.vgregion.metaservice.schema.medicalnode.MedicalNodeListType;
import se.vgregion.metaservice.vocabularyservice.VocabularyService;
import se.vgregion.metaservice.wsdl.vocabularyservices.GetVocabularyRequest;

public class VocabularyServiceIntServiceImpl implements se.vgregion.metaservice.wsdl.vocabularyservices.VocabularyService {

	private VocabularyService vocabularyService;
	
	public MedicalNodeListType getVocabulary(GetVocabularyRequest parameters) {

                //TODO: Fix this according to api

		NodeListResponseObject res = vocabularyService.getVocabulary("x",parameters.getPath());
                List<MedicalNode> nodes = res.getNodeList();

		MedicalNodeListType nodeList = MedicalNodeSdoHelper.toMedicalNodeListType(nodes);
		return nodeList;
	}


        //TODO: add the rest of the methods according to api

	
	public void setVocabularyService(VocabularyService vocabularyService) {
		this.vocabularyService = vocabularyService;
	}

}
