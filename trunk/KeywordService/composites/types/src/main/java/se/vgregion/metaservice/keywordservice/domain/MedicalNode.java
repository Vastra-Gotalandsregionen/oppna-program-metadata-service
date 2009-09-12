package se.vgregion.metaservice.keywordservice.domain;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;



/**
 * Represents a Node in a Medical Taxonomy Tree. 
 * A Medical Node consists of a name and references to zero or more parents
 * @author tobias
 *
 */
public class MedicalNode {

	private String name;
	private String internalId;
	private String sourceId;
	private String namespaceId;
	private List<MedicalNode> parents = new ArrayList<MedicalNode>();
	private List<String> synonyms = new ArrayList<String>();
	private List<UserStatus> userStatus = new ArrayList<UserStatus>();
	private boolean hasChildren;
	//private UserStatus userStatus;
	
	/**
	 * Constructs a new MedicalNode with no attributes
	 */
	public MedicalNode() {
		
	}
	
	
	
	/**
	 * Constructs a MedicalNode with the specified name.
	 * @param name the name of the MedicalNode
	 */
	public MedicalNode(String name, String internalId) {
		this.name = name;
		this.internalId = internalId;
	}

	/**
	 * Returns the name of the MedicalNode
	 * @return the name of the MedicalNode
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the internalId for the MedicalNode
	 * @return the internalId for the MedicalNode
	 */
	public String getInternalId() {
		return internalId;
	}

	/**
	 * Returns the namespaceId for the MedicalNode
	 * @return the namespaceId for the MedicalNode
	 */
	public String getNamespaceId() {
		return namespaceId;		
	}

	/**
	 * Returns the parents of the MedicalNode
	 * @return the parents of the MedicalNode
	 */
	public List<MedicalNode> getParents() {
		return parents;
	}
	
	/**
	 * Returns whether the MedicalNode has children
	 * @return true if the MedicalNode has children, otherwise false
	 */
	public boolean getHasChildren() {
		return hasChildren;
	}
	
	/**
	 * Adds the specified MedicalNode as a parent to this MedicalNode
	 * @param parent the MedicalNode to ad as a parent
	 */
	public void addParent(MedicalNode parent) {
		this.parents.add(parent);
	}
	
	/**
	 * Adds a user status to this MedicalNode
	 * @param status the status to add
	 */
	/*public void addUserStatus(UserStatus status) {
		this.userStatus.add(status);
	}*/
	
	/**
	 * Returns all user status for this MedicalNode
	 * @return the list of user status for this MedicalNode
	 */
	public List<UserStatus> getUserStatus() {
		return userStatus;
	}
	
	/**
	 * Used for indicating how and if this MedicalNode has been used by the User.
	 * @author tobias
	 *
	 */
	public enum UserStatus {
		BOOKMARKED("Bookmarked"),
		TAGGED("Tagged");
	
	    private final String value;
		
		UserStatus(String value) {
			this.value = value;
		}
		
		public String value() {
			return value;
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setInternalId(String internalId) {
		this.internalId = internalId;
	}
	
	public void setNamespaceId(String namespaceId) {
		this.namespaceId = namespaceId;
	}

	public void setParents(List<MedicalNode> parents) {
		this.parents = parents;
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}
	
	public void setUserStatus(List<UserStatus> userStatus) {
		this.userStatus = userStatus;
	}
	
	@Override
	public String toString() {
		String output = MessageFormat.format("Name: {0}, InternalId: {1}, NamespaceId: {2}, #Parents: {3}", getName(),getInternalId(),getNamespaceId(),getParents().size());
		return output;
	}

	public List<String> getSynonyms() {
		return synonyms;
	}

	public void addSynonym(String synonym) {
		this.synonyms.add(synonym);
	}
	
	public void setSynonyms(List<String> synonyms) {
		this.synonyms = synonyms;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	
	
	
}
