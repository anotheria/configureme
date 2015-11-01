package org.configureme;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

//this suite contains all clases with names not ended as Test.
@RunWith(value=Suite.class)
@SuiteClasses(value={TestForErrors.class, ReadConfigurationViaManager.class, AutoReConfig.class})
public class TemporarySuite {

}
