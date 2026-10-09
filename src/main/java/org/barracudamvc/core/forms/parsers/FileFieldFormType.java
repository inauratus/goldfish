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
