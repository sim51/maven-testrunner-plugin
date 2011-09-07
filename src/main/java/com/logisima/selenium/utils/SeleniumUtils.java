package com.logisima.selenium.utils;

import java.io.File;
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
     */
    public static List<TestScenario> parseTestFile(File test) {
        return null;
    }
}
