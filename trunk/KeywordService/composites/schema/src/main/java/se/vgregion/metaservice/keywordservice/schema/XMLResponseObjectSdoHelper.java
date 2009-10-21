package se.vgregion.metaservice.keywordservice.schema;

import se.vgregion.metaservice.keywordservice.domain.XMLResponseObject;
import se.vgregion.metaservice.schema.domain.XMLResponseObjectType;

/**
 * Helper class for SDO/DTO conversion
 * @author johan.sjoberg
 */
public class XMLResponseObjectSdoHelper {

    XMLResponseObjectSdoHelper() {
    }

    public static XMLResponseObjectType toXMLRepsonseObjectType(XMLResponseObject responseObject) {

        XMLResponseObjectType responseObjectType = new XMLResponseObjectType();

        responseObjectType.setRequestId(responseObject.getRequestId());
        responseObjectType.setXml(responseObject.getXml());
        responseObjectType.setErrorMessage(responseObject.getErrorMessage());
        responseObjectType.setStatusCode(ResponseObjectSdoHelper.toStatusCodeEnum(responseObject.getStatusCode()));
        responseObjectType.setTime(responseObject.getTime());
        return responseObjectType;
    }

    public static XMLResponseObject fromXMLRepsonseObjectType(XMLResponseObjectType responseObjectType) {

        XMLResponseObject responseObject = new XMLResponseObject();

        responseObject.setXml(responseObjectType.getXml());
        responseObject.setRequestId(responseObjectType.getRequestId());
        responseObject.setErrorMessage(responseObjectType.getErrorMessage());
        responseObject.setStatusCode(ResponseObjectSdoHelper.fromStatusCodeEnum(responseObjectType.getStatusCode()));
        responseObject.setTime(responseObjectType.getTime());

        return responseObject;
    }



}
