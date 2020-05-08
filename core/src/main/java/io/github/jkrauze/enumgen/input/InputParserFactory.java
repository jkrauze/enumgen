package io.github.jkrauze.enumgen.input;

import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;

public class InputParserFactory {

    Map<String, InputParser> instancesPerExtension;

    public InputParserFactory() throws IllegalAccessException, InstantiationException {
        instancesPerExtension = new HashMap<>();
        for (Class<? extends InputParser> implClass :
                new Reflections(InputParser.class.getPackage().getName())
                        .getSubTypesOf(InputParser.class)) {
            InputParser inputParser = implClass.newInstance();
            for (String supportedExtension : inputParser.supportedExtensions()) {
                instancesPerExtension.put(supportedExtension, inputParser);
            }
        }
    }

    public InputParser get(String extension) {
        return instancesPerExtension.get(extension);
    }

}
