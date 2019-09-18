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
 * $Id: RegularExpressionValidator.java 248 2012-03-13 16:53:30Z alci $
 */
package org.barracudamvc.core.forms.validators;

// java imports:
import java.util.regex.*;

// 3rd-party imports:
import org.barracudamvc.core.forms.*;


/**
 * A validator for checking a string value against a regular expression pattern.
 * This validator will try to _find_ the pattern in the user input.
 * To have a match behaviour, you must use a suitable pattern, eg if you
 * want to have only uppercase letters, you will use ^[A-Z]*$
 * Using [A-Z]* will validate you have at least one uppercase letter in the input.
 *
 * @author  christianc@atmreports.com
 * @since   csc_110304_1
 */
public class RegularExpressionValidator extends DefaultFormValidator {

    Pattern pattern = null;

    public RegularExpressionValidator(Pattern ipattern) {
        super();
        this.pattern = ipattern;
    }

    public RegularExpressionValidator(Pattern ipattern, String ierrorMessage) {
        super(ierrorMessage);
        this.pattern = ipattern;
    }

    public void validateFormElement(Object val, FormElement element, boolean deferExceptions) throws ValidationException {
        if (localLogger.isInfoEnabled()) localLogger.info("val="+val+" "+(val==null ? "true" : "false"));

        if (this.isNull(val, element)) return;

        //if element is null err because this should
        //not be considered a valid option (note that these exceptions are always
        //immediate since they typically represent a programming error)
        if (element==null) throw new ValidationException(val, "Object val:"+val+" is associated with a null FormElement");


        if (val!=null) {
            Matcher m = pattern.matcher(val.toString());
            if (!m.find()) { 
                throw this.generateException(element, deferExceptions, "Value does not match expression: "+pattern.pattern());
            }
        }
    }
}

/*
 * $Log: RegularExpressionValidator.java,v $
 */
