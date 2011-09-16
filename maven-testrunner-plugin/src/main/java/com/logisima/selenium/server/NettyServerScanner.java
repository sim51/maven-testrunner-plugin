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
package com.logisima.selenium.server;

import org.apache.maven.plugin.AbstractMojo;

/**
 * 
 * @author bsimard
 * 
 */
public class NettyServerScanner extends Thread {

    /**
     * The mojo that call this class (to get the logger)
     */
    private final AbstractMojo mojo;

    /**
     * port of the server
     */
    private final int          port;

    /**
     * Constructor.
     * 
     * @param mojo
     * @param port
     */
    public NettyServerScanner(AbstractMojo mojo, int port) {
        super();
        this.mojo = mojo;
        this.port = port;
    }

    /**
     * Run method of the thread.
     */
    public void run() {
        while (true) {
            // TODO: check if the server is UP
            getSomeSleep();
        }
    }

    /**
     * Get some sleep for the thread ...
     */
    private void getSomeSleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            mojo.getLog().debug(e);
        }
    }

}
