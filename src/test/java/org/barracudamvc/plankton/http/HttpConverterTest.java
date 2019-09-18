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
 * $Id: HttpConverterTest.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton.http;

import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class HttpConverterTest {

    @Test
    public void testHttpConverter() {
        //make sure the basic encoding works
        String ourl = "hello 100% - Ma & Pa";
        String eurl = HttpConverter.encode(ourl);
        String durl = HttpConverter.decode(eurl);
        assertTrue("Basic encode/decode failed - orig:" + ourl + " encoded:" + eurl + " decoded:" + durl, ourl.equals(durl));

        //now make sure we can go from a map to a string and back to a map correctly
        Map m1 = new HashMap();
        m1.put("Key1&75", "hello 100% - Ma & Pa");
        m1.put("Key2&75", "hello 100% - Ma & Pa");
        m1.put("Key3&75", "hello 100% - Ma & Pa");
        String url = HttpConverter.cvtMapToURLString(m1);
        Map m2 = HttpConverter.cvtURLStringToMap(url);
        assertTrue("Map to url to map encode/decode failed", m1.equals(m2));
    }

    @Test
    public void testFlatten() throws Exception {
        Map<Object, Object> map = new HashMap();
        Map<String, String> result = HttpConverter.flatten(map);
        assertThat(result.entrySet(), IsEmptyCollection.empty());

        map.put("A", "B");
        result = HttpConverter.flatten(map);
        assertThat(result.get("A"), is("B"));

        Set<String> item = new HashSet<>();
        item.add("C");
        map.put("B", item);
        result = HttpConverter.flatten(map);
        assertThat(result.get("B"), is("C"));
    }
}
