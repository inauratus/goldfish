package org.barracudamvc.core.forms.parsers;

import org.barracudamvc.core.forms.ParseException;

public class ShortFormType extends WholeNumberFormType<Short> implements NumberComparator {

    @Override
    public Class<Short> getFormClass() {
        return Short.class;
    }

    @Override
    protected Short narrow(Long value) throws ParseException {
        validateBounds(value, (long) Short.MAX_VALUE, (long) Short.MIN_VALUE);
        return value.shortValue();
    }

    @Override
    public Short[] getTypeArray(int size) {
        return new Short[size];
    }

    public int compare(Number o1, Number o2) {
        short n1 = o1.shortValue();
        short n2 = o2.shortValue();

        if (n1 > n2) {
            return 1;
        } else if (n1 < n2) {
            return -1;
        } else {
            return 0;
        }
    }
}
