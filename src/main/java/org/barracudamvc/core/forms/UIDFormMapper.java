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
 * $Id: UIDFormMapper.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.forms;

import java.util.*;

/**
 * <p>A UIDFormMapFilter makes it possible to map similar form elements based on a UID. 
 * For instance, let's say you have a url which contains parameters something like this:
 *
 * <ul>
 *     <li>Parm1::1234 = 5                  </li>
 *     <li>Parm2::1234 = Foo                </li>
 *     <li>Parm1::5432 = 6                  </li>
 *     <li>Parm2::5432 = Blah               </li>
 *     <li>...                              </li>
 * </ul>
 *
 * <p>What we really have here is a list of data "rows" that have been flattened
 * using a simple naming convention (key + "::" + uid = value). In other words,
 * the data viewed hierarchically would really look something like this:
 *
 * <ul>
 *     <li>uid 1234                         
 *         <ul>
 *             <li>Parm1 = 5                </li>
 *             <li>Parm2 = Foo              </li>
 *         </ul>
 *     </li>
 *     <li>uid 5432                         
 *         <ul>
 *             <li>Parm1 = 6                </li>
 *             <li>Parm2 = Blah             </li>
 *         </ul>
 *     </li>
 *     <li>...                              </li>
 * </ul>
 *
 * <p>The naming convention has been used to flatten a hierarchical set of data into
 * a flat set of data which can be submitted via a URL.
 *
 * <p>If we were dealing with a finite, known set of uids, then we <i>could</i> define
 * every item as its own form element, but that would be very burdensome on the developer,
 * plus there will be many cases where we don't know the uids. What we need then is a 
 * form map that is smart enough to handle such a UID naming scheme, and take it into 
 * account automatically. That's what this class does.
 *
 * <p>When mapping the form, this filter causes any element which conform to a basic pattern
 * (key + uid token + uid) to be mapped using the previously defined element for key.
 *
 * @author  Christian Cryder [christianc@granitepeaks.com]
 * @since   2.0
 */
public class UIDFormMapper extends DefaultFormMapper {

    public static final String UID_TOKEN = "::";

    protected Set<String> uids = new TreeSet<String>();
    protected String uidToken = UID_TOKEN;
    
    /**
     * Create a default UID form amp filter. UID token defaults
     * to "::".
     */
    public UIDFormMapper() {
        iterateOverParams = true;   //force mapping to be driven by param keys, not element keys
    }
    
    /**
     * Create a UID form map using a custom UID token
     */
    public UIDFormMapper(String iuidToken) {
        uidToken = iuidToken;
        iterateOverParams = true;   //force mapping to be driven by param keys, not element keys
    }
    

    //------------------------------------------------------------
    //protected methods for custom mappers to override
    //------------------------------------------------------------
    protected void preMap() {
        uids.clear();
    }

    protected FormElement getElementForMapping(FormMap fm, String paramKey) {
        //first see if we're dealing with a uid key
        int spos = paramKey.indexOf(uidToken);
        if (spos<0) return null;
        
        //if we are, extract the key/uid info
        String subKey = paramKey.substring(0, spos);
        String uid = paramKey.substring(spos+uidToken.length());
    
        //now see if we can find the underlying form element which corresponds to this uid element
        FormElement feMaster = fm.getElement(subKey);
        if (feMaster==null) return null;
    
        //assuming we have a match, now we need to create a NEW form element (essentially a copy
        //of the underlying form element) for the fully qualified uid key
        FormElement feNew = new DefaultFormElement(feMaster);
        feNew.setKey(paramKey);
        uids.add(uid);
    
        //return the newly created element    
        return feNew;
    }



    //------------------ UIDFormMapper ---------------------------
    public Set getMappedUIDs() {
        return uids;
    }

    public FormElement getElementByUID(FormMap fm, String subKey, String uid) {
        return fm.getElement(subKey+uidToken+uid);
    }
}
