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
 * $Id: DefaultPropertiesModel.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.barracudamvc.plankton.l10n.Localize;



/**
 * This class provides a default implementation of a TemplateModel
 * that looks up its keys in a properties file
 */
public class DefaultPropertiesModel extends AbstractTemplateModel {

    //public vars
    protected static final Logger logger = Logger.getLogger(DefaultPropertiesModel.class.getName());

    protected String modelName = null;
    protected String propFileName = null;


    //--------------- Constructors ---------------------------
    public DefaultPropertiesModel() {
        super();
    }
    
    public DefaultPropertiesModel(String ipropFileName) {
        setPropFileName(ipropFileName);
    }

    
    //--------------- DefaultPropertiesModel -----------------
    public void setPropFileName(String ipropFileName) {
        //set the prop file name
        propFileName = ipropFileName;

        //set the model name based on the prop file name. While the 
        //prop file name is fully qualified, the model name will just be
        //set to the last portion of the prop file name
        if (propFileName!=null) {
            int pos = propFileName.lastIndexOf(".");
            if (pos>-1) modelName = propFileName.substring(pos+1);
            else modelName = propFileName;
        }
    }
    
    

    //--------------- AbstractTemplateModel ------------------
    public String getName() {
        return modelName;
    }

//    public Object getItem(String key, ViewContext vc) {
    public Object getItem(String key) {
        //By default, we're going to try and return the key value from
        //the underlying properties file. To do this, we first need to
        //try and figure out what format our view expects. We get this 
        //information by looking in the ViewCapabilities file. If the
        //VC info is not set, just assume default locale...

        //figure out the target locale
        ViewContext vc = getViewContext();
        Locale targetLocale = vc.getViewCapabilities().getClientLocale();
    
        //get the appropriate resource bundle (we don't need to worry about
        //caching the resource bundle since the ResourceBundle class already
        //does that for us (cool!))
//jrk_20020414.1_start
        //If this class is not within the same class loader as the resource
        //that is being referred to, it won't find it and will throw a
        //java.util.MissingResourceException.  So, we provide a class loader by saying: 
        //Thread.currentThread().getContextClassLoader()
        //This should fix some classloading issues in Engines with multiple
        //class loaders (eg.. Tomcat-3.3.x and Tomcat-4.x.x)
        //ResourceBundle rb = ResourceBundle.getBundle(propFileName, targetLocale);
        ResourceBundle rb = ResourceBundle.getBundle(propFileName, targetLocale, Thread.currentThread().getContextClassLoader());
//jrk_20020414.1_end
        
        //now try looking up the value
        return Localize.getString(rb, key);
    }
}