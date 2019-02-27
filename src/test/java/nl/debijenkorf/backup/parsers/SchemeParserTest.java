package nl.debijenkorf.backup.parsers;

import nl.debijenkorf.backup.values.DataType;
import nl.debijenkorf.backup.values.Field;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class SchemeParserTest {

    @Test
    public void test_single_field_in_scheme() {
        String scheme = "name:string";
        List<Field> single = SchemeParser.parse(scheme);
        assertThat(single.size(), is(1));
        assertThat(single.get(0).getName(), is("name"));
        assertThat(single.get(0).getType(), is(DataType.STRING));
    }

}