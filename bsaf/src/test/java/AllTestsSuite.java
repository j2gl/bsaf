/**
 * @author Rob Ross
 * @version Date: Oct 15, 2009  12:03:13 AM
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(value = Suite.class)
@Suite.SuiteClasses(value =
        {
                org.jdesktop.application.convert.AllTests.class,
                org.jdesktop.application.inject.AllTests.class,
                org.jdesktop.application.resource.AllTests.class,
                org.jdesktop.application.AllTests.class
        })
public class AllTestsSuite
{
}
