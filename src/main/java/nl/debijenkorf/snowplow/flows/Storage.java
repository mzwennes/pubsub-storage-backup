package nl.debijenkorf.snowplow.flows;

import lombok.Builder;
import nl.debijenkorf.snowplow.utils.WindowedFilenamePolicy;
import org.apache.beam.sdk.io.Compression;
import org.apache.beam.sdk.io.FileBasedSink;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.fs.ResourceId;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.values.PCollection;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Builder
public class Storage implements Flow<String> {

    private int shards;
    private WindowedFilenamePolicy filePattern;

    @Override
    public PCollection<String> read() {
        throw new NotImplementedException();
    }

    @Override
    public void write(PCollection<String> payload) {
        payload.apply(TextIO.write()
                .to(filePattern)
                .withCompression(Compression.GZIP)
                .withWindowedWrites()
                .withNumShards(shards)
                .withTempDirectory(ValueProvider.NestedValueProvider.of(
                        filePattern.getOutputDirectory(),
                        (SerializableFunction<String, ResourceId>) FileBasedSink::convertToFileResourceIfPossible)));
    }
}
