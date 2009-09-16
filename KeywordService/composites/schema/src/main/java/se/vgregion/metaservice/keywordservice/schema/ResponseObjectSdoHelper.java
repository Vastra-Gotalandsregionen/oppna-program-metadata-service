package se.vgregion.metaservice.keywordservice.schema;

import se.vgregion.metaservice.keywordservice.domain.NodeListResponseObject;
import se.vgregion.metaservice.keywordservice.domain.ResponseObject;
import se.vgregion.metaservice.keywordservice.domain.ResponseObject.StatusCode;
import se.vgregion.metaservice.schema.domain.ResponseObjectType;
import se.vgregion.metaservice.schema.domain.NodeListResponseObjectType;
import se.vgregion.metaservice.schema.domain.StatusCodeEnum;
import se.vgregion.metaservice.schema.medicalnode.MedicalNodeListType;
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
        responseObjectType.setStatusCode(toStatusCodeEnum(responseObject.getStatusCode()));

        return responseObjectType;
    }

    public static ResponseObject fromRepsonseObjectType(ResponseObjectType responseObjectType) {

        ResponseObject responseObject = new ResponseObject();

        responseObject.setRequestId(responseObjectType.getRequestId());
        responseObject.setErrorMessage(responseObjectType.getErrorMessage());
        responseObject.setStatusCode(fromStatusCodeEnum(responseObjectType.getStatusCode()));

        return responseObject;
    }

    public static NodeListResponseObjectType toNodeListRepsonseObjectType(NodeListResponseObject nodeListResponseObject) {

        NodeListResponseObjectType nodeListResponseObjectType = new NodeListResponseObjectType();

        nodeListResponseObjectType.setRequestId(
                nodeListResponseObject.getRequestId());
        nodeListResponseObjectType.setNodeList(
                MedicalNodeSdoHelper.toMedicalNodeListType(
                nodeListResponseObject.getNodeList()));
        nodeListResponseObjectType.setErrorMessage(
                nodeListResponseObject.getErrorMessage());
        nodeListResponseObjectType.setStatusCode(toStatusCodeEnum(
                nodeListResponseObject.getStatusCode()));

        return nodeListResponseObjectType;
    }

    public static NodeListResponseObject fromNodeListRepsonseObjectType(NodeListResponseObjectType nodeListResponseObjectType) {

        NodeListResponseObject nodeListResponseObject = new NodeListResponseObject();

        nodeListResponseObject.setRequestId(nodeListResponseObjectType.getRequestId());
        nodeListResponseObject.setErrorMessage(nodeListResponseObjectType.getErrorMessage());
        nodeListResponseObject.setStatusCode(fromStatusCodeEnum(nodeListResponseObjectType.getStatusCode()));
        nodeListResponseObject.setNodeList(MedicalNodeSdoHelper.fromMedicalNodeListType(
                nodeListResponseObjectType.getNodeList()));

        return nodeListResponseObject;
    }

    /**
	 * Creates status code from a StatusCodeEnum
	 *
	 * @param statusCodeEnum
	 * @return 
	 */
	public static StatusCode fromStatusCodeEnum(StatusCodeEnum statusCodeEnum) {
		if (statusCodeEnum == null)
			return null;
        StatusCode statusCode = StatusCode.valueOf(statusCodeEnum.name());
		return statusCode;
	}

	/**
	 * Builds a StatusCodeEnum from a status code
	 *
	 * @param statusCode
	 * @return
	 */
	public static StatusCodeEnum toStatusCodeEnum(StatusCode statusCode) {
		if (statusCode == null)
			return null;
		StatusCodeEnum statusCodeEnum = StatusCodeEnum.fromValue(statusCode.code());
		return statusCodeEnum;
	}
}
