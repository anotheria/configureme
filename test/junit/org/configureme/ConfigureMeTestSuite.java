package org.configureme;

import org.configureme.environments.DynamicEnvironment;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value=Suite.class)
@SuiteClasses(DynamicEnvironment.class)
public class ConfigureMeTestSuite {

}
