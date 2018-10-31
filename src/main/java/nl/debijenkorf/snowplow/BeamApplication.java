package nl.debijenkorf.snowplow;

import nl.debijenkorf.snowplow.coders.FailsafeElementCoder;
import nl.debijenkorf.snowplow.flows.BigQuery;
import nl.debijenkorf.snowplow.flows.Pubsub;
import nl.debijenkorf.snowplow.flows.Storage;
import nl.debijenkorf.snowplow.parsers.SchemeParser;
import nl.debijenkorf.snowplow.utils.WindowedFilenamePolicy;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.CoderRegistry;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessageWithAttributesCoder;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

public class BeamApplication {

    public static void main(String[] args) {
        Options options = PipelineOptionsFactory
                .fromArgs(args)
                .withValidation()
                .as(Options.class);

        Pipeline pipeline = Pipeline.create(options);
        registerCoder(pipeline);
        setup(pipeline, options);
        pipeline.run().waitUntilFinish();
    }

    private static void setup(Pipeline pipeline, Options options) {
        Pubsub source = Pubsub.builder()
                .subscription(options.getSubscription())
                .window(options.getWindowDuration())
                .pipeline(pipeline)
                .build();

        if (options.getSink().equals("storage")) {
            Storage.builder()
                    .source(source.read())
                    .filePattern(filePattern(options))
                    .shards(options.getNumShards())
                    .build().write();
        } else if (options.getSink().equals("bigquery")) {
            BigQuery.builder()
                    .source(source.read())
                    .separator(options.getSeparator())
                    .fields(SchemeParser.parse(options.getSourceScheme()))
                    .table(generateTableName(options))
                    .build().write();
        }
    }

    private static void registerCoder(Pipeline pipeline) {
        FailsafeElementCoder<PubsubMessage, String> coder =
                FailsafeElementCoder.of(PubsubMessageWithAttributesCoder.of(), StringUtf8Coder.of());

        CoderRegistry coderRegistry = pipeline.getCoderRegistry();
        coderRegistry.registerCoderForType(coder.getEncodedTypeDescriptor(), coder);
    }

    private static String generateTableName(Options options) {
        return String.format("%s:%s.%s",
                options.getProjectId(), options.getDatasetId(), options.getTableName()
        );
    }

    private static WindowedFilenamePolicy filePattern(Options options) {
        return new WindowedFilenamePolicy(
                options.getOutputDirectory(),
                options.getOutputFilenamePrefix(),
                options.getOutputShardTemplate(),
                options.getOutputFilenameSuffix());
    }

}
