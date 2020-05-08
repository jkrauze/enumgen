# EnumGen

Generate Java Enums based on resources.

Replace your manually built properties key enum or string constants with compile-time generated enum.

## Example

Convert `.properties` file *(TODO: `.json` and `.yaml` support)*

**application.properties**
```properties
sample.key=Sample value
other.sample.key=Other sample value!
```

to Java enum

**ApplicationKeys**
```java
public enum ApplicationKeys {

    OTHER_SAMPLE_KEY("other.sample.key", "Other sample value!"),
    SAMPLE_KEY("sample.key", "Sample value");

    private String key;
    private String defaultValue;

    ApplicationKeys(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

}
```

on the compile time.

You'll be able to use the enum to ensure your string keys.

Instead of

```java
System.getProperty("sample.key")
// or
System.getProperty("sample.key", "Sample value")
```

you could do

```java
System.getProperty(ApplicationKeys.SAMPLE_KEY.getKey());
// or
System.getProperty(ApplicationKeys.SAMPLE_KEY.getKey(), ApplicationKeys.SAMPLE_KEY.getDefaultValue());
```

what would help you find out if you're using wrong key at the compile time!

## How to run?

Add the build plugin to your Maven project.

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>io.github.jkrauze.enumgen</groupId>
                <artifactId>enumgen-maven-plugin</artifactId>
                <version>0.1.0</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>generate-enum</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

## Configuration

*TODO*

```xml
            <plugin>
                ...
                <configuration>
                    <basePackageName>${project.groupId}.${project.artifactId}.resources</basePackageName>
                    <baseDir>src/main/resources</baseDir>
                    <recursive>true</recursive>
                    <outputBaseDir>target/generated-sources/enumgen</outputBaseDir>
                    <allowedExtensions>
                        <extension>properties</extension>
                        <!--TODO <extension>json</extension>-->
                        <!--TODO <extension>yaml</extension>-->
                    </allowedExtensions>
                    <enumClassNamePrefix></enumClassNamePrefix>
                    <enumClassNameSuffix>Keys</enumClassNameSuffix>
                </configuration>
            </plugin>
```

## License

Licensed under the [MIT License](LICENSE).
