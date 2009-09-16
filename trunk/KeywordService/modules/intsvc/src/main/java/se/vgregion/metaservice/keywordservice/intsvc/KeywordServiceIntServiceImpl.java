package se.vgregion.metaservice.keywordservice.intsvc;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import se.vgregion.metaservice.keywordservice.KeyWordService;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode;
import se.vgregion.metaservice.keywordservice.domain.Options;
import se.vgregion.metaservice.keywordservice.domain.Identification;
import se.vgregion.metaservice.keywordservice.domain.NodeListResponseObject;
import se.vgregion.metaservice.keywordservice.domain.document.TextDocument;
import se.vgregion.metaservice.keywordservice.exception.UnsupportedFormatException;
import se.vgregion.metaservice.keywordservice.schema.MedicalNodeSdoHelper;
import se.vgregion.metaservice.schema.medicalnode.MedicalNodeType;
import se.vgregion.metaservice.schema.medicalnode.MedicalNodeListType;
import se.vgregion.metaservice.wsdl.keywordservices.BookmarkedKeywordsRequest;
import se.vgregion.metaservice.wsdl.keywordservices.TaggedKeywordsRequest;
import se.vgregion.metaservice.wsdl.keywordservices.GetKeywordsRequest;
import se.vgregion.metaservice.wsdl.keywordservices.GetNodeByInternalIdRequest;


//TODO: Fix all this according to api

public class KeywordServiceIntServiceImpl implements
		se.vgregion.metaservice.wsdl.keywordservices.KeywordService {

	KeyWordService keywordService;

	public MedicalNodeListType getKeywords(GetKeywordsRequest parameters) {

		Logger log = Logger.getLogger(KeywordServiceIntServiceImpl.class);


		List<MedicalNode> keywordResult = new ArrayList<MedicalNode>();

                NodeListResponseObject res = keywordService.getKeywords(new Identification("x","x"),
                        "x", new TextDocument(), new Options());

                keywordResult = res.getNodeList();

                /**
		try {
			keywordResult = keywordService.getKeywords(parameters.getContent(),
					parameters.getTitle(), parameters.getUserId(), parameters
							.getFormat(), new Metadata[] {}, parameters
							.getExtractedWordsLimit(), parameters.getIncludeSourceIds());
		} catch (UnsupportedFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
                 * */



		List<MedicalNode> keywords = new ArrayList<MedicalNode>(keywordResult);

		MedicalNodeListType retval = MedicalNodeSdoHelper
				.toMedicalNodeListType(keywords);



		return retval;
	}

	public void setKeywordService(KeyWordService keywordService) {
		this.keywordService = keywordService;
	}

	public MedicalNodeType getNodeByInternalId(GetNodeByInternalIdRequest parameters) {
		
		Logger log = Logger.getLogger(KeywordServiceIntServiceImpl.class);

		MedicalNode nodeResult = new MedicalNode();

		try {
			nodeResult = keywordService.findMedicalNodeByInternalId(parameters.getInternalId());
		} catch (UnsupportedFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MedicalNodeType retval = MedicalNodeSdoHelper
				.toMedicalNodeType(nodeResult);

		return retval;
	}
	
	public void taggedKeywords(TaggedKeywordsRequest parameters) {
		List<String> userCodes = parameters.getKeywordCodes().getKeywordCode();
		String userId = parameters.getUserId();
		keywordService.addTaggedKeywords(new Identification(userId,"x"),"x", userCodes);

	}

	public void bookmarkedKeywords(BookmarkedKeywordsRequest parameters) {
		System.out.println("Bookmarking keyword ");
		List<String> userCodes = parameters.getKeywordCodes().getKeywordCode();
		String userId = parameters.getUserId();
		keywordService.addBookmarkedKeywords(new Identification(userId,"x"),"x", userCodes);
		
	}

}
