package io.github.jkrauze.enumgen.input.impl;

import io.github.jkrauze.enumgen.input.InputParser;
import io.github.jkrauze.enumgen.model.ConfigEntry;
import io.github.jkrauze.enumgen.model.ImmutableConfigEntry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class PropertiesInputParser implements InputParser {

    @Override
    public Set<String> supportedExtensions() {
        return Collections.singleton("properties");
    }

    @Override
    public List<ConfigEntry> parse(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        return properties.stringPropertyNames().stream()
                .map(key -> ImmutableConfigEntry.builder()
                        .key(key)
                        .value(properties.getProperty(key))
                        .build())
                .collect(Collectors.toList());
    }

}
