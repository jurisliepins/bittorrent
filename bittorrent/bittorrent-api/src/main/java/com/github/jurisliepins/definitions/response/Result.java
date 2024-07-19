package com.github.jurisliepins.definitions.response;

import java.util.Objects;

public record Result<T>(
        StatusType status,
        T data
) {
    public Result {
        Objects.requireNonNull(status, "status is null");
        Objects.requireNonNull(data, "data is null");
    }

    public static <T> Result<T> success(final T data) {
        return new Result<>(StatusType.SUCCESS, data);
    }

    public static <T> Result<T> failure(final T data) {
        return new Result<>(StatusType.FAILURE, data);
    }
}
