package se.vgregion.metaservice.keywordservice.schema;

import se.vgregion.metaservice.keywordservice.domain.NodeListResponseObject;
import se.vgregion.metaservice.keywordservice.domain.ResponseObject;
import se.vgregion.metaservice.schema.responseObject.ResponseObjectType;
import se.vgregion.metaservice.schema.responseObject.NodeListResponseObjectType;

/**
 * This class is a helperclass that converts responseObjects between SDO and POJO
 * @author sture.svensson
 */

public class ResponseObjectSdoHelper {

    ResponseObjectSdoHelper() {
    }

    public static ResponseObjectType toRepsonseObjectType(ResponseObject responseObject) {

        ResponseObjectType responseObjectType = new ResponseObjectType();

        responseObjectType.setRequestId(responseObject.getRequestId());
        responseObjectType.setErrorMessage(responseObject.getErrorMessage());
        responseObjectType.setStatusCode(responseObject.getStatusCode());

        return responseObjectType;
    }

    public static ResponseObject fromRepsonseObjectType(ResponseObjectType responseObjectType) {

        ResponseObject responseObject = new ResponseObject();

        responseObject.setRequestId(responseObjectType.getRequestId());
        responseObject.setErrorMessage(responseObjectType.getErrorMessage());
        responseObject.setStatusCode(responseObjectType.getStatusCode());

        return responseObject;
    }

    public static NodeListResponseObjectType toNodeRepsonseObjectType(NodeListResponseObject nodeListResponseObject) {

        NodeListResponseObjectType nodeListResponseObjectType = new NodeListResponseObjectType();

        nodeListResponseObjectType.setRequestId(
                nodeListResponseObject.getRequestId());
        nodeListResponseObjectType.setNodeList(
                MedicalNodeSdoHelper.toMedicalNodeListType(
                nodeListResponseObject.getNodeList()));
        nodeListResponseObjectType.setErrorMessage(
                nodeListResponseObject.getErrorMessage());
        nodeListResponseObjectType.setStatusCode(
                nodeListResponseObject.getStatusCode());

        return nodeListResponseObjectType;
    }

    public static NodeListResponseObject fromNodeRepsonseObjectType(NodeListResponseObjectType nodeListResponseObjectType) {

        NodeListResponseObject nodeListResponseObject = new NodeListResponseObject();

        nodeListResponseObject.setRequestId(nodeListResponseObjectType.getRequestId());
        nodeListResponseObject.setErrorMessage(nodeListResponseObjectType.getErrorMessage());
        nodeListResponseObject.setStatusCode(nodeListResponseObjectType.getStatusCode());
        nodeListResponseObject.setNodeList(MedicalNodeSdoHelper.fromMedicalNodeType(
                nodeListResponseObjectType.getNodeList()));

        return nodeListResponseObject;
    }
}
