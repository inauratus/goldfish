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
 * $Id: ViewUtil.java 265 2014-02-21 17:40:07Z alci $
 */
package org.barracudamvc.core.view;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.barracudamvc.core.helper.servlet.ScriptDetector;

/**
 * <p>This class provides utility functions for determining the ViewCapabilities.
 * 
 * <p>Note that currently only the HTML related logic is fully implemented. If
 * you need support for specific versions of WML, CHTML, etc, you may have to
 * add some code here to examine the appropriate headers and map it to the 
 * proper types. If you have expertise working with these other flavors of 
 * markup and would like to help flesh out support for these languages, email
 * the list and we'll be glad to give you pointers...
 */
public class ViewUtil {
	
	protected static Logger logger = Logger.getLogger(ViewUtil.class);

    //base data types
    private static int HTML = 0;
    private static int CHTML = 1;
    private static int XML = 2;
    private static int VXML = 3;
    private static int WML = 4;
    private static int XHTML_BASIC = 5;
    private static int XHTML_STANDARD = 6;

    /**
     * Determine the format type by looking at the Accept header in 
     * the request
     *
     * @param req an HttpServletRequest object
     * @return the appropriate client type (defaults to FormatType.UNKNOWN_FORMAT
     *      if we are unable to make a positive match)
     */
    public static FormatType getFormatType(HttpServletRequest req) {
        FormatType ft = FormatType.UNKNOWN_FORMAT;
        int baseType = getBaseType(req);

        //figure out what we're dealing with
        //...HTML
        if (baseType==HTML) {
            ft = FormatType.HTML_4_0;
        //...CHTML
        } else if (baseType==CHTML) {
            ft = FormatType.CHTML_1_0;
        //...XML
        } else if (baseType==XML) {
            ft = FormatType.XML_1_0;
        //...VXML
        } else if (baseType==VXML) {
            ft = FormatType.VXML_1_0;
        //...WML
        } else if (baseType==WML) {
            ft = FormatType.WML_1_0;
        //...XHTML_BASIC
        } else if (baseType==XHTML_BASIC) {
            ft = FormatType.XHTML_BASIC_1_0;
        //...XHTML_STANDARD
        } else if (baseType==XHTML_STANDARD) {
            ft = FormatType.XHTML_STANDARD_1_0;
        }
        
        return ft;
    }

    /**
     * Determine the what type of scripting is supported by figuring 
     * out what kind of browser we're dealing with. Note that this method
     * does NOT determine whether or not the browser actually has scripting
     * enabled.
     *
     * @param req an HttpServletRequest object
     * @return the appropriate client type (defaults to ScriptingType.NONE
     *      if we are unable to make a positive match)
     */
    public static ScriptingType getScriptingType(HttpServletRequest req) {
        ScriptingType st = ScriptingType.NONE;
        if (req!=null) {
        
            //csc_102201.1
            //first and foremost see if the request includes a scripting tag,
            //which would be a direct indication from the client of what it
            //supports. If it comes back false, we can immediately return;
            //otherwise, we still try to figure out the specific level of support
            //by looking at the client type (except we can now safely default to
            //Javascript 1.0)
            Boolean enabled = ScriptDetector.scriptingEnabled(req);
            if (enabled!=null) {
                if (enabled.booleanValue()==false) return ScriptingType.NONE;
                else st = ScriptingType.JAVASCRIPT_1_0;
            }
        
            //first get the client type
            ClientType ct = getClientType(req);
            
            //now map accordingly (note that we are not actually determining
            //whether or not the scripting is enabled). I got these mappings from
            //http://www.digitalroom.net/index2.html
            //http://www.javascripter.net/faq/javascr3.htm
            if (ct instanceof ClientType.HtmlBrowser) {
                if (ct instanceof ClientType.IE5x) st = ScriptingType.JAVASCRIPT_1_3;
                else if (ct instanceof ClientType.IE4x) st = ScriptingType.JAVASCRIPT_1_2;  //note: the first link asserts (above) IE4x actually supports 1.3
                else if (ct instanceof ClientType.IE3x) st = ScriptingType.JAVASCRIPT_1_0;
                else if (ct instanceof ClientType.NN6x) st = ScriptingType.JAVASCRIPT_1_5;
                else if (ct instanceof ClientType.NN4x) st = ScriptingType.JAVASCRIPT_1_3;
                else if (ct instanceof ClientType.NN3x) st = ScriptingType.JAVASCRIPT_1_1;  //by 4x we're assuming NN 4.5 or higher
                else if (ct instanceof ClientType.Opera4x) st = ScriptingType.JAVASCRIPT_1_3;
                else if (ct instanceof ClientType.Html32Browser) st = ScriptingType.JAVASCRIPT_1_0;
            } else if (ct instanceof ClientType.WmlBrowser) {
                st = ScriptingType.WMLSCRIPT_1x;
            }
			if (logger.isDebugEnabled()) logger.debug("detecting client: type: " + ct.getClass().getName() + ", ScriptingType: " + st.getClass().getName());
        }
        return st;
    }

