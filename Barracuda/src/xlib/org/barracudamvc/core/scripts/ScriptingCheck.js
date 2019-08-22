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
 * $Id: ScriptingCheck.js 114 2005-12-09 15:51:51Z christianc $
 */

//--------------------- Public functions ----------------------------
//csc_102200.1 - created
/**
 * Used to check a client page to see if it supports scripting
 */
function sc_CheckPage() {
    
	//iterate through the document's links
	for (var i=0; i<document.links.length; i++) {
	    var link = document.links[i];
        if (link.href.indexOf("$csjs=true")>-1 || link.href.indexOf("mailto:")>-1 || link.href.indexOf("javascript:")>-1 || link.href.indexOf("data:")>-1 || link.href.indexOf("jar:")>-1) continue;
        var pos = link.href.indexOf("$csjs=false");
        //if the link already contains a csjs reference
        if (pos>-1) {
            var href2 = link.href.substring(0,pos)+"$csjs=true";
            if (pos+11<link.href.length) href2 += link.href.substring(pos+11, link.href.length);
            link.href = href2;
        //otherwise just append it on    
        } else {
            var sep = "?";
            if (link.href.indexOf(sep)>-1) sep = "&amp;";
            var href2 = link.href;
            if (link.hash.length>0) href2 = link.href.substring(0,link.href.indexOf(link.hash)-1);
            link.href = href2 + sep + "$csjs=true" + link.hash;
        }
	}
    
    //now iterate through the forms
	for (var i=0; i<document.forms.length; i++) {
	    var form = document.forms[i];
        
        //see if we can find a csjs element, and set its
        //value to true if it exists
	    for (var j=0; j<form.elements.length; j++) {
	        var element = form.elements[j];
            if (element.name=="$csjs") {
                //set the value
                element.value = "true";

                //break
                break;
            }
	    }
    }
}

//--------------------- Utility functions ---------------------------
