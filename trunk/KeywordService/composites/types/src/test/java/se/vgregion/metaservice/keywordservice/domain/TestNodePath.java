package se.vgregion.metaservice.keywordservice.domain;

import junit.framework.TestCase;

/**
 *
 * @author tobias
 */
public class TestNodePath extends TestCase {

    private static String fullPath = "VGR/subnode/whitelist";

    public void testFullPath() {
        NodePath path  = new NodePath();
        path.setPath(fullPath);
        assertEquals(fullPath, path.getPath());
    }
    
    public void testNamespace() {
        NodePath path  = new NodePath();
        path.setPath(fullPath);
        assertEquals("VGR", path.getNamespace());
    }

    public void testOnlyNamespace() {
        NodePath path = new NodePath();
        path.setPath("VGR");
        assertEquals("VGR", path.getNamespace());
    }

    public void testRelativePath() {
        NodePath path  = new NodePath();
        path.setPath(fullPath);
        assertEquals("subnode/whitelist", path.getRelativePath());
    }


    public void testRelativePathWithLeadingSlash() {
        NodePath path  = new NodePath();
        path.setPath("/"+fullPath);
        assertEquals("subnode/whitelist", path.getRelativePath());
    }


    public void testName() {
        NodePath path  = new NodePath();
        path.setPath(fullPath);
        assertEquals("whitelist", path.getName());
    }
}