package io.github.jkrauze.enumgen.input;

import io.github.jkrauze.enumgen.model.ConfigEntry;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

public interface InputParser {

    Set<String> supportedExtensions();

    List<ConfigEntry> parse(InputStream inputStream) throws IOException;

}
