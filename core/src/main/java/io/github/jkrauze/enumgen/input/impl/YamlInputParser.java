package io.github.jkrauze.enumgen.input.impl;

import io.github.jkrauze.enumgen.input.InputParser;
import io.github.jkrauze.enumgen.model.ConfigEntry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class YamlInputParser implements InputParser {

    @Override
    public Set<String> supportedExtensions() {
        return Collections.singleton("yaml");
    }

    @Override
    public List<ConfigEntry> parse(InputStream inputStream) throws IOException {
        return Collections.emptyList(); // TODO: 02.05.20 implement
    }

}
