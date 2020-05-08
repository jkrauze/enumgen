package io.github.jkrauze.enumgen.keynaming;

import io.github.jkrauze.enumgen.keynaming.impl.DefaultEnumKeyNamingStrategy;

public class EnumKeyNamingStrategyHandle {

    private static EnumKeyNamingStrategy INSTANCE = DefaultEnumKeyNamingStrategy.getInstance();

    public static EnumKeyNamingStrategy getInstance() {
        return INSTANCE;
    }

    public static void setInstance(EnumKeyNamingStrategy instance) {
        INSTANCE = instance;
    }

}
