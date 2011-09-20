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

/**
 * Bean that represent a selenium command.
 * 
 * @author bsimard
 * 
 */
public class SeleniumCommand {

    /**
     * Selenium command.
     */
    private String command;
    /**
     * Selenium target.
     */
    private String target;
    /**
     * Selenium value.
     */
    private String value;

    /**
     * Constructor
     */
    public SeleniumCommand() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param command
     * @param target
     * @param value
     */
    public SeleniumCommand(String command, String target, String value) {
        this.command = command;
        this.target = target;
        this.value = value;
    }

    /**
     * @param command
     * @param value
     */
    public SeleniumCommand(String command, String target) {
        super();
        this.command = command;
        this.value = "";
        this.target = target;
    }

    /**
     * @param command
     */
    public SeleniumCommand(String command) {
        super();
        this.command = command;
        this.target = "";
        this.value = "";
    }

    /**
     * @return the command
     */
    public String getCommand() {
        return command;
    }

    /**
     * @param command the command to set
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

}
