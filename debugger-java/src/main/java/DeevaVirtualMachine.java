package deeva;

import deeva.io.DeevaIOService;

public class DeevaVirtualMachine {
    private final DeevaConfig config;

    public DeevaVirtualMachine(DeevaConfig config) {
        this.config = config;
    }

    public void start() {

    }

    public void stop() {

    }

    public void stepInto() {

    }

    public void stepOver() {

    }

    public void stepReturn() {

    }

    public void resume() {

    }

    public DeevaIOService getIOService() {
        return null;
    }

    public DeevaConfig getConfig() {
        return config;
    }
}
