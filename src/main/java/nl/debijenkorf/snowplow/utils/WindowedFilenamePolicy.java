package nl.debijenkorf.snowplow.utils;

import org.apache.beam.sdk.io.DefaultFilenamePolicy;
import org.apache.beam.sdk.io.FileBasedSink.FilenamePolicy;
import org.apache.beam.sdk.io.FileBasedSink.OutputFileHints;
import org.apache.beam.sdk.io.FileSystems;
import org.apache.beam.sdk.io.fs.ResolveOptions.StandardResolveOptions;
import org.apache.beam.sdk.io.fs.ResourceId;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.options.ValueProvider.StaticValueProvider;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.transforms.windowing.PaneInfo;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class WindowedFilenamePolicy extends FilenamePolicy {

    private static final Logger logger = LoggerFactory.getLogger(WindowedFilenamePolicy.class);

    private static final DateTimeFormatter YEAR = DateTimeFormat.forPattern("YYYY");
    private static final DateTimeFormatter MONTH = DateTimeFormat.forPattern("MM");
    private static final DateTimeFormatter DAY = DateTimeFormat.forPattern("dd");
    private static final DateTimeFormatter HOUR = DateTimeFormat.forPattern("HH");

    private final ValueProvider<String> outputDirectory;
    private final ValueProvider<String> suffix;
    private final ValueProvider<String> shardTemplate;

    public WindowedFilenamePolicy(
            ValueProvider<String> outputDirectory,
            ValueProvider<String> shardTemplate,
            ValueProvider<String> suffix) {
        this.outputDirectory = outputDirectory;
        this.shardTemplate = shardTemplate;
        this.suffix = suffix;
    }

    /**
     * The windowed filename method will construct filenames per window according to the baseFile,
     * suffix, and shardTemplate supplied. Directories with date templates in them will automatically
     * have their values resolved. For example the outputDirectory of /YYYY/MM/DD would resolve to
     * /2017/01/08 on January 8th, 2017.
     */
    @Override
    public ResourceId windowedFilename(
            int shardNumber,
            int numShards,
            BoundedWindow window,
            PaneInfo paneInfo,
            OutputFileHints outputFileHints) {

        ResourceId outputFile = resolveWithDateTemplates(outputDirectory, window);

        DefaultFilenamePolicy policy = DefaultFilenamePolicy.fromStandardParameters(
                        StaticValueProvider.of(outputFile), shardTemplate.get(), suffix.get(), true);

        ResourceId result = policy.windowedFilename(shardNumber, numShards, window, paneInfo, outputFileHints);
        logger.debug("Windowed file name policy created: {}", result.toString());
        return result;
    }

    @Override
    public ResourceId unwindowedFilename(
            int shardNumber, int numShards, OutputFileHints outputFileHints) {
        throw new UnsupportedOperationException("There is no policy for unwindowed filenames");
    }

    /**
     * Resolves any date variables which exist in the output directory path. This allows for the
     * dynamically changing of the output location based on the window end time.
     *
     * @return The new output directory with all variables resolved.
     */
    private ResourceId resolveWithDateTemplates(
            ValueProvider<String> outputDirectoryStr, BoundedWindow window) {
        ResourceId outputDirectory = FileSystems.matchNewResource(outputDirectoryStr.get(), true);

        if (window instanceof IntervalWindow) {
            IntervalWindow intervalWindow = (IntervalWindow) window;
            DateTime time = intervalWindow.end().toDateTime();
            String outputPath = outputDirectory.toString();
            outputPath = outputPath.replace("YYYY", YEAR.print(time));
            outputPath = outputPath.replace("MM", MONTH.print(time));
            outputPath = outputPath.replace("DD", DAY.print(time));
            outputPath = outputPath.replace("HH", HOUR.print(time));
            outputDirectory = FileSystems.matchNewResource(outputPath, true);
        }
        return outputDirectory;
    }

    public ValueProvider<String> getOutputDirectory() {
        return outputDirectory;
    }
}