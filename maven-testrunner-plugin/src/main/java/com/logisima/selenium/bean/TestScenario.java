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
package com.logisima.selenium.bean;

import java.util.List;

/**
 * Bean that represent a test for selenium test template.
 * 
 * @author bsimard
 * 
 */
public class TestScenario {

    private String                title;
    private List<SeleniumCommand> commands;

    /**
     * Constructor.
     * 
     * @param title
     * @param commands
     */
    public TestScenario(String title, List<SeleniumCommand> commands) {
        this.title = title;
        this.commands = commands;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the commands
     */
    public List<SeleniumCommand> getCommands() {
        return commands;
    }

}
