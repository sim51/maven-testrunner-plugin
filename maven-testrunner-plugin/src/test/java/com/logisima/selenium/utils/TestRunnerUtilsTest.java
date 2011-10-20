/**
 *  This file is part of LogiSima (http://www.logisima.com).
 *
 *  maven-testrunner-plugin is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  maven-testrunner-plugin is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with maven-testrunner-plugin. If not, see <http://www.gnu.org/licenses/>.
 *  
 *  @author Benoît Simard
 *  @See https://github.com/sim51/maven-testrunner-plugin
 */
package com.logisima.selenium.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

public class TestRunnerUtilsTest extends TestCase {

    @Test
    public void testCopySelenium() throws MojoExecutionException {
        File seleniumTarget = new File(FileUtils.getTempDirectoryPath() + "/selenium");
        TestRunnerUtils.copySeleniumToDir(seleniumTarget);
        File file = new File(seleniumTarget.getAbsolutePath() + "/Blank.html");
        if (!file.exists()) {
            fail("Fail to copy selenium testrunner");
        }
    }

    @Test
    public void testGetTestName() {
        String testSourceDirectory = "/tmp/project/src/test/java";
        File test = new File(testSourceDirectory + "/fr/logisima/test/oneTest.test.html");

        String name = TestRunnerUtils.getTestName(test, testSourceDirectory);

        assertEquals("/fr/logisima/test/oneTest.test.html", name);
    }

    @Test
    public void testGetTestDisplayName() {
        String testSourceDirectory = "/tmp/project/src/test/java";
        File test = new File(testSourceDirectory + "/fr/logisima/test/oneTest.test.html");

        String url = TestRunnerUtils.getTestDisplayName(test, testSourceDirectory);

        assertEquals("fr.logisima.test.oneTest.test.html", url);
    }

    @Test
    public void testGetTestActionUrl() {
        String testSourceDirectory = "/tmp/project/src/test/java";
        File test = new File(testSourceDirectory + "/fr/logisima/test/oneTest.test.html");

        String url = TestRunnerUtils.getTestActionUrl(test, testSourceDirectory);

        assertEquals("/test/fr/logisima/test/oneTest.test.html", url);
    }

    @Test
    public void testGetSuiteActionUrl() {
        String testSourceDirectory = "/tmp/project/src/test/java";
        File test = new File(testSourceDirectory + "/fr/logisima/test/oneTest.test.html");

        String url = TestRunnerUtils.getSuiteActionUrl(test, testSourceDirectory);

        assertEquals("/suite/fr/logisima/test/oneTest.test.html", url);
    }

    @Test
    public void testGetResultActionUrl() {
        String testSourceDirectory = "/tmp/project/src/test/java";
        File test = new File(testSourceDirectory + "/fr/logisima/test/oneTest.test.html");

        String url = TestRunnerUtils.getResultActionUrl(test, testSourceDirectory);

        assertEquals("/testresult/fr/logisima/test/oneTest.test.html", url);
    }

    @Test
    public void testGetTestrunnerActionUrl() throws UnsupportedEncodingException {
        String testSourceDirectory = "/tmp/project/src/test/java";
        File test = new File(testSourceDirectory + "/fr/logisima/test/oneTest.test.html");
        String baseApplicationUrl = "http://localhost:8080";

        String url = TestRunnerUtils.getTestrunnerActionUrl(test, baseApplicationUrl, testSourceDirectory);

        assertEquals(
                "/TestRunner.html?baseUrl=http://localhost:8080&auto=true&test=%2Fsuite%2Ffr%2Flogisima%2Ftest%2FoneTest.test.html&resultsUrl=%2Ftestresult%2Ffr%2Flogisima%2Ftest%2FoneTest.test.html",
                url);
    }

    @Test
    public void testGetTestrunnerActionFullUrl() throws IOException {
        String testSourceDirectory = "/tmp/project/src/test/java";
        File test = new File(testSourceDirectory + "/fr/logisima/test/oneTest.test.html");
        String baseApplicationUrl = "http://localhost:8080";

        URL url = TestRunnerUtils.getTestrunnerActionFullUrl(test, baseApplicationUrl, 7777, testSourceDirectory);

        assertEquals(
                "http://localhost:7777/TestRunner.html?baseUrl=http://localhost:8080&auto=true&test=%2Fsuite%2Ffr%2Flogisima%2Ftest%2FoneTest.test.html&resultsUrl=%2Ftestresult%2Ffr%2Flogisima%2Ftest%2FoneTest.test.html",
                url.toString());
    }

    @Test
    public void testGetAutoTestUrl() throws IOException {
        String testSourceDirectory = "/tmp/project/src/test/java";
        File test = new File(testSourceDirectory + "/fr/logisima/test/oneTest.test.html");

        String url = TestRunnerUtils.getAutoTestUrl(test, testSourceDirectory);
        assertEquals("/runtest/fr/logisima/test/oneTest.test.html", url);

    }

    @Test
    public void testWriteContent() throws IOException {
        File file = new File(FileUtils.getTempDirectoryPath() + "/myTest.txt");
        String content = "Some text";

        TestRunnerUtils.writeContent(content, file);

        BufferedReader br = new BufferedReader(new FileReader(file));
        String strLine = br.readLine();

        assertEquals(content, strLine);
    }
}