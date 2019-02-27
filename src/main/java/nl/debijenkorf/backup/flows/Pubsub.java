package nl.debijenkorf.backup.flows;

import lombok.Builder;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Duration;

@Builder
public class Pubsub {

    private Pipeline pipeline;
    private String subscription;
    private int window;

    public PCollection<String> read() {
        return pipeline.apply(PubsubIO.readStrings()
                .fromSubscription(subscription))
                .apply(Window.into(FixedWindows
                        .of(Duration.standardSeconds(window))));
    }

}