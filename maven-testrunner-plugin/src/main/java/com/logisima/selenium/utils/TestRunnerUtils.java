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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.maven.plugin.MojoExecutionException;

import com.gargoylesoftware.htmlunit.AlertHandler;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ConfirmHandler;
import com.gargoylesoftware.htmlunit.DefaultPageCreator;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.PromptHandler;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.javascript.host.Window;
import com.logisima.selenium.bean.TestScenario;
import com.logisima.selenium.template.SeleniumTestRunnerTemplate;

/**
 * Utils class for the testrunner application (or related to).
 * 
 * @author bsimard
 * 
 */
public class TestRunnerUtils {

    /**
     * Retrieve the path of the selenium-testrunner-template jar.
     * 
     * @return path of the jar
     */
    public static String getSeleniumTemplateJarPath() {
        URL location = SeleniumTestRunnerTemplate.class.getResource('/'
                + SeleniumTestRunnerTemplate.class.getName().replace(".", "/") + ".class");
        String jarPath = location.getPath();
        return jarPath.substring("file:".length(), jarPath.lastIndexOf("!"));
    }

    /**
     * Copy the selnium application into a specific directory.
     * 
     * @param seleniumTarget
     * @throws MojoExecutionException
     */
    public static void copySeleniumToDir(File seleniumTarget) throws MojoExecutionException {
        // retrieve File of the selenium-testrunner-template
        File templateZipFile = new File(getSeleniumTemplateJarPath());

        // unzip the template jar
        ZipFile myZip;
        try {
            myZip = new ZipFile(templateZipFile);
            unzipFileIntoDirectory(myZip, seleniumTarget);
        } catch (ZipException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Unzip <code>zipFile</code> into <code>jiniHomeParentDir</code> directory.
     * 
     * @param zipFile
     * @param jiniHomeParentDir
     */
    public static void unzipFileIntoDirectory(ZipFile zipFile, File jiniHomeParentDir) {
        Enumeration files = zipFile.entries();
        File f = null;
        FileOutputStream fos = null;

        while (files.hasMoreElements()) {
            try {
                ZipEntry entry = (ZipEntry) files.nextElement();
                InputStream eis = zipFile.getInputStream(entry);
                byte[] buffer = new byte[1024];
                int bytesRead = 0;

                f = new File(jiniHomeParentDir.getAbsolutePath() + File.separator + entry.getName());

                if (entry.isDirectory()) {
                    f.mkdirs();
                    continue;
                }
                else {
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                }

                fos = new FileOutputStream(f);

                while ((bytesRead = eis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                // ignore
                continue;
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
        }
    }

    /**
     * Return a <code>WebClient</code> weely initialized.
     * 
     * @return a webclient
     */
    public static WebClient getWebClient() {
        WebClient firephoque = new WebClient(BrowserVersion.INTERNET_EXPLORER_8);

        // silent mode
        firephoque.setCssErrorHandler(new SilentCssErrorHandler());

        firephoque.setPageCreator(new DefaultPageCreator() {

            @Override
            public Page createPage(WebResponse wr, WebWindow ww) throws IOException {
                Page page = createHtmlPage(wr, ww);
                return page;
            }
        });
        firephoque.setThrowExceptionOnFailingStatusCode(false);
        firephoque.setAlertHandler(new AlertHandler() {

            public void handleAlert(Page page, String string) {
                try {
                    Window window = (Window) page.getEnclosingWindow().getScriptObject();
                    window.custom_eval("parent.selenium.browserbot.recordedAlerts.push('" + string.replace("'", "\\'")
                            + "');");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        firephoque.setConfirmHandler(new ConfirmHandler() {

            public boolean handleConfirm(Page page, String string) {
                try {
                    Window window = (Window) page.getEnclosingWindow().getScriptObject();
                    Object result = window.custom_eval("parent.selenium.browserbot.recordedConfirmations.push('"
                            + string.replace("'", "\\'") + "');"
                            + "var result = parent.selenium.browserbot.nextConfirmResult;"
                            + "parent.selenium.browserbot.nextConfirmResult = true;" + "result");
                    return (Boolean) result;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        });
        firephoque.setPromptHandler(new PromptHandler() {

            public String handlePrompt(Page page, String string) {
                try {
                    Window window = (Window) page.getEnclosingWindow().getScriptObject();
                    Object result = window.custom_eval("parent.selenium.browserbot.recordedPrompts.push('"
                            + string.replace("'", "\\'")
                            + "');"
                            + "var result = !parent.selenium.browserbot.nextConfirmResult ? null : parent.selenium.browserbot.nextPromptResult;"
                            + "parent.selenium.browserbot.nextConfirmResult = true;"
                            + "parent.selenium.browserbot.nextPromptResult = '';" + "result");
                    return (String) result;
                } catch (Exception e) {
                    e.printStackTrace();
                    return "";
                }
            }
        });
        firephoque.setThrowExceptionOnScriptError(false);
        return firephoque;
    }

    /**
     * Return the name of selenium script (relative path)
     * 
     * @param file
     * @return
     */
    public static String getTestName(File test, String testSourceDirectory) {
        String testName = test.getAbsolutePath();
        testName = testName.substring(testSourceDirectory.length(), testName.length());
        testName.replaceFirst(".", "");
        return testName;
    }

    /**
     * Return the display name of selenium script
     * 
     * @param file
     * @return
     */
    public static String getTestDisplayName(File test, String testSourceDirectory) {
        String testName = test.getAbsolutePath();
        testName = testName.substring(testSourceDirectory.length(), testName.length());
        testName = testName.replaceAll("/", ".").replaceFirst(".", "");
        return testName;
    }

    /**
     * Method get the url of the test action for a selenium script.
     * 
     * @param test
     * @return
     */
    public static String getTestActionUrl(File test, String testSourceDirectory) {
        // test?test=$stringUtils.sub($test.path, $testSourceDirectory, '')
        String url = "/test";
        url += getTestName(test, testSourceDirectory);
        return url;
    }

    /**
     * Method get the url of the suite action for a selenium script.
     * 
     * @param test
     * @return
     */
    public static String getSuiteActionUrl(File test, String testSourceDirectory) {
        // suite.action%3Ftest%3D$stringUtils.sub($test.path, $testSourceDirectory, '')
        String url = "/suite";
        url += getTestName(test, testSourceDirectory);
        return url;
    }

    /**
     * Method get the url of the result action for a selenium script.
     * 
     * @param test
     * @param testSourceDirectory
     * @return
     */
    public static String getResultActionUrl(File test, String testSourceDirectory) {
        String url = "/testresult";
        url += getTestName(test, testSourceDirectory);
        return url;
    }

    /**
     * Methode to get the url of the testrunner action for a selenium script.
     * 
     * @param test
     * @param port
     * @param baseApplicationUrl
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getTestrunnerActionUrl(File test, String baseApplicationUrl, String testSourceDirectory)
            throws UnsupportedEncodingException {
        String url = "/TestRunner.html?baseUrl=";
        url += baseApplicationUrl;
        url += "&auto=true&test=";
        url += URLEncoder.encode(getSuiteActionUrl(test, testSourceDirectory), "UTF-8");
        url += "&resultsUrl=";
        url += URLEncoder.encode(getResultActionUrl(test, testSourceDirectory), "UTF-8");
        return url;
    }

    /**
     * Methode to get the full url of the testrunner action for a selenium script.
     * 
     * @param test
     * @param baseApplicationUrl
     * @param port
     * @param testSourceDirectory
     * @return
     * @throws IOException
     */
    public static URL getTestrunnerActionFullUrl(File test, String baseApplicationUrl, int port,
            String testSourceDirectory) throws IOException {
        String url = "http://localhost:" + port;
        url += getTestrunnerActionUrl(test, baseApplicationUrl, testSourceDirectory);
        return new URL(url);
    }

    /**
     * Create and write <code>content</code> to the <code>file</code>.
     * 
     * @param content
     * @param file
     * @throws IOException
     */
    public static void writeContent(CharSequence content, File file) throws IOException {
        FileWriter fstream = new FileWriter(file.getPath());
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(content.toString());
        out.close();
    }

    /**
     * Parse a play! test file and return a list of test scenario.
     * 
     * @param test
     * @return
     * @throws IOException
     */
    public static List<TestScenario> parseTestFile(File test) throws IOException {
        SeleniumParser parser = new SeleniumParser(test);
        parser.execute();
        return parser.getScenarios();
    }

    /**
     * Method to khnow if a file is selenium IDE file (true)or a play! one (false).
     * 
     * @param test file to test
     * @param
     * @return
     * @throws IOException
     */
    public static Boolean IsSeleniumIdeFile(File test) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(test));
        String strLine;
        while ((strLine = br.readLine()) != null) {
            if (strLine.contains("selenium.base")) {
                br.close();
                return Boolean.TRUE;
            }
        }
        br.close();
        return Boolean.FALSE;
    }

    public static String getFileContent(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String content = "";
        String strLine;
        while ((strLine = br.readLine()) != null) {
            content += strLine + "\n";
        }
        return content;
    }
}
