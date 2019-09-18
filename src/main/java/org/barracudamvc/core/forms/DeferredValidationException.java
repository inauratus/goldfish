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
 * $Id: DeferredValidationException.java 251 2012-11-09 18:49:25Z charleslowery $
 */
package org.barracudamvc.core.forms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class defines a deferred validation exception. It's
 * like a standard validation exception, except that validation
 * continues (so that we can collect all possible validation
 * errors on a form at one time), and that it only contains subexceptions.
 */
public class DeferredValidationException extends ValidationException implements Iterable<ValidationException> {

    private static final long serialVersionUID = 1L;
    protected List<ValidationException> subExceptions = new ArrayList<ValidationException>();

    /**
     * The noargs public contructor for DeferredValidationException
     */
    public DeferredValidationException() {
        super();
    }

    public DeferredValidationException(ValidationException ve) {
        super();
        addSubException(ve);
    }

    public DeferredValidationException(DeferredValidationException dve) {
        super();
        addSubException(dve);
    }

    public void addSubException(ValidationException ve) {
        if (ve instanceof DeferredValidationException) {
            this.subExceptions.addAll(((DeferredValidationException) ve).getSubExceptions());
        } else if (ve != null) {
            this.subExceptions.add(ve);
        }
    }

    public List<ValidationException> getSubExceptions() {
        return this.subExceptions;
    }

    public boolean hasSubExceptions() {
        return subExceptions.size() > 0;
    }

    @Override
    public Iterator<ValidationException> iterator() {
        return subExceptions.iterator();
    }

    @Override
    public String getMessage() {
        StringBuilder messageBuilder = new StringBuilder();
        String seperator = "";
        for (Exception exception : this) {
            messageBuilder.append(seperator);
            messageBuilder.append(exception.getMessage());
            seperator = " & ";
        }
        return messageBuilder.toString();
    }
}
