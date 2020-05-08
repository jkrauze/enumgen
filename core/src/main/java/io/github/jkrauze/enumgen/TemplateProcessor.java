package io.github.jkrauze.enumgen;

import freemarker.cache.TemplateNameFormat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import io.github.jkrauze.enumgen.model.TemplateProcessingContext;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.time.OffsetDateTime;
import java.util.HashMap;

public class TemplateProcessor {

    Configuration cfg;
    Template template;

    public TemplateProcessor() throws IOException {
        cfg = new Configuration(Configuration.VERSION_2_3_30);
        cfg.setTemplateNameFormat(TemplateNameFormat.DEFAULT_2_4_0);
        cfg.setClassForTemplateLoading(getClass(), "/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        template = cfg.getTemplate("default.ftl");
    }

    public String process(TemplateProcessingContext ctx) throws IOException, TemplateException {
        HashMap<String, Object> map = new HashMap<>();
        map.put("packageName", ctx.getPackageName());
        map.put("className", ctx.getClassName());
        map.put("entries", ctx.getEntries());
        map.put("generatorClass", ctx.getMojo() == null ? "enumgen" : ctx.getMojo().getImplementation());
        map.put("generatedComment", ctx.getMojo() == null ? "enumgen" : ctx.getMojo().getRoleHint());
        map.put("generatedDate", OffsetDateTime.now().toString());
        map.put("file", ctx.getFilePath() == null ? "?" : ctx.getFilePath());

        Writer out = new StringWriter();
        template.process(map, out);
        return out.toString();
    }

}
