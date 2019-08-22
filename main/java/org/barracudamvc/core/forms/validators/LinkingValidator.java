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
 * $Id: LinkingValidator.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.forms.validators;

import java.util.*;

// 3rd-party imports:
import org.barracudamvc.core.forms.*;


/**
 * Ensures that if any element in a list of elements contains a non-null value that all the other
 * elements also be non-null.
 *
 * @author christianc@atmreports.com
 * @since csc_110304_1
 */
public class LinkingValidator extends DefaultFormValidator {

    protected String keyField;
    protected String[] fields;
    protected String customErrorMessage;

    /**
     * Define a LinkingValidator where if any of the fields is non-null, then all the fields
     * must be non null
     */
    public LinkingValidator(String[] ifields) {
        this(null, ifields);
    }

    /**
     * Define a LinkingValidator where a group of fields are required only when a specific "key"
     * field is not null.
     */
    public LinkingValidator(String ikeyField, String[] ifields) {
        this.keyField = ikeyField;
        this.fields = ifields;
    }

    public LinkingValidator setErrorMessage(String customErrorMessage) {
        this.customErrorMessage = customErrorMessage;
        return this;
    }

    public void validateForm(FormMap map, boolean deferExceptions) throws ValidationException {
        FormMapper mapper = map.getFormMapper();

        //if we are dealing with a UIDMapper, we need to iterate through all ids,
        //and adjust the key fields for each of them
        if (mapper instanceof UIDFormMapper) {
            DeferredValidationException dve = null;
            UIDFormMapper uidMapper = (UIDFormMapper) mapper;
            Iterator it = uidMapper.getMappedUIDs().iterator();
            while (it.hasNext()) {
                //adjust the names for the specific uid
                String uid = (String) it.next();
                String keyField2 = keyField;
                if (keyField != null) {
                    FormElement el = uidMapper.getElementByUID(map, keyField, uid);
                    if (el != null) keyField2 = el.getKey();
                }
                String fields2[] = new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    FormElement el = uidMapper.getElementByUID(map, fields[i], uid);
                    if (el != null) fields2[i] = el.getKey();
                    else fields2[i] = fields[i];
                }

                //now check it (note that we need to accumulate exceptions since
                //we are doing this over multiple uid elements in the form)
                try {
                    _checkFields(map, keyField2, fields2, deferExceptions);
                } catch (DeferredValidationException dve1) {
                    if (dve == null)
                        dve = dve1;
                    else
                        dve.addSubException(dve1);
                }
            }
            if (dve != null) throw dve;

        } else {
            _checkFields(map, keyField, fields, deferExceptions);
        }
    }

    protected void _checkFields(FormMap map, String ikeyField, String[] fields, boolean deferExceptions) throws ValidationException {
        boolean needCheck = false;

        if (ikeyField != null) {
            FormElement keyElem = map.getElement(ikeyField);
            Object val = keyElem.getVal();
            if (this.isNull(val, keyElem))
                return;
            needCheck = true;

        } else {
            for (String field : fields) {
                FormElement elem = map.getElement(field);
                Object val = elem.getVal();
                if (!this.isNull(val, elem)) {
                    needCheck = true;
                    break;
                }
            }
        }

        if (needCheck) {
            NotNullValidator nnv = new NotNullValidator();
            nnv.setErrorMessage(customErrorMessage);
            DeferredValidationException dve = null;

            FormElement elem;
            for (String field : fields) {
                try {
                    elem = map.getElement(field);
                    nnv.validate(elem, map, deferExceptions);
                } catch (DeferredValidationException dve1) {
                    if (dve == null)
                        dve = dve1;
                    else
                        dve.addSubException(dve1);
                }
            }

            if (dve != null)
                throw dve;
        }
    }
}
