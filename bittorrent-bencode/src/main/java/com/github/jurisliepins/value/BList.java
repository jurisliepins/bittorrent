package com.github.jurisliepins.value;

import com.github.jurisliepins.BException;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public record BList(@NonNull List<BValue> value) implements BValue {
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return switch (other) {
            case BList val -> value().equals(val.value());
            default -> false;
        };
    }

    @Override
    public int compareTo(@NonNull final BValue other) {
        throw new BException("Comparable not supported for %s".formatted(BValueType.BDictionaryType));
    }

    @Override
    public String toString() {
        return "BList[value=\"\"]";
    }

    public static BList of() {
        return new BList(new ArrayList<>());
    }

    public static BList of(@NonNull final BValue... values) {
        return new BList(List.of(values));
    }

    public static BList of(@NonNull final List<BValue> values) {
        return new BList(values);
    }
}
