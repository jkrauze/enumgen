package io.github.jkrauze.enumgen;

import freemarker.template.TemplateException;
import io.github.jkrauze.enumgen.input.InputParser;
import io.github.jkrauze.enumgen.input.InputParserFactory;
import io.github.jkrauze.enumgen.model.ImmutableTemplateProcessingContext;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@Mojo(name = "generate-enum", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class EnumGenMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.groupId}.${project.artifactId}.resources")
    private String basePackageName;

    @Parameter(defaultValue = "src/main/resources")
    private String baseDir;

    @Parameter(defaultValue = "false")
    private boolean recursive;

    @Parameter(defaultValue = "target/generated-sources/enumgen")
    private String outputBaseDir;

// TODO:
//    @Parameter
//    private String fileNameFilter;

    @Parameter(defaultValue = "properties")
    private Set<String> allowedExtensions;

    @Parameter
    private String enumClassNamePrefix;

    @Parameter(defaultValue = "Keys")
    private String enumClassNameSuffix;


    private final InputParserFactory inputParserFactory;
    private final TemplateProcessor templateProcessor;

    public EnumGenMojo() throws InstantiationException, IllegalAccessException, IOException {
        inputParserFactory = new InputParserFactory();
        templateProcessor = new TemplateProcessor();
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();
        Path baseDirPath = Paths.get(baseDir);
        if (!baseDirPath.toFile().exists()) {
            log.warn(String.format("Skipping enumgen as %s dir not found.", baseDir));
            log.warn("Please set other base directory using 'baseDir' property.");
            return;
        }
        log.info(String.format("Searching for %s files in %s dir...", allowedExtensions, baseDir));
        basePackageName = cleanPackageName(basePackageName);
        try {
            List<Path> files = Files.walk(baseDirPath, recursive ? Integer.MAX_VALUE : 1)
                    .filter(Files::isRegularFile)
                    .filter(path -> allowedExtensions.contains(getExtension(path.getFileName().toString())))
                    .collect(Collectors.toList());
            log.info(String.format("Generating %d enum%s in %s package.", files.size(), files.size() == 1 ? "" : "s", basePackageName));
            for (Path path : files) {
                log.debug("Generating enum from " + path);
                try (FileInputStream fis = new FileInputStream(path.toFile())) {
                    InputParser inputParser = inputParserFactory.get(getExtension(path.getFileName().toString()));
                    ImmutableTemplateProcessingContext ctx = ImmutableTemplateProcessingContext.builder()
                            .filePath(path)
                            .entries(inputParser.parse(fis))
                            .className(getEnumClassName(path))
                            .packageName(getEnumPackageName(path))
                            .mojo(getMojo())
                            .build();
                    saveToFile(path, templateProcessor.process(ctx));
                    log.info(String.format("Generated %s enum from %s file.", ctx.getClassName(), baseDirPath.relativize(ctx.getFilePath())));
                }
            }
            if (!files.isEmpty()) {
                project.addCompileSourceRoot(outputBaseDir);
            }
        } catch (IOException | TemplateException e) {
            throw new MojoFailureException(e.getMessage(), e);
        }
    }

    private void saveToFile(Path path, String classImpl) throws IOException {
        Path outputPath = Paths.get(outputBaseDir, normalizeToPath(getEnumPackageName(path)), getEnumClassName(path) + ".java");
        outputPath.getParent().toFile().mkdirs();
        Files.deleteIfExists(outputPath);
        Files.write(outputPath, classImpl.getBytes(Charset.defaultCharset()), CREATE_NEW);
    }

    private MojoDescriptor getMojo() {
        return getPluginDescriptor().getMojo("generate-enum");
    }

    private PluginDescriptor getPluginDescriptor() {
        return (PluginDescriptor) getPluginContext().get("pluginDescriptor");
    }

    private String normalizeToPath(String enumPackage) {
        return enumPackage.replaceAll("\\.", "/");
    }

    private String getEnumPackageName(Path path) {
        return basePackageName + normalizeToPackageName(path);
    }

    private String cleanPackageName(String packageName) {
        return removeNotAllowedCharsInPackageName(packageName.replaceAll("-", "."));
    }

    private String removeNotAllowedCharsInPackageName(String packageName) {
        return packageName.replaceAll("[^A-Za-z0-9.]", "");
    }

    private String normalizeToPackageName(Path path) {
        Path relativeDir = Paths.get(baseDir).relativize(path).getParent();
        return relativeDir == null ? "" : cleanPackageName("." + relativeDir.toString().replaceAll("[\\\\/]", ".").toLowerCase());
    }

    private String getEnumClassName(Path path) {
        return trimToEmpty(enumClassNamePrefix) + StringUtils.capitalize(FilenameUtils.getBaseName(path.getFileName().toString())) + trimToEmpty(enumClassNameSuffix);
    }

}
