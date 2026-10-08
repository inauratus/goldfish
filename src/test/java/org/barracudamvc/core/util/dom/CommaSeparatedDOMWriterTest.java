package org.barracudamvc.core.util.dom;

import org.junit.Test;

import static org.junit.Assert.*;

public class CommaSeparatedDOMWriterTest {


    CommaSeparatedDOMWriter writer = new CommaSeparatedDOMWriter();

    @Test
    public void canRemoveCommaInNumberString() {
        String numWithComma = "5,000";
        assertEquals("5000", writer.removeComma(numWithComma));
    }

    @Test
    public void canTrimEnds() {
        String extraSpace = " 58 ";
        assertEquals("58", writer.trim(extraSpace));
    }

    @Test
    public void canEqualsSymbolTriggersAddingSingleQuote() {
        String equalsSign = "=";
        assertEquals("'=", writer.escapeFormulaSymbols(equalsSign));
    }

    @Test
    public void checkIsValidNegativeNumber() {
        String negative2 = "-2";
        assertTrue(writer.isValidNegativeNumber(negative2));
    }

    @Test
    public void checkIsInvalidNegativeNumber() {
        String negative = "-";
        String withLetter = "-F";
        assertFalse(writer.isValidNegativeNumber(negative));
        assertFalse(writer.isValidNegativeNumber(withLetter));
    }

    @Test
    public void checkNegativeCurrencyNumberIsValid() {
        String negative = "-$50";
        assertTrue(writer.isValidNegativeNumber(negative));
    }

    @Test
    public void checkDoubleNegativeIsNotValid() {
        String doubleNegative = "--5";
        assertFalse(writer.isValidNegativeNumber(doubleNegative));
    }

    @Test
    public void checkDecimalNumberIsValid() {

        String negative = "-5.5";
        assertTrue(writer.isValidNegativeNumber(negative));
    }

    @Test
    public void checkDecimalCurrencyIsValid() {
        String negative = "-$5.5";
        assertTrue(writer.isValidNegativeNumber(negative));
    }

    @Test
    public void canRemoveCurrencySymbol() {
        String dollarAmt = "$5";
        String otherNumber = "55";

        assertEquals("5", writer.removeCurrencySymbol(dollarAmt));
        assertEquals("55", writer.removeCurrencySymbol(otherNumber));
    }

    @Test
    public void canRemovePercentSymbol() {
        String withPercent = "5%";
        String justPercent = "%";
        assertEquals("5", writer.removePercentSign(withPercent));
        assertEquals("", writer.removePercentSign(justPercent));
    }

    @Test
    public void canAddQuoteToBeginningOfPossibleFormulas() {
        String str = "@500";
        String expectedStr = "'@500";
        String doubleNegative = "--5";
        String expectedDoubleNegative = "'--5";
        String equals = "=532";
        String expectedEquals = "'=532";

        if (!writer.isValidNegativeNumber(str)) {
            str = writer.escapeFormulaSymbols(str);
        }

        if (!writer.isValidNegativeNumber(doubleNegative)) {
            doubleNegative = writer.escapeFormulaSymbols(doubleNegative);
        }

        if (!writer.isValidNegativeNumber(equals)) {
            equals = writer.escapeFormulaSymbols(equals);
        }

        assertEquals(expectedStr, str);
        assertEquals(expectedDoubleNegative, doubleNegative);
        assertEquals(expectedEquals, equals);

    }

    @Test
    public void doNotAddQuoteToBeginningOfRegularField() {
        String str = "-$500";
        String expectedStr = "-$500";

        String negative = "-500";
        String expectedNegative = "-500";

        if (!writer.isValidNegativeNumber(str)) {
            str = writer.escapeFormulaSymbols(str);
        }

        if (!writer.isValidNegativeNumber(negative)) {
            negative = writer.escapeFormulaSymbols(negative);
        }

        assertEquals(expectedStr, str);
        assertEquals(expectedNegative, negative);
    }
}