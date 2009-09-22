package se.vgregion.metaservice.keywordservice.schema;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import se.vgregion.metaservice.keywordservice.domain.MedicalNode;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode.UserStatus;
import se.vgregion.metaservice.schema.domain.NodeListType;
import se.vgregion.metaservice.schema.domain.NodeType;
import se.vgregion.metaservice.schema.domain.SynonymsListType;
import se.vgregion.metaservice.schema.domain.UserStatusEnum;
import se.vgregion.metaservice.schema.domain.UserStatusListType;

/**
 * HelperClass for conversion between SDO representation of Node and the
 * common type POJO counterpart
 * 
 * @author tobias
 * 
 */
public abstract class NodeSdoHelper {

    public static Logger log = Logger.getLogger(NodeSdoHelper.class);

    /**
     * Prevent instantiation by sub-classing
     */
    NodeSdoHelper() {
    }

    /**
     * Builds an SDO NodeType from a Node
     *
     * @param node
     * @return NodeType
     */
    public static NodeType toNodeType(MedicalNode node) {
        NodeType nodeSDO = new NodeType();
        nodeSDO.setInternalId(node.getInternalId());
        nodeSDO.setName(node.getName());
        nodeSDO.setNamespaceId(node.getNamespaceId());
        nodeSDO.setSourceId(node.getSourceId());
        nodeSDO.setSynonyms(toSynonymsListType(node.getSynonyms()));
        nodeSDO.setParents(toNodeListType(node.getParents()));
        nodeSDO.setUserStatus(toUserStatusListType(node.getUserStatus()));
        nodeSDO.setHasChildren(node.getHasChildren());
        return nodeSDO;
    }

    /**
     * Builds a Node from an SDO counterpart
     *
     * @param sdoNode
     * @return Node
     */
    public static MedicalNode fromNodeType(NodeType sdoNode) {
        MedicalNode newNode = new MedicalNode();
        newNode.setInternalId(sdoNode.getInternalId());
        newNode.setName(sdoNode.getName());
        newNode.setNamespaceId(sdoNode.getNamespaceId());
        newNode.setSourceId(sdoNode.getSourceId());
        newNode.setParents(fromNodeListType(sdoNode.getParents()));
        newNode.setUserStatus(fromUserStatusListType(sdoNode.getUserStatus()));
        newNode.setHasChildren(sdoNode.isHasChildren());
        return newNode;
    }

    /**
     * Builds a list of medical nodes from a NodeList
     *
     * @param list
     * @return
     */
    public static List<MedicalNode> fromNodeListType(
            NodeListType list) {
        ArrayList<MedicalNode> returnType = new ArrayList<MedicalNode>();
        for (NodeType sdo : list.getNode()) {
            MedicalNode node = NodeSdoHelper.fromNodeType(sdo);
            returnType.add(node);
        }
        return returnType;
    }

    /**
     * Builds a NodeListType from a list of list of address entries
     *
     * @param NodeList
     * @return
     */
    public static NodeListType toNodeListType(
            List<MedicalNode> nodeList) {
        NodeListType returnType = new NodeListType();
        if (nodeList != null) {
            for (MedicalNode node : nodeList) {
                NodeType nodeSDO = NodeSdoHelper.toNodeType(node);
                returnType.getNode().add(nodeSDO);
            }
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
     * @param NodeList
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
     * @param NodeList
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
        if (use == null) {
            return null;
        }
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
        if (userStatus == null) {
            return null;
        }
        UserStatusEnum use = UserStatusEnum.fromValue(userStatus.value());
        return use;
    }

    public static List<String> fromSynonymsListType(SynonymsListType list) {
        return list.getSynonym();
    }

    public static SynonymsListType toSynonymsListType(List<String> synonymsList) {
        SynonymsListType list = new SynonymsListType();
        list.getSynonym().addAll(synonymsList);

        return list;
    }
}
