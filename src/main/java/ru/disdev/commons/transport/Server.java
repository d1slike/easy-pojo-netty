package ru.disdev.commons.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ru.disdev.commons.PacketHandler;
import ru.disdev.commons.codec.PacketDecoder;
import ru.disdev.commons.codec.PacketEncoder;
import ru.disdev.commons.configuration.Configuration;
import ru.disdev.commons.configuration.Handlers;

public class Server extends Transport<Server> {

    protected final EventLoopGroup bossGroup;
    protected final EventLoopGroup workerGroup;

    private Server(Configuration configuration, Handlers handlers) {
        super(configuration, handlers);
        bossGroup = new NioEventLoopGroup(configuration.getBossGroupThreadCount());
        workerGroup = new NioEventLoopGroup(configuration.getWorkerGroupThreadCount());
    }

    @Override
    public Server start() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new PacketDecoder(
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
                                )
                        );
                    }
                }).bind(hostName, port);
        return this;
    }

    @Override
    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    @Override
    protected Server getThis() {
        return this;
    }

    public static ServerBuilder builder() {
        return new ServerBuilder();
    }

    public static class ServerBuilder extends TransportBuilder<Server, ServerBuilder> {

        @Override
        public Server build() {
            return new Server(configuration, handlers);
        }

        @Override
        protected ServerBuilder getThis() {
            return this;
        }
    }
}
