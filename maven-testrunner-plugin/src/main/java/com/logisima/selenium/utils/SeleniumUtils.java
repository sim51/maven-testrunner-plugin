package com.logisima.selenium.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(os, "utf-8"));
            printWriter.println(content);
            printWriter.flush();
            os.flush();
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (os != null)
                    os.close();
            } catch (Exception e) {
                //
            }
        }
    }

}
