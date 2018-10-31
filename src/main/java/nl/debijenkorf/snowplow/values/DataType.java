package nl.debijenkorf.snowplow.values;

public enum DataType {
    STRING("STRING"),
    CHAR("STRING"),
    DOUBLE("FLOAT64"),
    BOOLEAN("BOOLEAN"),
    FLOAT64("FLOAT64"),
    DATETIME("DATETIME"),
    INTEGER("INTEGER");

    private String type;

    DataType(String type) {
        this.type = type;
    }

    public String inner() {
        return type;
    }
}
