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
 * $Id: ZipCodeValidator.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.forms.validators;

// java imports:
import java.util.regex.*;


/**
 * Validator for ensuring a string is a valid zip code.
 *
 * @author  christianc@atmreports.com
 * @since   csc_110304_1
 */
public class ZipCodeValidator extends RegularExpressionValidator {

    public static Pattern PATTERN = Pattern.compile("^\\d{5}(-\\d{4})?$");

    public ZipCodeValidator() {
        super(PATTERN, "Value is not a valid zip code (use 12345 or 12345-6789 format)");
    }

    public ZipCodeValidator(String ierrorMessage) {
        super(PATTERN, ierrorMessage);
    }
}

/*
 * $Log: ZipCodeValidator.java,v $
 */
