package io.github.jkrauze.enumgen.keynaming.impl;

import io.github.jkrauze.enumgen.keynaming.EnumKeyNamingStrategy;

/**
 * Applies following rules:
 * <ul>
 * <li>uppercase</li>
 * <li>replace all forbidden characters with '_'</li>
 * <li>add '_' prefix if key starts with number</li>
 * </ul>
 */
public class DefaultEnumKeyNamingStrategy implements EnumKeyNamingStrategy {

    private static final DefaultEnumKeyNamingStrategy INSTANCE = new DefaultEnumKeyNamingStrategy();

    public static DefaultEnumKeyNamingStrategy getInstance() {
        return INSTANCE;
    }

    @Override
    public String getName() {
        return "default";
    }

    @Override
    public String getEnumKey(String key) {
        String str = key.toUpperCase()
                .replaceAll("[^A-Z0-9_]", "_");
        if (Character.isDigit(str.charAt(0))) {
            str = "_" + str;
        }
        return str;
    }

}
