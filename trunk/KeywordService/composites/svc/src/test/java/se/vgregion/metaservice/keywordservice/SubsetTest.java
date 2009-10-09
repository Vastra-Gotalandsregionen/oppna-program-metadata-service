/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.vgregion.metaservice.keywordservice;

import java.util.List;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode;

/**
 *
 * @author tobias
 */
public class SubsetTest extends BaseSpringDependencyInjectionTest{

    MedicalTaxonomyService mts;
    	@Override
	protected void onSetUp() throws Exception {
		super.onSetUp();
		mts = (MedicalTaxonomyService) applicationContext.getBean("metaservice.medicalTaxonomyService");
		assertTrue(mts.initConnection());
	}

        public void testFindNodeInWhitelist() throws Exception {
        String whitelistName = "Whitelist";
        String nodeName = "influensa";
        List<MedicalNode> nodes = mts.findNodes(nodeName,true);
        assertTrue(nodes.size() >= 1);
        for(MedicalNode node : nodes) {
            for(MedicalNode parent : node.getParents()) {
                if(parent.getName().equals(whitelistName)) {
                        assertTrue(true);
                        }
            }
        }
    }
}
