package com.logisima.selenium.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.logisima.selenium.bean.SeleniumCommand;
import com.logisima.selenium.bean.TestScenario;

public class SeleniumParser {

    /**
     * File to parse.
     */
    private File                  file;

    /**
     * Scenarii of test file
     */
    private List<TestScenario>    scenarios;

    // local variable used to store parsed data until there are pushed into the object
    private String                title;
    private List<SeleniumCommand> commands;

    /**
     * @param file
     */
    public SeleniumParser(File file) {
        super();
        this.file = file;
        scenarios = new ArrayList<TestScenario>();
    }

    /**
     * Method that execute the parsing of the test file.
     * 
     * @throws IOException
     */
    public void execute() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String strLine;
        while ((strLine = br.readLine()) != null) {
            parseLine(strLine);
        }
        br.close();
    }

    /**
     * Parse line by line, and
     * 
     * @param line
     */
    private void parseLine(String line) {
        // if line contains "#{selenium", begin of new scenario
        if (line.trim().startsWith("#{selenium")) {
            // initialize of temp var
            title = null;
            commands = new ArrayList<SeleniumCommand>();
            // retrieve scenario title
            int quoteFirstIndex = line.indexOf("'");
            int quoteLastIndex = line.lastIndexOf("'");
            this.title = line.substring(quoteFirstIndex + 1, quoteLastIndex);
        }
        else {
            // end of scenario
            if (line.trim().startsWith("#{/selenium}")) {
                TestScenario scenario = new TestScenario(this.title, this.commands);
                this.scenarios.add(scenario);
                this.title = null;
                this.commands = null;
            }
            // it's a standard line with a selenium command (or a blank line)
            else {
                if (!line.trim().isEmpty()) {
                    SeleniumCommand command = new SeleniumCommand();

                    int paraFirstIndex = line.indexOf("(");
                    int paraLastIndex = line.lastIndexOf(")");

                    String cmd = line.substring(0, paraFirstIndex).trim();
                    command.setCommand(cmd);

                    String temp = line.substring(paraFirstIndex + 1, paraLastIndex);
                    String[] tempTab = temp.split("'[\\s]*,[\\s]*'");

                    // find target
                    String target;
                    if (tempTab.length == 1) {
                        target = temp.replaceAll("'", "");
                    }
                    else {
                        target = tempTab[0].trim().substring(1);
                    }
                    command.setTarget(target);

                    String value = "";
                    if (tempTab.length == 2) {
                        value = tempTab[1].trim().substring(0, tempTab[1].trim().length() - 1);
                        command.setValue(value);
                    }
                    command.setValue(value);
                    this.commands.add(command);
                }
            }
        }
    }

    /**
     * @return the scenarios
     */
    public List<TestScenario> getScenarios() {
        return scenarios;
    }

}
