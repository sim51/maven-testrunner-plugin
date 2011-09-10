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
