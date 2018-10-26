package nl.debijenkorf.snowplow;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.joda.time.Duration;

public class BeamApplication {

    public static void main(String[] args) {
        Options options = PipelineOptionsFactory
                .fromArgs(args)
                .withValidation()
                .as(Options.class);

        PipelineResult result = run(options);
        result.waitUntilFinish();
    }

    private static PipelineResult run(Options options) {
        Pipeline pipeline = Pipeline.create(options);

        pipeline.apply(PubsubIO.readStrings()
                .fromSubscription(options.getInputTopic()))
                .apply(Window.into(FixedWindows.of(Duration.standardSeconds(options.getWindowDuration()))))
                .apply(TextIO.write()
                        .to(generateFilePattern(options))
                        .withWindowedWrites()
                        .withNumShards(options.getNumShards()));
        return pipeline.run();
    }

    private static WindowedFilenamePolicy generateFilePattern(Options options) {
        return new WindowedFilenamePolicy(
                options.getOutputDirectory(),
                options.getOutputFilenamePrefix(),
                options.getOutputShardTemplate(),
                options.getOutputFilenameSuffix());
    }

}
