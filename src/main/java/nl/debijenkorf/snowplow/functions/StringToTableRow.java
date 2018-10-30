package nl.debijenkorf.snowplow.functions;

import com.google.api.services.bigquery.model.TableRow;
import nl.debijenkorf.snowplow.parsers.RowParser;
import nl.debijenkorf.snowplow.values.Field;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StringToTableRow extends DoFn<String, TableRow> {

    private static final Logger logger = LoggerFactory.getLogger(StringToTableRow.class);

    private final String separator;
    private final List<Field> fields;
    private final RowParser parser;

    public StringToTableRow(String separator, List<Field> fields, RowParser parser) {
        this.separator = separator;
        this.fields = fields;
        this.parser = parser;
    }

    @ProcessElement
    public void processElement(ProcessContext context) {
        String input = context.element();
        String[] separated = input.split(this.separator);

        // return failsafe object if headers do not match
        if (separated.length != fields.size()) {
            //context.output(input);
            logger.error("Input length and given schema length do not equal. " +
                    "Size of input length: {}, size of schema: {}",
                    separated.length, fields.size());
        }
        context.output(generateTableRow(separated));
    }

    /**
     *
     * @param separated (example: ["a", "1", "2018-10-01"])
     * @return a BigQuery TableRow object
     */
    private TableRow generateTableRow(String[] separated) {
        TableRow row = new TableRow();
        for (int i = 0; i < separated.length; i++) {

            String value = separated[i];
            if (value.equals("")) continue;

            Field field = fields.get(i);
            row.set(field.getName(), parser.parse(value, fields.get(i).getType()));
        }

        return row;
    }
}