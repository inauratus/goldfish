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
 * $Id: IntegerValidator.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.forms.validators;

// java imports:
import java.util.regex.*;


/**
 * Validate that the value is an integer meeting specific requirements for the digit counts.
 *
 * @author  christianc@atmreports.com
 * @since   csc_110304_1
 */
public class IntegerValidator extends RegularExpressionValidator {

    public static final Pattern DEFAULT_PATTERN = getPattern(0, -1);

    public IntegerValidator() {
        super(DEFAULT_PATTERN, getMessage(0, -1));
    }

    public IntegerValidator(String errorMessage) {
        super(DEFAULT_PATTERN, errorMessage);
    }

    public IntegerValidator(int minDigits, int maxDigits) {
        super(getPattern(minDigits, maxDigits),
            getMessage(minDigits, maxDigits));
    }

    public IntegerValidator(int minDigits, int maxDigits, String errorMessage) {
        super(getPattern(minDigits, maxDigits), errorMessage);
    }

    protected static Pattern getPattern(int minDigits, int maxDigits) {
        if (minDigits<0) minDigits = 0;
        if (maxDigits<0) maxDigits = 10;
        if (maxDigits<minDigits) maxDigits = minDigits;

        return Pattern.compile("^(\\+|-)?\\d{"+minDigits+","+maxDigits+"}$");
    }

    protected static String getMessage(int minDigits, int maxDigits) {
        if (minDigits<0) minDigits = 0;
        if (maxDigits<0) maxDigits = 10;
        if (maxDigits<minDigits) maxDigits = minDigits;

        StringBuffer sb = new StringBuffer(100);
        sb.append("Value must be an integer");
        String sep = " with ";

        if (minDigits>0 || maxDigits<10) {
            sb.append(sep);
            if (minDigits==maxDigits) sb.append("exactly "+minDigits+" digit"+(minDigits!=1? "s": ""));
            else sb.append(minDigits+"-"+maxDigits+" digits");
            sep = " and ";
        }

        return sb.toString();
    }
}

/*
 * $Log: DecimalValidator.java,v $
 */
