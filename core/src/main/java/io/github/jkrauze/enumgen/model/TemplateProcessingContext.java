package io.github.jkrauze.enumgen.model;


import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;

@Value.Immutable
public interface TemplateProcessingContext {

    @Nullable
    Path getFilePath();

    List<ConfigEntry> getEntries();

    String getPackageName();

    String getClassName();

    @Nullable
    MojoDescriptor getMojo();

}
