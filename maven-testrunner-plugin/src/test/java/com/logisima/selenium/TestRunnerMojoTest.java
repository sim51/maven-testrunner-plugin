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
package com.logisima.selenium;

import java.io.File;
import java.net.URL;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.After;
import org.junit.Before;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.logisima.selenium.utils.TestRunnerUtils;

public class TestRunnerMojoTest extends AbstractMojoTestCase {

    private TestRunnerMojo mojo;

    @Before
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * @throws Exception
     */
    public void testMojoGoal() throws Exception {
        File testPom = new File(getBasedir(), "src/test/resources/mojotest/pom.xml");
        mojo = (TestRunnerMojo) lookupMojo("run", testPom);
        assertNotNull(mojo);

        class MojoThread extends Thread {

            private TestRunnerMojo mojo;

            public MojoThread(TestRunnerMojo mojo) {
                this.mojo = mojo;
            }

            public void run() {
                try {
                    mojo.execute();
                } catch (MojoExecutionException e) {
                    Thread.currentThread().interrupt();
                    fail(e.getMessage());
                } catch (MojoFailureException e) {
                    Thread.currentThread().interrupt();
                    fail(e.getMessage());
                }
            }

            public void interrupt() {
                super.interrupt();
                try {
                    mojo.interrupt();
                } catch (InterruptedException e) {
                }
            }
        }
        MojoThread mojoThread = new MojoThread(mojo);
        mojoThread.start();

        WebClient firephoque = TestRunnerUtils.getWebClient();
        URL url = new URL("http://localhost:7777");
        HtmlPage page = firephoque.getPage(url);

        assertEquals("Maven Selenium testrunner plugin", page.getTitleText());

        firephoque.closeAllWindows();
        mojoThread.interrupt();
        mojoThread.join();

    }

    @After
    public void tearDown() {
        try {
            super.tearDown();
            mojo.interrupt();
        } catch (Exception e) {
        }
    }
}
