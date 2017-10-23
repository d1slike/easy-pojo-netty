package ru.disdev.commons.configuration;

import java.nio.charset.Charset;

public class Configuration {
    private int bossGroupThreadCount = 1;
    private int workerGroupThreadCount = 2;
    private String hostName = "localhost";
    private int port = 8080;
    private Charset stringCharset = Charset.forName("UTF-8");
    private boolean useLE = false;
    private boolean daemonThreads = false;

    public int getBossGroupThreadCount() {
        return bossGroupThreadCount;
    }

    public void setBossGroupThreadCount(int bossGroupThreadCount) {
        this.bossGroupThreadCount = bossGroupThreadCount;
    }

    public int getWorkerGroupThreadCount() {
        return workerGroupThreadCount;
    }

    public void setWorkerGroupThreadCount(int workerGroupThreadCount) {
        this.workerGroupThreadCount = workerGroupThreadCount;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Charset getStringCharset() {
        return stringCharset;
    }

    public void setStringCharset(Charset stringCharset) {
        this.stringCharset = stringCharset;
    }

    public boolean isUseLE() {
        return useLE;
    }

    public void setUseLE(boolean useLE) {
        this.useLE = useLE;
    }

    public boolean isDaemonThreads() {
        return daemonThreads;
    }

    public void setDaemonThreads(boolean daemonThreads) {
        this.daemonThreads = daemonThreads;
    }
}
