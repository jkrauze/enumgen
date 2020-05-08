package ${packageName};

import javax.annotation.Generated;

/**
 * List of keys from ${file} file.
 */
@Generated(value = "${generatorClass}",
        comments = "${generatedComment}",
        date = "${generatedDate}")
public enum ${className} {

<#list entries as entry>
    ${entry.enumKey}("${entry.key}", "${entry.value}")<#if entry?is_last>;<#else>,</#if>
</#list>

    private String key;
    private String defaultValue;

    ${className}(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the key as it is in the file.
     *
     * @return original key
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the default value taken from the file at the compile time.
     *
     * @return default value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

}