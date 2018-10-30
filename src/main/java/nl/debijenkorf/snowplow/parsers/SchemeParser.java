package nl.debijenkorf.snowplow.parsers;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableSchema;
import nl.debijenkorf.snowplow.values.DataType;
import nl.debijenkorf.snowplow.values.Field;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SchemeParser {

    /**
     * Parse a string based scheme to a list of fields each containing
     * a name and a valid BigQuery data type
     *
     * @param scheme user given schema (example: name:string,age:integer)
     */
    public static List<Field> parse(String scheme) {
        return Arrays.stream(scheme.split(","))
                .filter(x -> !x.isEmpty())
                .map(pair -> {
                    String[] keyVal = pair.split(":");
                    return new Field(keyVal[0], DataType.valueOf(keyVal[1].toUpperCase()));
                }).collect(Collectors.toList());
    }

    /**
     * Converts the given list of fields to a BigQuery schema
     * @return a valid BigQuery TableSchema with NULLABLE types
     */
    public static TableSchema toTableSchema(List<Field> fields) {
        List<TableFieldSchema> schema = fields.stream()
                .map(field -> {
                    TableFieldSchema tfs = new TableFieldSchema();
                    tfs.setName(field.getName())
                            .setType(field.getType().inner())
                            .setMode("NULLABLE");
                    return tfs;
                }).collect(Collectors.toList());
        return new TableSchema().setFields(schema);
    }

}
