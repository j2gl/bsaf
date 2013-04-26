/*
 * Copyright (C) 2013 Kevin Greiner.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
*/

package org.jdesktop.application;

import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;


/**
 * Verify that a Application can be constructed and launched using a custom
 * ApplicationContext.
 */
public class ApplicationCustomContextTest{

    /* Support for private (not static) inner classes isn't provided
     * by Application.launch() because then we'd have to find a way to
     * pass an instance of the enclosing class along.
     */
    private static class CustomContextApplication extends WaitForStartupApplication {
        private CustomContextApplication() { 
            super(new CustomApplicationContext());
        }
    }
    
    private static class CustomApplicationContext extends ApplicationContext {
        
    }

    @Before
    public void methodSetup()
    {
        CustomContextApplication.launchAndWait(CustomContextApplication.class);
    }

    /**
     * Verify that the app has a custom context.
     */
    @Test
    public void testPrivateConstructor() {
	CustomContextApplication app = Application.getInstance(CustomContextApplication.class);
        assertTrue(app.getContext() instanceof CustomApplicationContext);
    }
}


