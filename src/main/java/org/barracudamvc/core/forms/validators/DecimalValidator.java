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
 * $Id: DecimalValidator.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.forms.validators;

// java imports:
import java.util.regex.*;


/**
 * Validate that the value is a decimal meeting specific requirements for digit counts before and
 * after the point.
 *
 * @author  christianc@atmreports.com
 * @since   csc_110304_1
 */
public class DecimalValidator extends RegularExpressionValidator {

    public static final Pattern DEFAULT_PATTERN = getPattern(0, -1, 0, -1);

    public DecimalValidator() {
        super(DEFAULT_PATTERN, getMessage(0, -1, 0, -1));
    }

    public DecimalValidator(String errorMessage) {
        super(DEFAULT_PATTERN, errorMessage);
    }

    public DecimalValidator(int minBefore, int maxBefore, int minAfter, int maxAfter) {
        super(getPattern(minBefore, maxBefore, minAfter, maxAfter),
            getMessage(minBefore, maxBefore, minAfter, maxAfter));
    }

    public DecimalValidator(int minBefore, int maxBefore, int minAfter, int maxAfter, String errorMessage) {
        super(getPattern(minBefore, maxBefore, minAfter, maxAfter), errorMessage);
    }

    protected static Pattern getPattern(int minBefore, int maxBefore, int minAfter, int maxAfter) {
        if (minBefore<0) minBefore = 0;
        if (maxBefore<0) maxBefore = 10;
        if (maxBefore<minBefore) maxBefore = minBefore;
        if (minAfter<0) minAfter = 0;
        if (maxAfter<0) maxAfter = 10;
        if (maxAfter<minAfter) maxAfter = minAfter;

        return Pattern.compile("^(\\+|-)?\\d{"+minBefore+","+maxBefore+"}(\\.\\d{"+minAfter+","+maxAfter+"})"+(minAfter>0? "": "?")+"$");
    }

    protected static String getMessage(int minBefore, int maxBefore, int minAfter, int maxAfter) {
        if (minBefore<0) minBefore = 0;
        if (maxBefore<0) maxBefore = 10;
        if (maxBefore<minBefore) maxBefore = minBefore;
        if (minAfter<0) minAfter = 0;
        if (maxAfter<0) maxAfter = 10;
        if (maxAfter<minAfter) maxAfter = minAfter;

        StringBuffer sb = new StringBuffer(100);
        sb.append("Value must be a decimal");
        String sep = " with ";

        if (minBefore>0 || maxBefore<10) {
            sb.append(sep);
            if (minBefore==maxBefore) sb.append("exactly "+minBefore+" digit"+(minBefore!=1? "s": ""));
            else sb.append(minBefore+"-"+maxBefore+" digits");
            sb.append(" before the decimal");
            sep = " and ";
        }

        if (minAfter>0 || maxAfter<10) {
            sb.append(sep);
            if (minAfter==maxAfter) sb.append("exactly "+minAfter+" digit"+(minAfter!=1? "s": ""));
            else sb.append(minAfter+"-"+maxAfter+" digits");
            sb.append(" after the decimal");
            sep = " and ";
        }

        return sb.toString();
    }
}

/*
 * $Log: DecimalValidator.java,v $
 */
