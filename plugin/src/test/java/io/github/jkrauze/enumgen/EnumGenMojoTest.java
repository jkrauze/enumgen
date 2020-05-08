package io.github.jkrauze.enumgen;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

@RunWith(JUnit4.class)
public class EnumGenMojoTest extends AbstractMojoTestCase {

    public EnumGenMojoTest() {
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void execute() throws Exception {
        File pom = getTestFile("src/test/resources/simple-plugin-config.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());

//        EnumGenMojo mojo = (EnumGenMojo) lookupMojo("generate-enum", pom);
//        assertNotNull(mojo);
//
//        mojo.execute();
    }
}