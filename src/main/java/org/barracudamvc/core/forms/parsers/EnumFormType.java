package org.barracudamvc.core.forms.parsers;

import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ParseException;

import java.lang.reflect.Array;
import java.util.Locale;

/**
 * Created by ben.potter on 3/31/2016.
 */
public class EnumFormType<T extends Enum> extends FormType<T> {
    private final Class<T> clazz;

    public EnumFormType(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Class<T> getFormClass() {
        return clazz;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T[] getTypeArray(int size) {
        return (T[]) Array.newInstance(clazz, size);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T parse(String origVal, Locale loc) throws ParseException {
        if (origVal == null || origVal.isEmpty()) return null;
        try {
            return (T) Enum.valueOf(clazz, origVal);
        } catch (IllegalArgumentException e) {
            throw new ParseException(e);
        }
    }

}
