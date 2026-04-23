package com.p2p.tutoring.service.common;

public final class ServiceResult<T> {

    private final boolean success;
    private final String message;
    private final T payload;

    private ServiceResult(boolean success, String message, T payload) {
        this.success = success;
        this.message = message;
        this.payload = payload;
    }

    public static <T> ServiceResult<T> success(T payload) {
        return new ServiceResult<>(true, null, payload);
    }

    public static <T> ServiceResult<T> success() {
        return new ServiceResult<>(true, null, null);
    }

    public static <T> ServiceResult<T> failure(String message) {
        return new ServiceResult<>(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getPayload() {
        return payload;
    }
}
