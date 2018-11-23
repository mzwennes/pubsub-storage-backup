package nl.debijenkorf.snowplow;

import nl.debijenkorf.snowplow.coders.FailsafeElementCoder;
import nl.debijenkorf.snowplow.flows.Flow;
import nl.debijenkorf.snowplow.flows.FlowFactory;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.CoderRegistry;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessageWithAttributesCoder;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.values.PCollection;

import java.util.Optional;

public class BeamApplication {

    public static void main(String[] args) {
        Options options = PipelineOptionsFactory
                .fromArgs(args)
                .withValidation()
                .as(Options.class);

        Pipeline pipeline = Pipeline.create(options);
        registerCoder(pipeline);

        // init factory to allow creation of sink/sources (flows)
        FlowFactory flowFactory = new FlowFactory();

        // main data flow from source -> sink
        Optional<Flow> source = flowFactory.getFlow(options.getSource(), options);

        source.ifPresent(src -> {
            PCollection input = src.read();
            Optional<Flow> sink = flowFactory.getFlow(options.getSink(), options);
            sink.ifPresent(snk -> snk.write(input));
        });

        pipeline.run().waitUntilFinish();
    }

    /**
     * BigQuery requires coders for custom data types.
     * @param pipeline
     */
    private static void registerCoder(Pipeline pipeline) {
        FailsafeElementCoder<PubsubMessage, String> coder =
                FailsafeElementCoder.of(PubsubMessageWithAttributesCoder.of(), StringUtf8Coder.of());

        CoderRegistry coderRegistry = pipeline.getCoderRegistry();
        coderRegistry.registerCoderForType(coder.getEncodedTypeDescriptor(), coder);
    }

}
