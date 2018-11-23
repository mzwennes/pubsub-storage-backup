package nl.debijenkorf.snowplow.flows;

import nl.debijenkorf.snowplow.Options;
import nl.debijenkorf.snowplow.parsers.SchemeParser;
import nl.debijenkorf.snowplow.utils.WindowedFilenamePolicy;

import java.util.Optional;

public class FlowFactory {
    /**
     * Tries to initialize a flow based on the given name
     * @param flowName name of the flow (e.g. bigquery, storage)
     * @return either a existing flow with a read/write function or an empty optional
     */
    public Optional<Flow> getFlow(String flowName, Options options) {
        switch (flowName) {
            case "storage":
                return Optional.of(storage(options));
            case "biquery":
                return Optional.of(bigQuery(options));
            case "pubsub":
                return Optional.of(pubsub(options));
            default:
                return Optional.empty();
        }
    }

    private Storage storage(Options options) {
        return Storage.builder()
                .filePattern(filePattern(options))
                .shards(options.getNumShards())
                .build();
    }

    private BigQuery bigQuery(Options options) {
        return BigQuery.builder()
                .separator(options.getSeparator())
                .fields(SchemeParser.parse(options.getSourceScheme()))
                .table(generateTableName(options))
                .build();
    }

    private Pubsub pubsub(Options options) {
        return Pubsub.builder()
                .subscription(options.getSubscription())
                .window(options.getWindowDuration())
                .build();
    }


    private static String generateTableName(Options options) {
        return String.format("%s:%s.%s",
                options.getProjectId(), options.getDatasetId(), options.getTableName()
        );
    }

    private static WindowedFilenamePolicy filePattern(Options options) {
        return new WindowedFilenamePolicy(
                options.getOutputDirectory(),
                options.getOutputShardTemplate(),
                options.getOutputFilenameSuffix());
    }
}
