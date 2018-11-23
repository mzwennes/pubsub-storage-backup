package nl.debijenkorf.snowplow.flows;

import lombok.Builder;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Duration;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Builder
public class Pubsub implements Flow<String> {

    private Pipeline pipeline;
    private String subscription;
    private int window;

    @Override
    public PCollection<String> read() {
        return pipeline.apply(PubsubIO.readStrings()
                .fromSubscription(subscription))
                .apply(Window.into(FixedWindows
                        .of(Duration.standardSeconds(window))));
    }

    @Override
    public void write(PCollection<String> payload) {
        throw new NotImplementedException();
    }
}