package ru.disdev.commons.transport;

import io.netty.channel.Channel;
import ru.disdev.commons.Key;
import ru.disdev.commons.Packet;
import ru.disdev.commons.configuration.Configuration;
import ru.disdev.commons.configuration.Handlers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public abstract class Transport<C> {

    protected final String hostName;
    protected final int port;
    protected final Handlers handlers;
    protected final Configuration configuration;
    protected final Map<Integer, Class<? extends Packet>> allowedPackets = new ConcurrentHashMap<>();
    protected final Map<Integer, BiConsumer<Channel, ? extends Packet>> handlersMap = new ConcurrentHashMap<>();

    Transport(Configuration configuration,
              Handlers handlers) {
        hostName = configuration.getHostName();
        port = configuration.getPort();
        this.handlers = handlers;
        this.configuration = configuration;
    }

    public abstract C start();

    public <T extends Packet> C subscribe(Class<T> clazz, BiConsumer<Channel, T> consumer) {
        Key key = clazz.getAnnotation(Key.class);
        if (key != null) {
            int value = key.value();
            allowedPackets.put(value, clazz);
            handlersMap.put(value, consumer);
        }
        return getThis();
    }

    public C unsubscribe(Class<? extends Packet> target) {
        Key key = target.getAnnotation(Key.class);
        if (key != null) {
            int value = key.value();
            allowedPackets.remove(value);
            handlersMap.remove(value);
        }
        return getThis();
    }

    public C unsubsribeAll() {
        allowedPackets.clear();
        handlersMap.clear();
        return getThis();
    }

    public abstract void shutdown();

    protected abstract C getThis();

}
