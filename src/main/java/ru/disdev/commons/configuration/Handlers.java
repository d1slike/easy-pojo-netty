package ru.disdev.commons.configuration;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class Handlers {

    private Consumer<Channel> onConnection = c -> {
    };
    private Consumer<Channel> onDisconnection = c -> {
    };
    private BiConsumer<Channel, Throwable> onException = (c, t) -> {
    };
    private BiConsumer<Channel, Object> notMappedDataConsumer = (c, s) -> {
    };
    private Function<ByteBuf, Object> notMappedDataFunction = b -> null;

    public Consumer<Channel> getOnConnection() {
        return onConnection;
    }

    public void setOnConnection(Consumer<Channel> onConnection) {
        this.onConnection = onConnection;
    }

    public Consumer<Channel> getOnDisconnection() {
        return onDisconnection;
    }

    public void setOnDisconnection(Consumer<Channel> onDisconnection) {
        this.onDisconnection = onDisconnection;
    }

    public BiConsumer<Channel, Throwable> getOnException() {
        return onException;
    }

    public void setOnException(BiConsumer<Channel, Throwable> onException) {
        this.onException = onException;
    }

    public Function<ByteBuf, Object> getNotMappedDataFunction() {
        return notMappedDataFunction;
    }

    public void setNotMappedDataFunction(Function<ByteBuf, Object> notMappedDataFunction) {
        this.notMappedDataFunction = notMappedDataFunction;
    }

    public BiConsumer<Channel, Object> getNotMappedDataConsumer() {
        return notMappedDataConsumer;
    }

    public void setNotMappedDataConsumer(BiConsumer<Channel, Object> notMappedDataConsumer) {
        this.notMappedDataConsumer = notMappedDataConsumer;
    }
}
