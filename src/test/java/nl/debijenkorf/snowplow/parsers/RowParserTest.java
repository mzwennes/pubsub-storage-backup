package nl.debijenkorf.snowplow.parsers;

import nl.debijenkorf.snowplow.values.DataType;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class RowParserTest {

    @Test
    public void test_boolean_parsing() {
        RowParser parser = new RowParser();
        assertThat(parser.parse("1", DataType.BOOLEAN), is(true));
        assertThat(parser.parse("0", DataType.BOOLEAN), is(false));
        assertThat(parser.parse("", DataType.BOOLEAN), is(false));
    }

    @Test
    public void test_integer_parsing() {
        RowParser parser = new RowParser();
        assertThat(parser.parse("1", DataType.INTEGER), is(1));
    }

}