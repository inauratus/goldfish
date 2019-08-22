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
 * $Id: TestDefaultEventBroker.java 252 2013-02-21 18:15:44Z charleslowery $
 */
package org.barracudamvc.core.event;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

public class DefaultEventBrokerTest {

    /**
     * Simple test to verify that the component renders as expected
     */
    @Test
    public void testListeners() throws InvalidClassException {

        //create the event broker
        DefaultEventBroker eb = new DefaultEventBroker(null, ".event");
        String id = null;
        String result = null;

        //create some aliases 
        String classes[] = new String[]{
            "foo.blah.event.Test",
            "foo.blah.event.Blarney",
            "foo.blah.event2.Test"
        };
        for (String classe : classes) {
            eb.addAliases(classe, eb.getAliases(classe), eb.eventXref);
        }

        //now see if we get matches
        id = "Blarney";
        result = eb.matchEventClass(id);
        assertTrue("failed to find id:" + id + ", got:" + result, classes[1].equals(result));
        id = "event2.Test";
        result = eb.matchEventClass(id);
        assertTrue("failed to find id:" + id + ", got:" + result, classes[2].equals(result));
        id = "Test";
        try {
            result = eb.matchEventClass(id);
            fail("failed to throw exception on id:" + id + ", got:" + result);
        } catch (InvalidClassException e) {
            //noop - this is what we expected
        }
        id = "BLARNEY";  //test upper case
        result = eb.matchEventClass(id);
        assertTrue("failed to find id:" + id + ", got:" + result, classes[1].equals(result));
        id = "blarney";  //test lower case
        result = eb.matchEventClass(id);
        assertTrue("failed to find id:" + id + ", got:" + result, classes[1].equals(result));
        id = "fOo.BlAh.EvEnT.bLaRnEy";  //test mixed case
        result = eb.matchEventClass(id);
        assertTrue("failed to find id:" + id + ", got:" + result, classes[1].equals(result));
    }
}
