package com.logisima.selenium.utils;

import java.io.File;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

public class TestRunnerUtilsTest extends TestCase {

    @Test
    public void testCopySelenium() throws MojoExecutionException {
        File seleniumTarget = new File(FileUtils.getTempDirectoryPath() + "/selenium");
        TestRunnerUtils.copySeleniumToDir(seleniumTarget);
    }
}