
package se.vgregion.metaservice.keywordservice.domain;

/**
 * NodePath represents the path to a node. A nodepath is constructed
 * by namespace/path/to/node.
 */
public class NodePath {
    private String namespace;
    private String relativePath;
    private String name;

    public NodePath() {
    }


    /**
     * Set the full path of the node on the form
     * namespace/relative/path.
     *
     * @param path The full node path
     */
    public void setPath(String path) {
        if(path.startsWith("/")) {
            path = path.substring(1);
        }
        int namespaceSeparatorIndex = path.indexOf('/');
        if(namespaceSeparatorIndex == -1)
            namespace = path;
        else
            namespace = path.substring(0, namespaceSeparatorIndex);
        
        this.relativePath = path.substring(path.indexOf('/') + 1);
        this.name = path.substring(path.lastIndexOf('/') + 1);
    }

    /**
     * Get the full path of the node on the form
     * namespace/relative/path.
     *
     * @return The full node path
     */
    public String getPath() {
        return namespace + "/" + relativePath;
    }

    /**
     * Get the namespace of the node 
     * 
     * @return
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Set the namespace of the node
     *
     * @param namespace The node namespace
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Get the name of the node without leading path
     * @return the name of the node
     */
    public String getName() {
        return name;
    }


    /**
     * Get the full path of the node without the namespace prefix.
     *
     * @return The relative path of the node
     */
    public String getRelativePath() {
        return relativePath;
    }

    /**
     * Set the full path of the node without the namespace prefix.
     *
     * @param relativePath The relative path of the node
     */

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }


    @Override
    public String toString() {
        return getPath();
    }
}
