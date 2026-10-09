package org.barracudamvc.core.forms.parsers.formatProviders;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;

public class YearFirstDateParser implements DateFormatProvider {

    private final SimpleDateFormatFactory format;

    public YearFirstDateParser(String description) {
        format = new SimpleDateFormatFactory(description);
    }

    @Override
    public DateFormat getDateFormat(Locale locale) {
        return new DateFormatWrapper(format.getDateFormat(locale));
    }

    private static class DateFormatWrapper extends DateFormat {

        DateFormat myFormmater;

        public DateFormatWrapper(DateFormat myFormmater) {
            this.myFormmater = myFormmater;
        }

        @Override
        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
            return myFormmater.format(date, toAppendTo, fieldPosition);
        }

        @Override
        public Date parse(String source, ParsePosition pos) {
            return myFormmater.parse(source, pos);
        }

        @Override
        public Date parse(String source) throws ParseException {
            if (source == null) {
                throw new ParseException("Date was null", 0);
            }

            if (source.indexOf('-') < 3) {
                throw new ParseException("Only 4 digit years are supported", source.indexOf('-'));
            } else {
                return myFormmater.parse(source);
            }

        }
    }
}
