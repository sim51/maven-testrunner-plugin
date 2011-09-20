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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import com.logisima.selenium.bean.SeleniumCommand;
import com.logisima.selenium.bean.TestScenario;

public class SeleniumParserTest extends TestCase {

    @Test
    public void testSeleniumParser1() throws IOException {
        URL url = SeleniumParserTest.class.getResource("/Application.test.html");
        File script = new File(url.getFile());
        List<TestScenario> scenarii = TestRunnerUtils.parseTestFile(script);

        List<TestScenario> scenariiExpected = new ArrayList<TestScenario>();
        List<SeleniumCommand> scenario1Command = new ArrayList<SeleniumCommand>();
        scenario1Command.add(new SeleniumCommand("deleteAllVisibleCookies"));
        scenario1Command.add(new SeleniumCommand("open", "/"));
        scenario1Command.add(new SeleniumCommand("assertTitle", "Welcome"));
        scenariiExpected.add(new TestScenario("index", scenario1Command));

        List<SeleniumCommand> scenario2Command = new ArrayList<SeleniumCommand>();
        scenario2Command.add(new SeleniumCommand("open", "/login"));
        scenario2Command.add(new SeleniumCommand("waitForPageToLoad", "1000"));
        scenario2Command.add(new SeleniumCommand("assertTitle", "CAS Mock Server - Login"));
        scenario2Command.add(new SeleniumCommand("type", "login", "admin"));
        scenario2Command.add(new SeleniumCommand("type", "password", "admin"));
        scenario2Command.add(new SeleniumCommand("clickAndWait", "//input[@value='Login']"));
        scenario2Command.add(new SeleniumCommand("waitForPageToLoad", "100"));
        scenario2Command.add(new SeleniumCommand("assertTextPresent", "Welcome admin"));
        scenariiExpected.add(new TestScenario("login", scenario2Command));

        for (int i = 0; i < scenariiExpected.size(); i++) {
            TestScenario scenarioExpected = scenariiExpected.get(i);
            TestScenario scenario = scenarii.get(i);
            assertEquals(scenarioExpected.getTitle(), scenario.getTitle());
            for (int j = 0; j < scenarioExpected.getCommands().size(); j++) {
                SeleniumCommand commandExpected = scenarioExpected.getCommands().get(j);
                SeleniumCommand command = scenario.getCommands().get(j);
                assertEquals("Bad command", commandExpected.getCommand(), command.getCommand());
                assertEquals("Bad value", commandExpected.getValue(), command.getValue());
                assertEquals("Bad target", commandExpected.getTarget(), command.getTarget());
            }
        }
    }

}
