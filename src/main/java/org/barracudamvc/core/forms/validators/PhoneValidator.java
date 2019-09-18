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
 * $Id: PhoneValidator.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.forms.validators;

// java imports:
import java.util.regex.*;


/**
 * Validator for ensuring a string is a valid phone number.
 *
 * @author  christianc@atmreports.com
 * @since   csc_110304_1
 */
public class PhoneValidator extends RegularExpressionValidator {

    public static Pattern PATTERN = Pattern.compile("^\\d{3}-\\d{3}-\\d{4}$");

    public PhoneValidator() {
        super(PATTERN, "Value is not a valid phone number (use 123-456-7890 format)");
    }

    public PhoneValidator(String ierrorMessage) {
        super(PATTERN, ierrorMessage);
    }
}

/*
 * $Log: DecimalValidator.java,v $
 */
