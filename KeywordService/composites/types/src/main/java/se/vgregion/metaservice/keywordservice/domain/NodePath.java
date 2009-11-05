
package se.vgregion.metaservice.keywordservice.domain;

/**
 * NodePath represents the path to a node. A nodepath is constructed
 * by namespace/path/to/node.
 */
public class NodePath {
    private String namespace;
    private String relativePath;

    public NodePath() {
    }


    /**
     * Set the full path of the node on the form
     * namespace/relative/path.
     *
     * @param path The full node path
     */
    public void setPath(String path) {
        namespace = path.substring(0, path.indexOf('/'));
        this.relativePath = path.substring(path.indexOf('/') + 1);
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
