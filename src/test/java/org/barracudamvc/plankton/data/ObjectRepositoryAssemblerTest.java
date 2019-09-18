/*
 * Copyright (C) 2003  Christian Cryder [christianc@granitepeaks.com]
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * $Id: ObjectRepositoryAssemblerTest.java 260 2013-10-24 14:41:40Z charleslowery $
 */
package org.barracudamvc.plankton.data;

import java.io.InputStream;

import org.hamcrest.core.IsNull;
import org.junit.Test;
import static org.junit.Assert.*;

public class ObjectRepositoryAssemblerTest {

    @Test
    public void given_object_reference_in_property_set_referenced_value() {
        InputStream file = this.getClass().getResourceAsStream("ob-repo-test-property.xml");
        new ObjectRepositoryAssembler().assemble(null, file);

        assertNotNull(ValueObject.HOME);
    }

    @Test
    public void testAssembler() {
        InputStream file = this.getClass().getResourceAsStream("object-repository-tests.xml");

        //assemble into the default ObjectRepository
        new ObjectRepositoryAssembler().assemble(null, file);
        //now make sure the values got changed
        assertTrue("Failed to set TEST_CLASS", ValueObject.TEST_CLASS.equals(Param.class));
        assertTrue("Failed to set TEST_STRING", ValueObject.TEST_STRING.equals("foo"));
        assertTrue("Failed to set TEST_INT", ValueObject.TEST_INT == -99);
        assertTrue("Failed to set TEST_SHORT", ValueObject.TEST_SHORT == ((short) 99));
        assertTrue("Failed to set TEST_LONG", ValueObject.TEST_LONG == ((long) 99));
        assertTrue("Failed to set TEST_DOUBLE", ValueObject.TEST_DOUBLE == ((double) 99));
        assertTrue("Failed to set TEST_FLOAT", ValueObject.TEST_FLOAT == ((float) 99));
        assertTrue("Failed to set TEST_BOOLEAN", ValueObject.TEST_BOOLEAN == true);
        assertEquals("Failed to set TEST_INT2", ValueObject.TEST_INT2, new Integer(99));
        assertEquals("Failed to set TEST_SHORT2", ValueObject.TEST_SHORT2, new Short((short) 99));
        assertEquals("Failed to set TEST_LONG2", ValueObject.TEST_LONG2, new Long(99));
        assertEquals("Failed to set TEST_DOUBLE2", ValueObject.TEST_DOUBLE2, new Double(99));
        assertEquals("Failed to set TEST_FLOAT2", ValueObject.TEST_FLOAT2, new Float(99));
        assertEquals("Failed to set TEST_BOOLEAN2", ValueObject.TEST_BOOLEAN2, Boolean.TRUE);
        assertTrue("Failed to set TEST_STRING2", ValueObject.TEST_STRING2.equals("foo"));
        assertTrue("Failed to set TEST_STRING3", ValueObject.TEST_STRING3.equals("jujubean"));

        ObjectRepository or = ObjectRepository.getGlobalRepository();
        assertEquals("Failed to set TEST_KEY1", or.getState("TEST_KEY1").toString(), "my very elderly mother just sent us nine pizzas");
        assertEquals("Failed to set TEST_KEY2", or.getState("TEST_KEY2").toString(), "sometimes you feel like a nut, sometimes you don't");
        assertEquals("Failed to set TEST_KEY3", or.getState("TEST_KEY3"), or.getState("TEST_KEY2").toString());
        assertEquals("Failed to set TEST_KEY4", or.getState("TEST_KEY4"), ObjectRepositoryAssembler.class);
        assertTrue("Failed to set TEST_KEY5", or.getState("TEST_KEY5") != null);

        assertNotNull(ValueObject.TEST_VALUE_1);
        assertEquals("Failed to set TEST_VALUE_1", ValueObject.TEST_VALUE_1.toString(), "my very elderly mother just sent us nine pizzas");
        //todo: we really ought to be testing the other aspects of this class as well (instantiating
        //an object, invoking methods on it, and placing it in the object repository)

    }
}
