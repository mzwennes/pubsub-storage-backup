package nl.debijenkorf.snowplow.flows;

import lombok.Builder;
import nl.debijenkorf.snowplow.utils.WindowedFilenamePolicy;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.values.PCollection;

@Builder
public class Storage {

    private int shards;
    private PCollection<String> source;
    private WindowedFilenamePolicy filePattern;

    public void write() {
        source.apply(TextIO.write()
                .to(filePattern)
                .withWindowedWrites()
                .withNumShards(shards));
    }

}
