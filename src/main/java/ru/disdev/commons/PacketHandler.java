package ru.disdev.commons;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.disdev.commons.annotations.Key;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PacketHandler extends SimpleChannelInboundHandler<Object> {

    private final Consumer<Channel> onConnection;
    private final Consumer<Channel> onDisconnection;
    private final BiConsumer<Channel, Throwable> onException;
    private final BiConsumer<Channel, Object> notMappedDataConsumer;
    private final Map<Integer, BiConsumer<Channel, ? extends Packet>> consumersMap;

    public PacketHandler(Consumer<Channel> onConnection,
                         Consumer<Channel> onDisconnection,
                         BiConsumer<Channel, Throwable> onException,
                         BiConsumer<Channel, Object> notMappedDataConsumer,
                         Map<Integer, BiConsumer<Channel, ? extends Packet>> consumersMap) {
        this.onConnection = onConnection;
        this.onDisconnection = onDisconnection;
        this.onException = onException;
        this.notMappedDataConsumer = notMappedDataConsumer;
        this.consumersMap = consumersMap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (onConnection != null) {
            onConnection.accept(ctx.channel());
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (onDisconnection != null) {
            onDisconnection.accept(ctx.channel());
        }
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (onException != null) {
            onException.accept(ctx.channel(), cause);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Packet) {
            Class<? extends Packet> clazz = ((Packet) msg).getClass();
            Key key = clazz.getAnnotation(Key.class);
            if (key != null) {
                BiConsumer consumer = consumersMap.get(key.value());
                if (consumer != null) {
                    Packet packet = clazz.cast(msg);
                    consumer.accept(ctx.channel(), packet);
                }
            }
        } else {
            if (notMappedDataConsumer != null) {
                notMappedDataConsumer.accept(ctx.channel(), msg);
            }
        }
    }
}
