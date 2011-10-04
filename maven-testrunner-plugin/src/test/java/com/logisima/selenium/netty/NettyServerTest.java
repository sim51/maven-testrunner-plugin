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
package com.logisima.selenium.netty;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.logisima.selenium.server.NettyServer;
import com.logisima.selenium.utils.SeleniumParserTest;
import com.logisima.selenium.utils.TestRunnerUtils;

public class NettyServerTest extends TestCase {

    private File        projectTestPathFile;
    private NettyServer server;
    private WebClient   firephoque;

    @Before
    public void setUp() throws MojoExecutionException, MalformedURLException {
        // copy selenium application
        File seleniumTarget = new File(FileUtils.getTempDirectoryPath() + "/selenium");
        TestRunnerUtils.copySeleniumToDir(seleniumTarget);

        // get the "src/test" path of the project
        URL url2 = SeleniumParserTest.class.getResource("/selenium.test.html");
        File script = new File(url2.getFile());
        String projectTestPath = script.getParentFile().getParentFile().getParentFile().toString() + "/src/test";
        projectTestPathFile = new File(projectTestPath);
        URL url = new URL("http://localhost:7777");

        // starting server
        server = new NettyServer(7777, seleniumTarget.getPath(), url, projectTestPathFile.getAbsolutePath(),
                FileUtils.getTempDirectoryPath() + "/selenium", 5);
        server.start();
        try {
            server.join();
        } catch (InterruptedException e) {
        }

        // get firephoque
        firephoque = TestRunnerUtils.getWebClient();
        firephoque.setTimeout(1000);
    }

    @Test
    public void testListActionServer() throws MojoExecutionException, FailingHttpStatusCodeException, IOException {
        HtmlPage page = firephoque.getPage(new URL("http://localhost:7777/"));
        assertEquals("Maven Selenium testrunner plugin", page.getTitleText());
        assertTrue(page
                .asXml()
                .contains(
                        "<a href=\"/TestRunner.html?baseUrl=http://localhost:7777&amp;auto=true&amp;test=%2Fsuite%2Fresources%2Fcom%2Flogisima%2Ftest%2Fselenium.test.html&amp;"));
    }

    @Test
    public void testSuiteActionServer() throws MojoExecutionException, FailingHttpStatusCodeException, IOException {
        File seleniumScript = new File(projectTestPathFile.getAbsolutePath() + "/resources/selenium.test.html");
        URL suiteUrl = new URL("http://localhost:7777"
                + TestRunnerUtils.getSuiteActionUrl(seleniumScript, projectTestPathFile.getAbsolutePath()));
        HtmlPage page = firephoque.getPage(suiteUrl);
        assertTrue(page.asText().contains("Maven Selenium testrunner plugin"));
        assertTrue(page.asText().contains("resources.selenium.test.html"));
    }

    @Test
    public void testTestActionServer() throws MojoExecutionException, FailingHttpStatusCodeException, IOException {
        File seleniumScript = new File(projectTestPathFile.getAbsolutePath() + "/resources/selenium.test.html");
        URL suiteUrl = new URL("http://localhost:7777"
                + TestRunnerUtils.getTestActionUrl(seleniumScript, projectTestPathFile.getAbsolutePath()));
        HtmlPage page = firephoque.getPage(suiteUrl);
        assertTrue(page.asText().contains("open"));
        assertTrue(page.asText().contains("assertTitle"));
    }

    @Test
    public void testRunSeleniumServer() throws MojoExecutionException, IOException, InterruptedException {
        File seleniumScript = new File(projectTestPathFile.getAbsolutePath() + "/resources/selenium.test.html");
        WebWindow window = firephoque.openWindow(TestRunnerUtils.getTestrunnerActionFullUrl(seleniumScript,
                "http://localhost:7777", 7777, projectTestPathFile.getAbsolutePath()), "headless");

        File resultFile = new File(FileUtils.getTempDirectoryPath()
                + "/selenium/selenium-result/resources.selenium.test.html.passed.html");
        Thread.sleep(5000);
        if (!resultFile.exists()) {
            fail("Selenium auto test failed !");
        }
    }

    @Test
    public void testRunSeleniumServerChunk() throws MojoExecutionException, IOException, InterruptedException {
        File seleniumScript = new File(projectTestPathFile.getAbsolutePath() + "/resources/NotLoggedUser.test.html");
        WebWindow window = firephoque.openWindow(TestRunnerUtils.getTestrunnerActionFullUrl(seleniumScript,
                "http://localhost:7777", 7777, projectTestPathFile.getAbsolutePath()), "headless");

        File resultFile = new File(FileUtils.getTempDirectoryPath()
                + "/selenium/selenium-result/resources.NotLoggedUser.test.html.failed.html");
        Thread.sleep(5000);
        if (!resultFile.exists()) {
            fail("Selenium auto test passed !");
        }
    }

    @Test
    public void testRunSeleniumServerSeleniumIDE() throws MojoExecutionException, IOException, InterruptedException {
        File seleniumScript = new File(projectTestPathFile.getAbsolutePath() + "/resources/seleniumIDE.test.html");
        WebWindow window = firephoque.openWindow(TestRunnerUtils.getTestrunnerActionFullUrl(seleniumScript,
                "http://localhost:7777", 7777, projectTestPathFile.getAbsolutePath()), "headless");

        File resultFile = new File(FileUtils.getTempDirectoryPath()
                + "/selenium/selenium-result/resources.seleniumIDE.test.html.passed.html");
        Thread.sleep(5000);
        if (!resultFile.exists()) {
            fail("Selenium auto test failed !");
        }
    }

    @After
    public void tearDown() {
        firephoque.closeAllWindows();
        server.interrupt();
        try {
            server.join();
        } catch (InterruptedException e) {
        }
    }
}
