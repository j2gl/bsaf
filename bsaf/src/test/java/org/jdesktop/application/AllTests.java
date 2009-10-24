package org.jdesktop.application;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * @author Rob Ross
 * @version Date: Oct 23, 2009  1:29:59 PM
 */
@RunWith(value = Suite.class)
@Suite.SuiteClasses(value =
        {
                ApplicationActionMapTest.class,
                ApplicationActionsTest.class,
                ApplicationDefaultLNFResourceTest.class,
                ApplicationEndTest.class,
                ApplicationMotifLNFResourceTest.class,
                ApplicationNoLNFResourceTest.class,
                ApplicationPrivateCtorTest.class,
                ApplicationSystemLNFResourceTest.class,
                ApplicationTest.class,
                BadSessionStateTest.class,
                CustomPropertySupportTest.class,
                EnabledPropertyInGermanLocaleTest.class,
                LocalStorageTest.class,
                MnemonicTextTest.class,
                NoApplicationTest.class,
                ProxyActionTest.class,
                ResourceManagerTest.class,  //note, this a placeholder for  ResourceManager unit tests  to be written
                ResourceMapTest.class,
                TaskStateTest.class,
                TaskTest.class
        })
public class AllTests
{
}
