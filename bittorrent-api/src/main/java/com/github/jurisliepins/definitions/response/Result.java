package com.github.jurisliepins.definitions.response;

import static java.util.Objects.requireNonNull;

public record Result<T>(
        StatusType status,
        T data
) {
    public Result {
        requireNonNull(status, "status is null");
        requireNonNull(data, "data is null");
    }

    public static <T> Result<T> success(final T data) {
        return new Result<>(StatusType.Success, data);
    }

    public static <T> Result<T> failure(final T data) {
        return new Result<>(StatusType.Failure, data);
    }
}
