package nl.debijenkorf.snowplow;

import org.apache.beam.sdk.options.*;

public interface Options extends PipelineOptions, StreamingOptions {
    @Description("The Cloud Pub/Sub topic to read from.")
    @Validation.Required
    ValueProvider<String> getInputTopic();
    void setInputTopic(ValueProvider<String> value);

    @Description("The directory to output files to. Must end with a slash.")
    @Validation.Required
    ValueProvider<String> getOutputDirectory();
    void setOutputDirectory(ValueProvider<String> value);

    @Description("The filename prefix of the files to write to.")
    @Default.String("backup-")
    @Validation.Required
    ValueProvider<String> getOutputFilenamePrefix();
    void setOutputFilenamePrefix(ValueProvider<String> value);

    @Description("The suffix of the files to write.")
    @Default.String("")
    ValueProvider<String> getOutputFilenameSuffix();
    void setOutputFilenameSuffix(ValueProvider<String> value);

    @Description("The shard template of the output file. Specified as repeating sequences "
            + "of the letters 'S' or 'N' (example: SSS-NNN). These are replaced with the "
            + "shard number, or number of shards respectively")
    @Default.String("W-P-SS-of-NN")
    ValueProvider<String> getOutputShardTemplate();
    void setOutputShardTemplate(ValueProvider<String> value);

    @Description("The maximum number of output shards produced when writing.")
    @Default.Integer(1)
    Integer getNumShards();
    void setNumShards(Integer value);

    @Description("The window duration in seconds for aggregation of PubSub data.")
    @Default.Integer(120)
    Integer getWindowDuration();
    void setWindowDuration(Integer value);
}