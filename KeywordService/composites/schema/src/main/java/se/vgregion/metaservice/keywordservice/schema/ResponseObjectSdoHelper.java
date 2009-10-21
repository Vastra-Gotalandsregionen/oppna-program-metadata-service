package se.vgregion.metaservice.keywordservice.schema;

import se.vgregion.metaservice.keywordservice.domain.LastChangeResponseObject;
import se.vgregion.metaservice.keywordservice.domain.LookupResponseObject;
import se.vgregion.metaservice.keywordservice.domain.LookupResponseObject.ListType;
import se.vgregion.metaservice.keywordservice.domain.NodeListResponseObject;
import se.vgregion.metaservice.keywordservice.domain.ResponseObject;
import se.vgregion.metaservice.keywordservice.domain.ResponseObject.StatusCode;
import se.vgregion.metaservice.schema.domain.LastChangeResponseObjectType;
import se.vgregion.metaservice.schema.domain.ResponseObjectType;
import se.vgregion.metaservice.schema.domain.LookupResponseObjectType;
import se.vgregion.metaservice.schema.domain.NodeListResponseObjectType;
import se.vgregion.metaservice.schema.domain.ListTypeEnum;
import se.vgregion.metaservice.schema.domain.StatusCodeEnum;

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

    public static LastChangeResponseObjectType toLastChangeRepsonseObjectType(LastChangeResponseObject responseObject) {

        LastChangeResponseObjectType responseObjectType = new LastChangeResponseObjectType();

        responseObjectType.setRequestId(responseObject.getRequestId());
        responseObjectType.setErrorMessage(responseObject.getErrorMessage());
        responseObjectType.setStatusCode(toStatusCodeEnum(responseObject.getStatusCode()));
        responseObjectType.setLastChange(responseObject.getLastChange());

        return responseObjectType;
    }

    public static LastChangeResponseObject fromLastChangeRepsonseObjectType(LastChangeResponseObjectType responseObjectType) {

        LastChangeResponseObject responseObject = new LastChangeResponseObject();

        responseObject.setRequestId(responseObjectType.getRequestId());
        responseObject.setErrorMessage(responseObjectType.getErrorMessage());
        responseObject.setStatusCode(fromStatusCodeEnum(responseObjectType.getStatusCode()));
        responseObject.setLastChange(responseObjectType.getLastChange());

        return responseObject;
    }

    public static NodeListResponseObjectType toNodeListRepsonseObjectType(NodeListResponseObject nodeListResponseObject) {

        NodeListResponseObjectType nodeListResponseObjectType = new NodeListResponseObjectType();

        nodeListResponseObjectType.setRequestId(
                nodeListResponseObject.getRequestId());
        nodeListResponseObjectType.setNodeList(
                NodeSdoHelper.toNodeListType(
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
        nodeListResponseObject.setNodeList(NodeSdoHelper.fromNodeListType(
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
        if (statusCodeEnum == null) {
            return null;
        }
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
        if (statusCode == null) {
            return null;
        }
        StatusCodeEnum statusCodeEnum = StatusCodeEnum.fromValue(statusCode.code());
        return statusCodeEnum;
    }

    public static LookupResponseObjectType toLookupRepsonseObjectType(LookupResponseObject lookupResponseObject) {

        LookupResponseObjectType lookupResponseObjectType = new LookupResponseObjectType();
        lookupResponseObjectType.setRequestId(lookupResponseObject.getRequestId());
        lookupResponseObjectType.setErrorMessage(lookupResponseObject.getErrorMessage());
        lookupResponseObjectType.setStatusCode(toStatusCodeEnum(lookupResponseObject.getStatusCode()));
        lookupResponseObjectType.setListType(toListTypeEnum(lookupResponseObject.getListType()));

        return lookupResponseObjectType;
    }

    public static LookupResponseObject fromLookupRepsonseObjectType(LookupResponseObjectType lookupResponseObjectType) {

        LookupResponseObject lookupResponseObject = new LookupResponseObject();

        lookupResponseObject.setRequestId(lookupResponseObjectType.getRequestId());
        lookupResponseObject.setErrorMessage(lookupResponseObjectType.getErrorMessage());
        lookupResponseObject.setStatusCode(fromStatusCodeEnum(lookupResponseObjectType.getStatusCode()));
        lookupResponseObject.setListType(fromListTypeEnum(
                lookupResponseObjectType.getListType()));

        return lookupResponseObject;
    }

    /**
     * Creates status code from a StatusCodeEnum
     *
     * @param statusCodeEnum
     * @return
     */
    public static ListType fromListTypeEnum(ListTypeEnum listTypeEnum) {
        if (listTypeEnum == null) {
            return null;
        }
        ListType listType = ListType.valueOf(listTypeEnum.name());
        return listType;
    }

    /**
     * Builds a ListTypeEnum from a list type
     *
     * @param listType
     * @return
     */
    public static ListTypeEnum toListTypeEnum(ListType listType) {
        if (listType == null) {
            return null;
        }
        ListTypeEnum listTypeEnum = ListTypeEnum.fromValue(listType.name());
        return listTypeEnum;
    }
}
