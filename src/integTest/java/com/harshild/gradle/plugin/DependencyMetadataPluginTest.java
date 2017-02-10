package com.harshild.gradle.plugin;

import com.harshild.GradleTestHelper;
import com.harshild.gradle.plugin.constants.CommandConstants;
import com.harshild.gradle.plugin.task.MetadataReportGeneratorTask;
import org.gradle.testkit.runner.BuildResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Created by harshild on 2/6/2017.
 */
public class DependencyMetadataPluginTest {
    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();
    private File buildFile;
    String buildFileContent;

    @Before
    public void setup() throws IOException {
        buildFile = testProjectDir.newFile("build.gradle");
        buildFileContent = "plugins {" +
                "    id 'com.harshild.dependency-metadata'" +
                "}";
        GradleTestHelper.writeFile(buildFile, buildFileContent);
    }

    @Test
    public void plugin_has_reportGeneratorTask() throws IOException {
        BuildResult result = GradleTestHelper.executeBuild(testProjectDir,"tasks","--all");
        assertEquals(result.task(":tasks").getOutcome(), SUCCESS);
        assertTrue(result.getOutput().contains(CommandConstants.GENERATE_DEPENDENCY_METADATA_REPORT));
    }

    @Test
    public void plugin_canExecute_reportGeneratorTask() throws IOException {
        BuildResult result = GradleTestHelper.executeBuild(testProjectDir, CommandConstants.GENERATE_DEPENDENCY_METADATA_REPORT);
        assertEquals(result.task(":"+CommandConstants.GENERATE_DEPENDENCY_METADATA_REPORT).getOutcome(), SUCCESS);
        assertTrue(result.getOutput().contains(MetadataReportGeneratorTask.INFO_MESSAGE));
    }


    @Test
    public void plugin_canExecute_reportGeneratorTask_AndGenerateXMLForOneDependency() throws IOException {
        String compileDep = buildFileContent + "\n"+
                "apply plugin: 'java'\n\n"
                +"repositories {\n" +
                "    mavenCentral()\n" +
                "}\n"+
                "dependencies {\n" +
                "    compile 'junit:junit:4.12'\n" +
                "}";
        GradleTestHelper.writeFile(buildFile, compileDep);

        BuildResult result = GradleTestHelper.executeBuild(testProjectDir, CommandConstants.GENERATE_DEPENDENCY_METADATA_REPORT);
        assertEquals(result.task(":"+CommandConstants.GENERATE_DEPENDENCY_METADATA_REPORT).getOutcome(), SUCCESS);
        assertTrue(result.getOutput().contains(MetadataReportGeneratorTask.INFO_MESSAGE));
        assertTrue(new File(testProjectDir.getRoot().getAbsolutePath()+"/build/reports/dependency-metadata.xml").exists());
    }


    @Test
    public void plugin_canExecute_reportGeneratorTask_AndGenerateXMLForMultipleDependencies() throws IOException {
        String compileDep = buildFileContent + "\n"+
                "apply plugin: 'java'\n\n"
                +"repositories {\n" +
                "    mavenCentral()\n" +
                "}\n"+
                "dependencies {\n" +
                "    compile 'junit:junit:4.12'\n" +
                "    compile 'javax.inject:javax.inject:1'\n" +
                "}";
        GradleTestHelper.writeFile(buildFile, compileDep);

        BuildResult result = GradleTestHelper.executeBuild(testProjectDir, CommandConstants.GENERATE_DEPENDENCY_METADATA_REPORT);
        assertEquals(result.task(":"+CommandConstants.GENERATE_DEPENDENCY_METADATA_REPORT).getOutcome(), SUCCESS);
        assertTrue(result.getOutput().contains(MetadataReportGeneratorTask.INFO_MESSAGE));
        assertTrue(new File(testProjectDir.getRoot().getAbsolutePath()+"/build/reports/dependency-metadata.xml").exists());
    }

    @Test
    public void plugin_shouldRun_build_before_reportGeneratorTask() throws IOException {
        BuildResult result = GradleTestHelper.executeBuild(testProjectDir,CommandConstants.GENERATE_DEPENDENCY_METADATA_REPORT);
        assertEquals(result.task(":build").getOutcome(), SUCCESS);
    }

}