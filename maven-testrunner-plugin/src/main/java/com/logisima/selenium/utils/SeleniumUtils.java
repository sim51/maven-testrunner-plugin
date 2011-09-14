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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.logisima.selenium.bean.TestScenario;

public class SeleniumUtils {

    private List<File> tests;

    /**
     * Constructor.
     */
    public SeleniumUtils() {
        super();
        this.tests = new ArrayList<File>();
    }

    /**
     * @return the tests
     */
    public List<File> getTests() {
        return tests;
    }

    /**
     * Scan directory to retrieve selenium test.
     * 
     * @param dir
     */
    public void scanForSeleniumTests(File dir) {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                scanForSeleniumTests(f);
            }
            else if (f.getName().endsWith(".test.html")) {
                tests.add(f);
            }
        }
    }

    /**
     * Parse a test file and return a list of test scenario.
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

    public static URL testFileToTestrunnerUri(String testFile, String baseUrl, int port) throws IOException {
        String url = "http://localhost:" + port + "/TestRunner.html";
        String urlParams = "?auto=true&resultsUrl=";
        urlParams += URLEncoder.encode("/testresult/" + testFile, "utf-8");
        urlParams += "&baseUrl=";
        urlParams += URLEncoder.encode(baseUrl, "utf-8");
        urlParams += "&test=" + URLEncoder.encode("/suite.action?test=" + testFile, "utf-8");
        URL rUrl = new URL(url + urlParams);
        return rUrl;
    }

    public static void writeContent(CharSequence content, File file) throws IOException {
        FileWriter fstream = new FileWriter(file.getPath());
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(content.toString());
        out.close();
    }

}
