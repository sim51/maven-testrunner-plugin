package com.logisima.selenium;

import java.io.File;

import junit.framework.TestCase;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import com.logisima.selenium.utils.TestRunnerUtils;

public class TestRunnerUtilsTest extends TestCase {

    @Test
    public void testCopySelenium() throws MojoExecutionException {
        File seleniumTarget = new File("/home/bsimard/workspace_logisima/selenium-testrunner/target/");
        TestRunnerUtils.copySeleniumToDir(seleniumTarget);
    }
}