    /**
     * Determine the client type by looking at the User-Agent header in 
     * the request
     *
     * @param req an HttpServletRequest object
     * @return the appropriate client type (defaults to ClientType.UNKNOWN_BROWSER
     *      if we are unable to make a positive match)
     */
    public static ClientType getClientType(HttpServletRequest req) {
        ClientType ct = ClientType.UNKNOWN_BROWSER;
        chk: if (req!=null) {
            //get the user agent string
            String uas = req.getHeader("User-Agent");
            if (uas==null) uas = req.getHeader("user-agent");
            if (uas==null) break chk;
            uas = uas.toLowerCase();

            int baseType = getBaseType(req);

            //figure out what we're dealing with
            //...HTML
            if (baseType==HTML) {
                //these here are going to cover the most common situations
                //...Opera (we check for opera first since it masquerades as both IE and NN)
                if (uas.indexOf("opera")!=-1) {
                    if (uas.indexOf("opera/5")!=-1) ct = ClientType.OPERA_5x;
                    else if (uas.indexOf("opera/4")!=-1) ct = ClientType.OPERA_4x;
                    else if (uas.indexOf("opera 5")!=-1) ct = ClientType.OPERA_5x;
                    else if (uas.indexOf("opera 4")!=-1) ct = ClientType.OPERA_4x;
                    else if (uas.indexOf("opera/2")!=-1) ct = ClientType.HTML_BROWSER;
                    else if (uas.indexOf("opera 2")!=-1) ct = ClientType.HTML_BROWSER;
                    else ct = ClientType.HTML_3_2_BROWSER;
                //...IE (next we check for IE, since it also looks like NN)
                } else if (uas.indexOf("msie ")!=-1) {
                    if (uas.indexOf("rv:11")!=-1) ct = ClientType.IE_11x;
                    else if (uas.indexOf("msie 10")!=-1) ct = ClientType.IE_10x;
              	    else if (uas.indexOf("msie 9")!=-1) ct = ClientType.IE_9x;
                    else if (uas.indexOf("msie 8")!=-1) ct = ClientType.IE_8x;
               	    else if (uas.indexOf("msie 7")!=-1) ct = ClientType.IE_7x;
                    else if (uas.indexOf("msie 6")!=-1) ct = ClientType.IE_6x;
                    else if (uas.indexOf("msie 5")!=-1) ct = ClientType.IE_5x;
                    else if (uas.indexOf("msie 4")!=-1) ct = ClientType.IE_4x;
                    else if (uas.indexOf("msie 3")!=-1) ct = ClientType.IE_3x;
                    else if (uas.indexOf("msie 2")!=-1) ct = ClientType.HTML_BROWSER;
                    else ct = ClientType.HTML_3_2_BROWSER;
                //...NN  (it its none of the above, its probably NN)
                } else if (uas.indexOf("mozilla")!=-1) {
                    if (uas.indexOf("netscape6")!=-1) ct = ClientType.NN_6x;
                    else if (uas.indexOf("gecko")!=-1) ct = ClientType.NN_6x;
                    else if (uas.indexOf("mozilla/5")!=-1) ct = ClientType.HTML_4_0_BROWSER;
                    else if (uas.indexOf("mozilla/4")!=-1) ct = ClientType.NN_4x;
                    else if (uas.indexOf("mozilla/3")!=-1) ct = ClientType.NN_3x;
                    else if (uas.indexOf("mozilla 5")!=-1) ct = ClientType.HTML_4_0_BROWSER;
                    else if (uas.indexOf("mozilla 4")!=-1) ct = ClientType.NN_4x;
                    else if (uas.indexOf("mozilla 3")!=-1) ct = ClientType.NN_3x;
                    else ct = ClientType.HTML_BROWSER;
                }
                if (ct!=ClientType.UNKNOWN_BROWSER) break chk;
            
            
                //if we're still looking at an unknown browser, now we can get 
                //more specific (we do these checks down here for performance 
                //purposes...we only want to check these when we have to)
                if (uas.indexOf("lynx")!=-1) ct = ClientType.HTML_BROWSER;
				else if (uas.indexOf("konqueror")!=-1) ct = ClientType.HTML_BROWSER;
                else if (uas.indexOf("mozilla")!=-1) ct = ClientType.HTML_BROWSER;
				else if (uas.indexOf("w3m")!=-1) ct = ClientType.HTML_BROWSER;
				else if (uas.indexOf("WWW-Mechanize")!=-1) ct = ClientType.HTML_BROWSER;
            //...CHTML
            } else if (baseType==CHTML) {
                //TODO: we still need better granularity for this
                ct = ClientType.CHTML_BROWSER;
            //...XML
            } else if (baseType==XML) {
                //TODO: we still need better granularity for this
                ct = ClientType.XML_BROWSER;
            //...VXML
            } else if (baseType==VXML) {
                //TODO: we still need better granularity for this
                ct = ClientType.VXML_BROWSER;
            //...WML
            } else if (baseType==WML) {
                //TODO: we still need better granularity for this. A good place
                //to look for a whole slew of sample headers is
                //http://amaro.g-art.nl/useragent/
                ct = ClientType.WML_BROWSER;
            //...XHTML_BASIC
            } else if (baseType==XHTML_BASIC) {
                //TODO: we still need better granularity for this
                ct = ClientType.XHTML_BROWSER;
            //...XHTML_STANDARD
            } else if (baseType==XHTML_STANDARD) {
                //TODO: we still need better granularity for this
                ct = ClientType.XHTML_BROWSER;
            }
        }
        return ct;
    }

    //look in the accept header to determine the basic content type
    private static int getBaseType(HttpServletRequest req) {
        int baseType = HTML;    //default
        chk: if (req!=null) {
            //get the accept string
            String acc = req.getHeader("Accept");
            if (acc==null) acc = req.getHeader("accept");
            if (acc==null) break chk;
            acc = acc.toLowerCase();

            //now figure out what we're dealing with
            if (acc.indexOf("text/html")!=-1) baseType = HTML;
            else if (acc.indexOf("text/vnd.wap.wml")!=-1) baseType = WML;
            else if (acc.indexOf("text/chtml")!=-1) baseType = CHTML;
            else if (acc.indexOf("text/xml")!=-1) baseType = XML;
            //TODO: identify VXML
            //TODO: identify XHTML_BASIC
            //TODO: identify XHTML_STANDARD
       }
        return baseType;
    }
}