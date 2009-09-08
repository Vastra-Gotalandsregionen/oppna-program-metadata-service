package se.vgregion.metaservice.keywordservice;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public abstract class BaseSpringDependencyInjectionTest extends
		AbstractDependencyInjectionSpringContextTests {

	
	@Override
	protected String[] getConfigLocations() {
		// TODO Auto-generated method stub
		//return super.getConfigLocations();
		return new String[]{"services-test-config.xml"};
	}
}
