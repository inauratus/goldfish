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
 * $Id: Localize.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton.l10n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Simple localization utilities.
 */
public class Localize {

    private static String missingBundle = "[[Missing Bundle]]";
    private static String missingKey = "[[Missing Key]]";
    private static String missingData = "[[Missing Data]]";

    /**
     * Get data from a resource bundle. If the data is not available an
     * appropriate error message is returned instead.
     *
     * @param rb the resource bundle
     * @param key the key we are after
     * @return the value in the resource bundle for the given key
     */
    public static String getString(ResourceBundle rb, String key) {

        //eliminate the obvious
        if (rb==null) return missingBundle;
        if (key==null) return missingKey;

        //get the string
        String s = null;
        try {
            s = rb.getString(key);
        } catch (MissingResourceException mre) {
            mre.printStackTrace();
        }

        //sanity check on what we got back
        if (s==null) s = missingData;

        //now return it
        return s.trim();
    }

}
