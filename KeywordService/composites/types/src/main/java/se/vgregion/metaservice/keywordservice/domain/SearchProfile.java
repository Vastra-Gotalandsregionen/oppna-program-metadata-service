
package se.vgregion.metaservice.keywordservice.domain;

import java.util.Set;

/**
 * SearchProfile defines which namespaces are readable or
 * writeable. Each profile is identified by a profileId
 * which must be supplied when invoking any of the routines:
 * <pre>
 *   getKeywords
 *   getNodeByInternalId
 *   lookupWord
 *   addVocabularyNode
 *   moveVocabularyNode
 *   updateVocabularyNode
 * </pre>
 *
 * Note, the above list is just a sample and does not aim
 * to be complete. 
 */
public class SearchProfile {
    private String profileId = null;
    private Set<String> SearchableNamespaces;
    private Set<String> WriteableNamespaces;
    private NodePath blackList;
    private NodePath whiteList;
    private NodePath reviewList;

    public SearchProfile() {
    }

    public Set<String> getSearchableNamespaces() {
        return SearchableNamespaces;
    }

    public void setSearchableNamespaces(Set<String> SearchableNamespaces) {
        this.SearchableNamespaces = SearchableNamespaces;
    }

    public Set<String> getWriteableNamespaces() {
        return WriteableNamespaces;
    }

    public void setWriteableNamespaces(Set<String> WriteableNamespaces) {
        this.WriteableNamespaces = WriteableNamespaces;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public NodePath getBlackList() {
        return blackList;
    }

    public void setBlackList(NodePath blackList) {
        this.blackList = blackList;
    }

    public NodePath getReviewList() {
        return reviewList;
    }

    public void setReviewList(NodePath reviewList) {
        this.reviewList = reviewList;
    }

    public NodePath getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(NodePath whiteList) {
        this.whiteList = whiteList;
    }
    
}
