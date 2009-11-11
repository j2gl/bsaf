package org.jdesktop.application.inject;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * (c) 2009 Rob Ross
 * All rights reserved
 *
 * @author Rob Ross
 * @version Date: Nov 10, 2009  1:23:15 AM
 */
@RunWith(value = Suite.class)
@Suite.SuiteClasses(value =
        {
                TestAbstractButtonInjector.ASuite.class,
                TestActionInjector.ASuite.class,
                TestJLabelInjector.ASuite.class

        })
public class AllTests
{

}
