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

import com.logisima.selenium.TestRunnerMojo;

public class NettyServerScanner extends Thread {

    private final TestRunnerMojo mojo;
    private final int            port;

    /**
     * @param mojo
     * @param port
     */
    public NettyServerScanner(TestRunnerMojo mojo, int port) {
        super();
        this.mojo = mojo;
        this.port = port;
    }

    public void run() {
        while (true) {
            // TODO: check if the server is UP
            getSomeSleep();
        }
    }

    private void getSomeSleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            mojo.getLog().debug(e);
        }
    }

}
