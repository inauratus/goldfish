/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.forms.parsers;

import java.util.Locale;
import org.barracudamvc.core.forms.ParseException;

public class FileFieldFormType implements FileElementParser<FileField> {

    @Override
    public FileField parse(FileField field, Locale locale) throws ParseException {
        return field;
    }

    @Override
    public FileField[] getTypeArray(int size) {
        return new FileField[size];
    }

    @Override
    public Class<FileField> getFormClass() {
        return FileField.class;
    }
}
