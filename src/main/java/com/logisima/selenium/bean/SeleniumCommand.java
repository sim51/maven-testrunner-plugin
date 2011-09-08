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
