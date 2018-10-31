package nl.debijenkorf.snowplow.coders;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import nl.debijenkorf.snowplow.values.FailsafeElement;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.CoderException;
import org.apache.beam.sdk.coders.CustomCoder;
import org.apache.beam.sdk.coders.NullableCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.beam.sdk.values.TypeParameter;

public class FailsafeElementCoder<Original, Current>
        extends CustomCoder<FailsafeElement<Original, Current>> {

    private static final NullableCoder<String> STRING_CODER = NullableCoder.of(StringUtf8Coder.of());
    private final Coder<Original> originalPayloadCoder;
    private final Coder<Current> currentPayloadCoder;

    private FailsafeElementCoder(
            Coder<Original> originalPayloadCoder, Coder<Current> currentPayloadCoder) {
        this.originalPayloadCoder = originalPayloadCoder;
        this.currentPayloadCoder = currentPayloadCoder;
    }

    public static <Original, Current> FailsafeElementCoder<Original, Current> of(
            Coder<Original> originalPayloadCoder, Coder<Current> currentPayloadCoder) {
        return new FailsafeElementCoder<>(originalPayloadCoder, currentPayloadCoder);
    }

    @Override
    public void encode(FailsafeElement<Original, Current> value, OutputStream outStream)
            throws IOException {
        if (value == null) {
            throw new CoderException("The FailsafeElementCoder cannot encode a null object!");
        }

        originalPayloadCoder.encode(value.getOriginalPayload(), outStream);
        currentPayloadCoder.encode(value.getPayload(), outStream);
        STRING_CODER.encode(value.getErrorMessage(), outStream);
        STRING_CODER.encode(value.getStackTrace(), outStream);
    }

    @Override
    public FailsafeElement<Original, Current> decode(InputStream inStream) throws IOException {

        Original originalPayload = originalPayloadCoder.decode(inStream);
        Current currentPayload = currentPayloadCoder.decode(inStream);
        String errorMessage = STRING_CODER.decode(inStream);
        String stacktrace = STRING_CODER.decode(inStream);

        return FailsafeElement.of(originalPayload, currentPayload)
                .setErrorMessage(errorMessage)
                .setStacktrace(stacktrace);
    }

    @Override
    public List<? extends Coder<?>> getCoderArguments() {
        return Arrays.asList(originalPayloadCoder, currentPayloadCoder);
    }

    @Override
    public TypeDescriptor<FailsafeElement<Original, Current>> getEncodedTypeDescriptor() {
        return new TypeDescriptor<FailsafeElement<Original, Current>>() {}
            .where(new TypeParameter<Original>() {},
                    originalPayloadCoder.getEncodedTypeDescriptor())
                .where(new TypeParameter<Current>() {},
                        currentPayloadCoder.getEncodedTypeDescriptor());
    }
}