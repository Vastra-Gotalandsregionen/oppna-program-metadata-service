
package se.vgregion.metaservice.LemmatisationService.model;

import java.net.URL;

/**
 *
 * @author johan.sjoberg
 */
public class FilesystemDictionary {
    private String location;
    private String identifier;

    public FilesystemDictionary() {
    }

    public FilesystemDictionary(String location, String identifier) {
        this.location = location;
        this.identifier = identifier;
    }


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    
}
