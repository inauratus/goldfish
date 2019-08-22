/*
 * Copyright (C) 2014 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: ByteArrayFormType.java 
 * Created: Jul 2, 2014 4:20:59 PM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.core.forms.parsers;

import java.io.IOException;
import java.util.Locale;
import org.barracudamvc.core.forms.ParseException;
import org.barracudamvc.core.forms.exception.FileNotParsableException;
import org.barracudamvc.plankton.io.StreamUtils;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
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
