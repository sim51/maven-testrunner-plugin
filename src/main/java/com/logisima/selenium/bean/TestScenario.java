package com.logisima.selenium.bean;

import java.util.ArrayList;
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
     */
    public TestScenario(String title) {
        this.title = title;
        commands = new ArrayList<SeleniumCommand>();
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
