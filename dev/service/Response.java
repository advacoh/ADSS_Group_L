package service;

public class Response<T> {

    private final T value;
    private final String errorMessage;
    private final boolean isError;

    private Response(T value, String errorMessage, boolean isError) {
        this.value = value;
        this.errorMessage = errorMessage;
        this.isError = isError;
    }

    // For operations that return a value
    public static <T> Response<T> success(T value) {
        return new Response<>(value, null, false);
    }

    // For void operations — no null leaking through call sites
    public static Response<Void> success() {
        return new Response<>(null, null, false);
    }

    public static <T> Response<T> failure(String errorMessage) {
        return new Response<>(null, errorMessage, true);
    }

    public boolean isError() {
        return isError;
    }

    // Throws instead of silently returning null
    public T getValue() {
        if (isError) {
            throw new IllegalStateException("Response is an error: " + errorMessage);
        }
        if (value == null) {
            throw new IllegalStateException("Response has no value (void operation)");
        }
        return value;
    }

    // Use this before calling getValue()
    public boolean hasValue() {
        return !isError && value != null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}