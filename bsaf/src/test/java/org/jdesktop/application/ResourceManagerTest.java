/*
* Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
* subject to license terms.
*/


package org.jdesktop.application;

import junit.framework.TestCase;

/**
 * [TBD]
 *
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */

public class ResourceManagerTest extends TestCase {
    public ResourceManagerTest(String testName) {
        super(testName);
    }

    class TestResourceManager extends ResourceManager {
        TestResourceManager() {
            super(new ApplicationContext());
        }
    }

    TestResourceManager resourceManager() {
        return new TestResourceManager();
    }

    public void testBasics() {
        TestResourceManager manager = resourceManager();
        ResourceMap rm = manager.getResourceMap(getClass());
        // todo
    }
}
