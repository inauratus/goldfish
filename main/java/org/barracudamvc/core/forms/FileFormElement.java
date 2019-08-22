/*
 * Copyright (C) 2014 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.forms;

import org.barracudamvc.core.forms.parsers.FormElementParser;

public class FileFormElement extends DefaultFormElement {

    public FileFormElement(String name, FormElementParser parser, FormValidator validator) {
        super(name, parser, null, validator);
    }

    public Object getVal() {
        return val;
    }
}
