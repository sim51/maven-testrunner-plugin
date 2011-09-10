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
