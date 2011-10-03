
package se.vgregion.metaservice.keywordservice.domain;

import java.io.Serializable;

/**
 * Identification holds information to identify a user.
 * It has two id's matching a user and a profile in the
 * Appelon system.
 */
public class Identification implements Serializable{


    //No default values allowed?
    String userId;
    String profileId;

    public Identification(String userId, String profileId) {
        this.userId = userId;
        this.profileId = profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileId() {
        return profileId;
    }

    public String getUserId() {
        return userId;
    }



}
