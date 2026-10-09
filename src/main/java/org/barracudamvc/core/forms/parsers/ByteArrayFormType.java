package org.barracudamvc.core.forms.parsers;

import java.io.IOException;
import java.util.Locale;
import org.barracudamvc.core.forms.ParseException;
import org.barracudamvc.core.forms.exception.FileNotParsableException;
import org.barracudamvc.plankton.io.StreamUtils;

public class ByteArrayFormType implements FileElementParser<byte[]> {

    @Override
    public byte[] parse(FileField field, Locale locale) throws ParseException {
        try {
            return StreamUtils.readIntoByteArray(field.getInputStream());
        } catch (IOException ex) {
            throw new FileNotParsableException("The file provided is not accessible", ex);
        }
    }

    @Override
    public byte[][] getTypeArray(int size) {
        return new byte[size][];
    }

    @Override
    public Class<byte[]> getFormClass() {
        return byte[].class;
    }
}
