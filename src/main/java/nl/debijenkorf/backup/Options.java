package nl.debijenkorf.backup;

import org.apache.beam.sdk.options.*;

public interface Options extends PipelineOptions, StreamingOptions {
    @Description("The Cloud Pub/Sub subscription to read from.")
    @Validation.Required
    String getSubscription();
    void setSubscription(String value);

    @Description("Pick the sink to write to. supported values: bigquery, storage")
    @Validation.Required
    String getSink();
    void setSink(String value);

    @Description("The directory to output files to. Must end with a slash.")
    @Validation.Required
    ValueProvider<String> getOutputDirectory();
    void setOutputDirectory(ValueProvider<String> value);

    @Description("The prefix of the files to write")
    @Default.String("")
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

    @Description("The Google Project ID")
    @Validation.Required
    ValueProvider<String> getProjectId();
    void setProjectId(ValueProvider<String> value);

    @Description("The BigQuery dataset name")
    ValueProvider<String> getDatasetId();
    void setDatasetId(ValueProvider<String> value);

    @Description("The BigQuery table name")
    ValueProvider<String> getTableName();
    void setTableName(ValueProvider<String> value);

    @Description("Definition of the source data in Pubsub. (example: name:string,age:integer,price:float) "
    + "Allowed types: string, integer, float, long")
    String getSourceScheme();
    void setSourceScheme(String value);

    @Description("Separator of the incoming Pubsub data (examples: ,:;\t)")
    String getSeparator();
    void setSeparator(String value);
}