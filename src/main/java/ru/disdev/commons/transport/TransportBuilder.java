package ru.disdev.commons.transport;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import ru.disdev.commons.configuration.Configuration;
import ru.disdev.commons.configuration.Handlers;

import java.nio.charset.Charset;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class TransportBuilder<T extends Transport, B> {

    protected Configuration configuration = new Configuration();
    protected Handlers handlers = new Handlers();

    public abstract T build();

    protected abstract B getThis();

    public B onConnection(Consumer<Channel> consumer) {
        handlers.setOnConnection(consumer);
        return getThis();
    }

    public B onDisconnection(Consumer<Channel> consumer) {
        handlers.setOnDisconnection(consumer);
        return getThis();
    }

    public B onException(BiConsumer<Channel, Throwable> consumer) {
        handlers.setOnException(consumer);
        return getThis();
    }

    public B onNotMappedData(BiConsumer<Channel, Object> consumer) {
        handlers.setNotMappedDataConsumer(consumer);
        return getThis();
    }

    public B notMappedDataFunction(Function<ByteBuf, Object> function) {
        handlers.setNotMappedDataFunction(function);
        return getThis();
    }

    public B bossGroupThreadCount(int count) {
        configuration.setBossGroupThreadCount(count);
        return getThis();
    }

    public B workerGroupThreadCount(int count) {
        configuration.setWorkerGroupThreadCount(count);
        return getThis();
    }

    public B port(int port) {
        configuration.setPort(port);
        return getThis();
    }

    public B hostName(String host) {
        configuration.setHostName(host);
        return getThis();
    }

    public B useLE(boolean value) {
        configuration.setUseLE(value);
        return getThis();
    }

    public B stringCharset(Charset charset) {
        configuration.setStringCharset(charset);
        return getThis();
    }

    public B daemonThreads(boolean value) {
        configuration.setDaemonThreads(value);
        return getThis();
    }
}
