package se.vgregion.metaservice.keywordservice.schema;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import se.vgregion.metaservice.keywordservice.domain.MedicalNode;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode.UserStatus;
import se.vgregion.metaservice.schema.medicalnode.MedicalNodeListType;
import se.vgregion.metaservice.schema.medicalnode.MedicalNodeType;
import se.vgregion.metaservice.schema.medicalnode.SynonymsListType;
import se.vgregion.metaservice.schema.medicalnode.UserStatusEnum;
import se.vgregion.metaservice.schema.medicalnode.UserStatusListType;

/**
 * HelperClass for conversion between SDO representation of MedicalNode and the
 * common type POJO counterpart
 * 
 * @author tobias
 * 
 */
public abstract class MedicalNodeSdoHelper {

	public static Logger log = Logger.getLogger(MedicalNodeSdoHelper.class);

	/**
	 * Prevent instantiation by sub-classing
	 */
	MedicalNodeSdoHelper() {
	}

	/**
	 * Builds an SDO MedicalNodeType from a MedicalNode
	 * 
	 * @param node
	 * @return MedicalNodeType
	 */
	public static MedicalNodeType toMedicalNodeType(MedicalNode node) {
		MedicalNodeType nodeSDO = new MedicalNodeType();
		nodeSDO.setInternalId(node.getInternalId());
		nodeSDO.setName(node.getName());
		nodeSDO.setNamespaceId(node.getNamespaceId());
		nodeSDO.setSourceId(node.getSourceId());
		nodeSDO.setSynonyms(toSynonymsListType(node.getSynonyms()));
		nodeSDO.setParents(toMedicalNodeListType(node.getParents()));
		nodeSDO.setUserStatus(toUserStatusListType(node.getUserStatus()));
		nodeSDO.setHasChildren(node.getHasChildren());
		return nodeSDO;
	}

	/**
	 * Builds a MedicalNode from an SDO counterpart
	 * 
	 * @param sdoNode
	 * @return MedicalNode
	 */
	public static MedicalNode fromMedicalNodeType(MedicalNodeType sdoNode) {
		MedicalNode newNode = new MedicalNode();
		newNode.setInternalId(sdoNode.getInternalId());
		newNode.setName(sdoNode.getName());
		newNode.setNamespaceId(sdoNode.getNamespaceId());
		newNode.setSourceId(sdoNode.getSourceId());
		newNode.setParents(fromMedicalNodeListType(sdoNode.getParents()));
		newNode.setUserStatus(fromUserStatusListType(sdoNode.getUserStatus()));
		newNode.setHasChildren(sdoNode.isHasChildren());	
		return newNode;
	}

	/**
	 * Builds a list of medical nodes from a MedicalNodeList
	 * 
	 * @param list
	 * @return
	 */
	public static List<MedicalNode> fromMedicalNodeListType(
			MedicalNodeListType list) {
		ArrayList<MedicalNode> returnType = new ArrayList<MedicalNode>();
		for (MedicalNodeType sdo : list.getMedicalNode()) {
			MedicalNode node = MedicalNodeSdoHelper.fromMedicalNodeType(sdo);
			returnType.add(node);
		}
		return returnType;
	}

	/**
	 * Builds a MedicalNodeListType from a list of list of address entries
	 * 
	 * @param medicalNodeList
	 * @return
	 */
	public static MedicalNodeListType toMedicalNodeListType(
			List<MedicalNode> medicalNodeList) {
		MedicalNodeListType returnType = new MedicalNodeListType();
		for (MedicalNode node : medicalNodeList) {
			MedicalNodeType nodeSDO = MedicalNodeSdoHelper
					.toMedicalNodeType(node);
			returnType.getMedicalNode().add(nodeSDO);
		}
		return returnType;
	}

	/**
	 * Builds a list of user status from a UserStatusList
	 * 
	 * @param list
	 * @return
	 */
	public static List<UserStatus> fromUserStatusListType(
			UserStatusListType list) {
		List<UserStatus> returnType = new ArrayList<UserStatus>();
		for (UserStatusEnum use : list.getUserStatus()) {
			UserStatus us = UserStatus.valueOf(use.value());
			returnType.add(us);
		}

		return returnType;
	}

	/**
	 * Builds a UserStatusListType from a list of user status
	 * 
	 * @param medicalNodeList
	 * @return
	 */
	public static UserStatusListType toUserStatusListType(
			List<UserStatus> userStatusList) {
		UserStatusListType returnType = new UserStatusListType();
		for (UserStatus status : userStatusList) {
			UserStatusEnum use = UserStatusEnum.fromValue(status.value());
			returnType.getUserStatus().add(use);
		}
		return returnType;
	}
	
	/**
	 * Builds a list of user status from a UserStatusList
	 * 
	 * @param list
	 * @return
	 */
	public static List<UserStatus> fromUserStatusEnumList(
			List<UserStatusEnum> list) {
		List<UserStatus> returnType = new ArrayList<UserStatus>();
		for (UserStatusEnum use : list) {
			UserStatus us = UserStatus.valueOf(use.value());
			returnType.add(us);
		}

		return returnType;
	}
	
	/**
	 * Builds a UserStatusListType from a list of user status
	 * 
	 * @param medicalNodeList
	 * @return
	 */
	public static List<UserStatusEnum> toUserStatusEnumList(
			List<UserStatus> userStatusList) {
		List<UserStatusEnum> returnType = new ArrayList<UserStatusEnum>();
		for (UserStatus status : userStatusList) {
			UserStatusEnum use = UserStatusEnum.fromValue(status.value());
			returnType.add(use);
		}
		return returnType;
	}

	/**
	 * Creates user status from a UserStatusEnum
	 * 
	 * @param use
	 * @return
	 */
	public static UserStatus fromUserStatusEnum(UserStatusEnum use) {
		if (use == null)
			return null;
		UserStatus us = UserStatus.valueOf(use.value());
		return us;
	}

	/**
	 * Builds a UserStatusEnum from a user status
	 * 
	 * @param userStatus
	 * @return
	 */
	public static UserStatusEnum toUserStatusEnum(UserStatus userStatus) {
		if (userStatus == null)
			return null;
		UserStatusEnum use = UserStatusEnum.fromValue(userStatus.value());
		return use;
	}
	
	public static List<String> fromSynonymsListType(SynonymsListType list ) {
		return list.getSynonym();
	}
	
	public static SynonymsListType toSynonymsListType(List<String> synonymsList) {
		SynonymsListType list = new SynonymsListType();
		list.getSynonym().addAll(synonymsList);
		
		return list;
	}

}
