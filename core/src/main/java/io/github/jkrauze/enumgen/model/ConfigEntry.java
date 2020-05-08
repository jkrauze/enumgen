package io.github.jkrauze.enumgen.model;

import io.github.jkrauze.enumgen.keynaming.EnumKeyNamingStrategyHandle;
import org.immutables.value.Value;

@Value.Immutable
public interface ConfigEntry {

    String getKey();

    String getValue();

    default String getEnumKey() {
        return EnumKeyNamingStrategyHandle.getInstance().getEnumKey(getKey());
    }

}
