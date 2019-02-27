package nl.debijenkorf.backup.values;

import org.apache.avro.reflect.Nullable;

public class FailsafeElement<Original, Current> {

    private final Original originalPayload;
    private final Current payload;
    @Nullable private String errorMessage;
    @Nullable private String stackTrace;

    private FailsafeElement(Original original, Current current) {
        this.originalPayload = original;
        this.payload = current;
    }

    public static <Original, Current> FailsafeElement<Original, Current> of(
        Original original, Current current) {
            return new FailsafeElement<>(original, current);
    }

    public Original getOriginalPayload() {
        return this.originalPayload;
    }

    public Current getPayload() {
        return this.payload;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public String getStackTrace() {
        return this.stackTrace;
    }

    public FailsafeElement<Original, Current> setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public FailsafeElement<Original, Current> setStacktrace(String stacktrace) {
        this.stackTrace = stacktrace;
        return this;
    }
}