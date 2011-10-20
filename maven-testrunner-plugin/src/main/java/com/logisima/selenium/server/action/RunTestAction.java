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
package com.logisima.selenium.server.action;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

import com.gargoylesoftware.htmlunit.WebClient;
import com.logisima.selenium.utils.TestRunnerUtils;

/**
 * Server action to generate the selenium test file.
 * 
 * @author bsimard
 * 
 */
public class RunTestAction extends ServerAction {

    /**
     * Port of the server
     */
    private Integer port;

    /**
     * Constructor.
     * 
     * @param request
     * @param chunksBuf
     * @param baseApplicationUrl
     * @param testSourceDirectory
     * @param outputDirectory
     * @param port
     */
    public RunTestAction(HttpRequest request, StringBuilder chunksBuf, URL baseApplicationUrl,
            File testSourceDirectory, File outputDirectory, Integer port) {
        super(request, chunksBuf, baseApplicationUrl, testSourceDirectory, outputDirectory);
        this.port = port;
    }

    @Override
    public void execute() {
        this.contentType = "text/html; charset=utf-8";

        // construction of the test file
        String testPath = request.getUri().replaceFirst("/runtest", "");
        File testFile = new File(testSourceDirectory + testPath.split("[?]")[0]);
        // the testrunner url to call
        try {
            // we construct the url to call
            URL url = TestRunnerUtils.getTestrunnerActionFullUrl(testFile, baseApplicationUrl.toString(), port,
                    testSourceDirectory.toString());

            String displayName = TestRunnerUtils.getTestDisplayName(testFile, testSourceDirectory.toString());

            // let's go to run test with firephoque
            WebClient firephoque = TestRunnerUtils.getWebClient();
            firephoque.openWindow(url, "headless");
            firephoque.waitForBackgroundJavaScript(5 * 60 * 1000);

            // to know if a test passed or failed, we test if there is a file testName.PASSED|FAILED.html
            int retry = 0;
            while (retry < 5) {
                if (new File(outputDirectory + "/selenium-result/" + displayName + "." + "passed.html").exists()) {
                    File resultFile = new File(outputDirectory + "/selenium-result/" + displayName + "."
                            + "passed.html");
                    this.status = HttpResponseStatus.OK;
                    this.content = ChannelBuffers.copiedBuffer(TestRunnerUtils.getFileContent(resultFile),
                            CharsetUtil.UTF_8);
                    break;
                }
                else if (new File(outputDirectory + "/selenium-result/" + displayName + "." + "failed.html").exists()) {
                    File resultFile = new File(outputDirectory + "/selenium-result/" + displayName + "."
                            + "failed.html");
                    this.status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
                    this.content = ChannelBuffers.copiedBuffer(TestRunnerUtils.getFileContent(resultFile),
                            CharsetUtil.UTF_8);
                    break;
                }
                else {
                    if (retry++ == 4) {
                        this.status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
                        this.content = ChannelBuffers.copiedBuffer("Selenium test ERROR", CharsetUtil.UTF_8);
                        break;
                    }
                }
                firephoque.closeAllWindows();
            }

        } catch (IOException e) {
            this.status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
            this.content = ChannelBuffers.copiedBuffer(e.toString(), CharsetUtil.UTF_8);
        }
    }
}
