package org.acme.application.util;

import java.util.function.Function;

public record PatchField<T>(
        T value,
        boolean isSet
) {
    public <Q> PatchField<Q> mapTo(Function<T, Q> map) {
        if (isSet) {
            return new PatchField<>(map.apply(value), true);
        }
        return new PatchField<>(null, false);
    }
}
