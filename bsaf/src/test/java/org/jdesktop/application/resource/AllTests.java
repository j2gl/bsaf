package org.jdesktop.application.resource;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.jdesktop.application.resource.locale.TestLocaleChange;


/**
 * (c) 2009 Rob Ross
 * All rights reserved
 *
 * @author Rob Ross
 * @version Date: Oct 16, 2009  9:21:49 PM
 */
@RunWith(value = Suite.class)
@Suite.SuiteClasses(value =
        {
                TestResourceMap1.class,
                TestResourceMapCaching1.class,
                TestResourceMapLocale_es1.class,
                TestResourceMapWithParent.TestParentMapData.class,
                TestResourceMapWithParent.TestChildMapData.class,
                TestResourceMapWithParent.TestChildUsingLocale_es.class,
                TestGetArrays.class,
                TestExpressionEval.class,
                TestImportedPropFiles.class,
                TestLocaleChange.class
        })
public class AllTests
{
}
