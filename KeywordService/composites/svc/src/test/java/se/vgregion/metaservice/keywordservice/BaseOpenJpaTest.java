package se.vgregion.metaservice.keywordservice;

import org.springframework.test.jpa.AbstractJpaTests;

public abstract class BaseOpenJpaTest extends AbstractJpaTests {

	@Override
	protected String[] getConfigLocations() {
		// TODO Auto-generated method stub
		// return super.getConfigLocations();
		return new String[] { "services-test-config.xml"};
	}

    @Override
    protected boolean shouldUseShadowLoader() {
        return false;
    }



}
