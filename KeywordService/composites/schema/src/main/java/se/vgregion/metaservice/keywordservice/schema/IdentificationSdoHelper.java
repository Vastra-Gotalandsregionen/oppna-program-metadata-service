/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.vgregion.metaservice.keywordservice.schema;

import se.vgregion.metaservice.keywordservice.domain.Identification;
import se.vgregion.metaservice.schema.domain.IdentificationType;

/**
 * HelperClass for conversion between SDO representation of Identification and the
 * common type POJO counterpart
 * @author tobias
 */
public class IdentificationSdoHelper {

    public IdentificationSdoHelper() {
        
    }

    /**
	 * Builds an SDO IdentificationType from an Identification object
	 *
	 * @param Identification identification
	 * @return IdentificationType
	 */
	public static IdentificationType toIdentificationType(Identification identification) {
		IdentificationType identType = new IdentificationType();
        identType.setProfileId(identification.getProfileId());
        identType.setUserId(identification.getUserId());

        return identType;
	}

	/**
	 * Builds an Identification from an SDO IdentificationType object
	 *
	 * @param IdentificationType identType
	 * @return Identification
	 */
	public static Identification fromIdentificationType(IdentificationType identType) {
        Identification identification = new Identification(identType.getUserId(), identType.getProfileId());
        return identification;
    }

}
