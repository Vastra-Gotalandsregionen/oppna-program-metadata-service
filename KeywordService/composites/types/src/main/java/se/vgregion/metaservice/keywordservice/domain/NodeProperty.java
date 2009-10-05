/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.vgregion.metaservice.keywordservice.domain;

/**
 *
 * @author sture.svensson
 */
public class NodeProperty {

    public NodeProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NodeProperty other = (NodeProperty) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
            return false;
        }
        return true;
    }


    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 19 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}
