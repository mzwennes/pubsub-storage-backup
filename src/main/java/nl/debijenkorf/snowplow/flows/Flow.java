package nl.debijenkorf.snowplow.flows;

import org.apache.beam.sdk.values.PCollection;

public interface Flow<T> {
    PCollection<T> read();
    void write(PCollection<T> payload);
}
