/*
 * Copyright (C) 2004 ATM Express, Inc [christianc@atmreports.com]
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
 * Christian Cryder, Diez B. Roggisch
 *
 * $Id: EmailValidator.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.forms.validators;

// java imports:
import java.util.*;
import java.util.regex.*;

// 3rd-party imports:
import org.barracudamvc.core.forms.*;


/**
 * Validator for ensuring a string is a valid email address.
 *
 * @author  christianc@atmreports.com
 * @since   csc_110304_1
 */
public class EmailValidator extends RegularExpressionValidator {

    public static Pattern PATTERN = Pattern.compile("^\\w[a-zA-Z_0-9\\-]*(\\.\\w[a-zA-Z_0-9\\-]*)*@\\w[a-zA-Z_0-9\\-]*(\\.\\w[a-zA-Z_0-9\\-]*)+$");

    public EmailValidator() {
        super(PATTERN, "Value is not a valid email address");
    }

    public EmailValidator(String ierrorMessage) {
        super(PATTERN, ierrorMessage);
    }

    //csc_070204_1 - added
    public void validateFormElement(Object val, FormElement element, boolean deferExceptions) throws ValidationException {
        if (localLogger.isInfoEnabled()) localLogger.info("val="+val+" "+(val==null ? "true" : "false"));

        if (this.isNull(val, element)) return;

        //if element is null err because this should
        //not be considered a valid option (note that these exceptions are always
        //immediate since they typically represent a programming error)
        if (element==null) throw new ValidationException(val, "Object val:"+val+" is associated with a null FormElement");

        if (val!=null) {
            StringTokenizer st = new StringTokenizer(val.toString(), ",");
            while (st.hasMoreTokens()) {
                String addr = st.nextToken().trim();
                Matcher m = pattern.matcher(addr.toString());
                if (!m.find()) {
                    this.setErrorMessage("Value: '"+addr+"' is not a valid email address");
                    throw this.generateException(element, deferExceptions, "Value does not match expression: "+pattern.pattern());
                }
            }
        }
    }

}

/*
 * $Log: DecimalValidator.java,v $
 */
