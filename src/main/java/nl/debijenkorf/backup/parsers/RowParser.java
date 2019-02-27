package nl.debijenkorf.backup.parsers;

import nl.debijenkorf.backup.values.DataType;

import java.io.Serializable;

public class RowParser implements Serializable {

    public Object parse(String value, DataType type) {
        if (type.equals(DataType.BOOLEAN)) {
            return value.equals("1");
        } else if (type.equals(DataType.FLOAT64)) {
            return Float.parseFloat(value);
        } else if (type.equals(DataType.INTEGER)) {
            return Integer.parseInt(value);
        } else {
            return value;
        }
    }

}
