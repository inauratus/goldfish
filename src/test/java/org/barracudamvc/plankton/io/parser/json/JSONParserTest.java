package org.barracudamvc.plankton.io.parser.json;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.barracudamvc.plankton.io.parser.json.parser.DefaultBuilder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class JSONParserTest {

    JSONParser parser;

    @Test(expected = Exception.class)
    public void givenIllegalFirstCharacter_expectException() {
        parse("abcd");
    }

    @Test()
    public void givenEmptyStreamUsingDefaultBuilderExpect_emptyMap() {
        Object result = parse("");

        assertThat(result, instanceOf(Map.class));
        Map map = (Map) result;
        assertThat(map.size(), is(0));
    }

    @Test
    public void givenOpenCloseBrases_expectEmptyMap() {
        Object result = parse("{}");

        assertThat(result, instanceOf(Map.class));
        Map map = (Map) result;
        assertThat(map.size(), is(0));
    }

    @Test
    public void givenOpenCloseBracket_expectEmptyList() {
        Object result = parse("[]");

        assertThat(result, instanceOf(List.class));
        List list = (List) result;
        assertThat(list.size(), is(0));
    }

    @Test
    public void givenEmptyArrayOfArrays_expectEmptyListOfList() {
        Object result = parse("[[]]");

        assertThat(result, instanceOf(List.class));
        List list = (List) result;
        assertThat(list.size(), is(1));
    }

    @Test
    public void givenEmptyArrayOfArraysSingleValue_expectEmptyListOfList() {
        Object result = parse("[[null]]");

        assertThat(result, instanceOf(List.class));
        List list = (List) result;
        assertThat(list.size(), is(1));
    }

    @Test
    public void givenArrayWithNull_expectAListContainingNull() {
        Object result = parse("[null]");

        assertThat(result, instanceOf(List.class));
        List<String> list = (List) result;
        assertThat(list.size(), is(1));
        assertThat(list, hasItem((String) null));
    }

    @Test
    public void givenAnArrayOfStrings_expectAListOfStrings() {
        Object result = parse("[\"a\", \"b\", \"c\"]");

        assertThat(result, instanceOf(List.class));
        List list = (List) result;
        assertThat(list.size(), is(3));
    }

    @Test
    public void givenAnUTFCharacterStrings_expectAListOfStrings() {
        Object result = parse("[\"\u2022\", \"b\", \"c\"]");
        ArrayList<String> list = new ArrayList<>();
        list.add("\u2022");
        list.add("b");
        list.add("c");
        assertThat(result, instanceOf(List.class));
        assertThat(result, is(list));
    }

    @Test(expected = Exception.class)
    public void givenAlmostNull() {
        Object result = parse("[na]");

        assertThat(result, instanceOf(List.class));
    }

    @Test
    public void givenArrayWithTrue_expectAListContainingTrue() {
        Object result = parse("[true]");

        assertThat(result, instanceOf(List.class));
        List<String> list = (List) result;
        assertThat(list.size(), is(1));
        assertThat(list, hasItem("true"));
    }

    @Test
    public void givenArrayWithFalse_expectAListContainingFalse() {
        Object result = parse("[false]");

        assertThat(result, instanceOf(List.class));
        List<String> list = (List) result;
        assertThat(list.size(), is(1));
        assertThat(list, hasItem("false"));
    }

    @Test
    public void givenArrayOfReserved_expectListOfReserved() {
        Object result = parse("[null, null, true, false]");

        assertThat(result, instanceOf(List.class));
        List<String> list = (List) result;
        assertThat(list.size(), is(4));
        assertThat(list, hasItems(null, null, "true", "false"));
    }

    @Test
    public void givenArrayOfArrayOfNull_expectListOfListOfNull() {
        Object result = parse("[[null], [null], [null]]");
        ArrayList RESULT_LIST = new ArrayList();
        RESULT_LIST.add(null);

        assertThat(result, instanceOf(List.class));
        List<ArrayList> list = (List) result;
        assertThat(list.size(), is(3));
        assertThat(list, hasItems(RESULT_LIST, RESULT_LIST, RESULT_LIST));
    }

    @Test
    public void givenDeepArray() {
        Object result = parse("[[[null]], [null], [null]]");
        ArrayList RESULT_LIST_1 = new ArrayList();
        ArrayList RESULT_LIST = new ArrayList();
        RESULT_LIST.add(null);
        RESULT_LIST_1.add(RESULT_LIST);

        assertThat(result, instanceOf(List.class));
        List<ArrayList> list = (List) result;
        assertThat(list.size(), is(3));
        assertThat(list, hasItems(RESULT_LIST_1, RESULT_LIST, RESULT_LIST));
    }

    private Object parse(String stream) {
        return parser.parse(toStream(stream), new DefaultBuilder());
    }

    private ByteArrayInputStream toStream(String stream) {
        return new ByteArrayInputStream(stream.getBytes());
    }

    @Before
    public void setup() {
        parser = new JSONParser();
    }

}
