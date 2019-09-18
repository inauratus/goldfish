/*
 * Copyright (C) 2003  Chris Webb <chris.webb@voxsurf.com>
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
 * $Id: FormType.java 270 2014-07-15 15:36:03Z charleslowery $
 */
package org.barracudamvc.core.forms;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import org.barracudamvc.core.forms.parsers.BigDecimalFormType;
import org.barracudamvc.core.forms.parsers.BooleanFormType;
import org.barracudamvc.core.forms.parsers.DateFormType;
import org.barracudamvc.core.forms.parsers.DefaultFormElementParser;
import org.barracudamvc.core.forms.parsers.DoubleFormType;
import org.barracudamvc.core.forms.parsers.FileElementParser;
import org.barracudamvc.core.forms.parsers.FileField;
import org.barracudamvc.core.forms.parsers.FileFieldFormType;
import org.barracudamvc.core.forms.parsers.FloatFormType;
import org.barracudamvc.core.forms.parsers.IntegerFormType;
import org.barracudamvc.core.forms.parsers.LongFormType;
import org.barracudamvc.core.forms.parsers.ShortFormType;
import org.barracudamvc.core.forms.parsers.StringFormType;
import org.barracudamvc.core.forms.parsers.TimeFormType;
import org.barracudamvc.core.forms.parsers.TimestampFormType;

/**
 * This class defines all valid FormTypes
 * 
 * @author  Chris Webb <chris.webb@voxsurf.com>
 * @author  Diez Roggisch <diez.roggisch@artnology.com>
 * @author  Iman L. Crawford <icrawford@greatnation.com>
 * @author  Christian Cryder <christianc@granitepeaks.com>
 * @author  Jacob Kjome <hoju@visi.com>
 * @version %I%, %G%
 * @since   1.0
 */
public abstract class FormType<T> extends DefaultFormElementParser<T> {

    public static FormType<String> STRING = new StringFormType();
    public static FormType<Boolean> BOOLEAN = new BooleanFormType();
    public static FormType<Integer> INTEGER = new IntegerFormType();
    public static FormType<Long> LONG = new LongFormType();
    public static FormType<Short> SHORT = new ShortFormType();
    public static FormType<Double> DOUBLE = new DoubleFormType();
    public static FormType<Float> FLOAT = new FloatFormType();
    public static FormType<BigDecimal> BIG_DECIMAL = new BigDecimalFormType();
    public static FormType<Date> DATE = new DateFormType();
    public static FormType<Timestamp> TIMESTAMP = new TimestampFormType();
    public static FormType<Time> TIME = new TimeFormType();
    public static FileElementParser<FileField> FILE = new FileFieldFormType();

    /**
     * Protected constructor to prevent external instantiation. Cannot be
     * private because we would be unable to call the constructor from a
     * sub-class.
     */
    protected FormType() {
    }

}
