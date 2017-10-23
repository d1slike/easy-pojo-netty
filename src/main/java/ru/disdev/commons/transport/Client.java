package ru.disdev.commons.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import ru.disdev.commons.PacketHandler;
import ru.disdev.commons.codec.PacketDecoder;
import ru.disdev.commons.codec.PacketEncoder;
import ru.disdev.commons.configuration.Configuration;
import ru.disdev.commons.configuration.Handlers;

public class Client extends Transport<Client> {

    private final EventLoopGroup group;

    private Client(Configuration configuration, Handlers handlers) {
        super(configuration, handlers);
        group = new NioEventLoopGroup(configuration.getBossGroupThreadCount(), threadFactory());
    }

    @Override
    public Client start() {
        new Bootstrap()
                .channel(NioSocketChannel.class)
                .group(group)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new PacketDecoder(
                                        allowedPackets,
                                        configuration.getStringCharset(),
                                        configuration.isUseLE(),
                                        handlers.getNotMappedDataFunction()),
                                new PacketEncoder(
                                        configuration.getStringCharset(),
                                        configuration.isUseLE()),
                                new PacketHandler(
                                        handlers.getOnConnection(),
                                        handlers.getOnDisconnection(),
                                        handlers.getOnException(),
                                        handlers.getNotMappedDataConsumer(),
                                        handlersMap
                                ));
                    }
                }).connect(hostName, port);
        return this;
    }

    @Override
    public void shutdown() {
        group.shutdownGracefully();
    }

    @Override
    protected Client getThis() {
        return this;
    }

    public static ClientBuilder builder() {
        return new ClientBuilder()
                .daemonThreads(true)
                .workerGroupThreadCount(1);
    }

    public static class ClientBuilder extends TransportBuilder<Client, ClientBuilder> {

        @Override
        public Client build() {
            return new Client(configuration, handlers);
        }

        @Override
        public ClientBuilder workerGroupThreadCount(int count) {
            configuration.setBossGroupThreadCount(count);
            return this;
        }

        @Override
        protected ClientBuilder getThis() {
            return this;
        }
    }
}
