package nl.debijenkorf.snowplow.flows;

import lombok.Builder;
import nl.debijenkorf.snowplow.functions.StringToTableRow;
import nl.debijenkorf.snowplow.parsers.RowParser;
import nl.debijenkorf.snowplow.parsers.SchemeParser;
import nl.debijenkorf.snowplow.values.Field;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

@Builder
public class BigQuery implements Flow<String> {

    private String table;
    private List<Field> fields;
    private String separator;

    @Builder.Default
    private RowParser parser = new RowParser();

    @Override
    public PCollection<String> read() {
        throw new NotImplementedException();
    }

    @Override
    public void write(PCollection<String> payload) {
        payload.apply("Convert msg to Table row",
                ParDo.of(new StringToTableRow(separator, fields, parser)))
                .apply(BigQueryIO.writeTableRows()
                        .to(table)
                        .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
                        .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND)
                        .withSchema(SchemeParser.toTableSchema(fields)));
    }
}
