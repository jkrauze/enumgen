package io.github.jkrauze.enumgen;

import freemarker.template.TemplateException;
import io.github.jkrauze.enumgen.model.ConfigEntry;
import io.github.jkrauze.enumgen.model.ImmutableConfigEntry;
import io.github.jkrauze.enumgen.model.ImmutableTemplateProcessingContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class TemplateProcessorTest {

    private static Logger log = Logger.getLogger(TemplateProcessorTest.class.getSimpleName());

    Vars vars;
    Results results;

    TemplateProcessor templateProcessor;

    @BeforeEach
    void setUp() throws IOException {
        vars = new Vars();
        results = new Results();

        templateProcessor = new TemplateProcessor();
    }

    @Test
    void singleEntry() {
        testFollowingEntriesProcessing(ImmutableConfigEntry.builder()
                .key("hello")
                .value("world")
                .build());
    }

    @Test
    void twoEntries() {
        testFollowingEntriesProcessing(
                ImmutableConfigEntry.builder()
                        .key("first.entry.key")
                        .value("First entry value!")
                        .build(),
                ImmutableConfigEntry.builder()
                        .key("second.entry.key")
                        .value("Second entry value!")
                        .build());
    }

    private void testFollowingEntriesProcessing(ConfigEntry... entries) {
        givenFollowingEntries(entries);
        whenProcessingTemplate();
        printResult();
        thenResultIsCompiling();
        thenResultMatchesEntries();
    }

    private void givenFollowingEntries(ConfigEntry... entries) {
        vars.entries = Arrays.asList(entries);
    }

    private void whenProcessingTemplate() {
        try {
            results.enumClassSource = templateProcessor.process(
                    ImmutableTemplateProcessingContext.builder()
                            .packageName(vars.packageName)
                            .className(vars.enumClassName)
                            .entries(vars.entries)
                            .build());
        } catch (IOException | TemplateException e) {
            fail(e);
        }
    }

    private void printResult() {
        System.out.println(results.enumClassSource);
    }

    private void thenResultIsCompiling() {
        try {
            JavaFileObject javaFileObject = new InMemoryJavaFileObject(vars.enumClassName, results.enumClassSource);
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

            Iterable<File> files = Arrays.asList(new File("target/classes"));
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, files);

            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

            JavaCompiler.CompilationTask task = compiler.getTask(null,
                    fileManager,
                    diagnostics,
                    null,
                    null,
                    Arrays.asList(javaFileObject));

            boolean success = task.call();

            fileManager.close();
            assertTrue(success, "Compilation failed!");
        } catch (Exception e) {
            fail(e);
        }
    }

    private void thenResultMatchesEntries() {
        try {
            Class<?> clazz = loadClass(vars.packageName + "." + vars.enumClassName);
            Object[] enumConstants = clazz.getEnumConstants();
            for (int i = 0; i < enumConstants.length; i++) {
                ConfigEntry expected = vars.entries.get(i);
                Object result = enumConstants[i];
                Assertions.assertEquals(expected.getEnumKey(), ((Enum) result).name());
                Assertions.assertEquals(expected.getKey(), clazz.getDeclaredMethod("getKey").invoke(result));
                Assertions.assertEquals(expected.getValue(), clazz.getDeclaredMethod("getDefaultValue").invoke(result));
            }
        } catch (ReflectiveOperationException | IOException e) {
            fail(e);
        }
    }

    private Class<?> loadClass(String className) throws IOException, ClassNotFoundException {
        Class<?> clazz = TemplateProcessor.class;
        URL[] urls = {clazz.getProtectionDomain().getCodeSource().getLocation()};
        ClassLoader delegateParent = clazz.getClassLoader().getParent();
        try (URLClassLoader cl = new URLClassLoader(urls, delegateParent)) {
            return cl.loadClass(className);
        }
    }


    // in-memory Java file object
    class InMemoryJavaFileObject extends SimpleJavaFileObject {
        private String contents = null;

        public InMemoryJavaFileObject(String className, String contents)
                throws Exception {
            super(URI.create("string:///" + className.replace('.', '/') +
                    Kind.SOURCE.extension), Kind.SOURCE);
            this.contents = contents;
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors)
                throws IOException {
            return contents;
        }
    }

    static class Vars {
        String enumClassName = "TestEnum";
        String packageName = "io.github.jkrauze.enumgen.test";
        List<ConfigEntry> entries;
    }

    static class Results {
        String enumClassSource;
    }

}