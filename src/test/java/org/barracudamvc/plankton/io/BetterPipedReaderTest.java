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
 * $Id: BetterPipedReaderTest.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton.io;

import java.io.IOException;
import java.util.Random;
import org.barracudamvc.util.TestUtil;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class BetterPipedReaderTest {

    String srcstr = null;

    public static void main(String args[]) {
        TestUtil.run(BetterPipedReaderTest.class);
    }

    @Test
    public void testReader() throws IOException {

        for (int i = 1; i < 3000; i++) {
            //add the tests
            byte[] bytes = new byte[i];
            new Random().nextBytes(bytes);
            srcstr = new String(bytes);
            test();
        }
    }

    private void test() throws IOException {

        //create the reader/writer
        BetterPipedWriter pw = new BetterPipedWriter();
        BetterPipedReader pr = new BetterPipedReader();
        pr.connect(pw);

        //write the string (this should cause the writer to expand as needed, since we haven't started reading yet)
        pw.write(srcstr);
        pw.close();

        //read the string
        StringBuilder sb2 = new StringBuilder(srcstr.length());
        int len = 1024;
        char[] data = new char[len];
        while (true) {
            int cnt = pr.read(data, 0, len);
            if (cnt == -1)
                break;
            sb2.append(data, 0, cnt);
        }
        String s2 = sb2.toString();

        //check for equality
        assertEquals("[" + srcstr.length() + "] s1!=s2", srcstr, s2);

        pr.close();
    }
}
