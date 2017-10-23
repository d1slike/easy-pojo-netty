package ru.disdev.commons.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.apache.commons.lang3.reflect.FieldUtils;
import ru.disdev.commons.Packet;
import ru.disdev.commons.annotations.Ignore;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

public class PacketDecoder extends ReplayingDecoder<Packet> {

    private final Map<Integer, Class<? extends Packet>> allowedPackets;
    private final Charset stringCharset;
    private final boolean useLE;
    private final Function<ByteBuf, Object> notMappedDataFunction;

    public PacketDecoder(Map<Integer, Class<? extends Packet>> allowedPackets,
                         Charset stringCharset, boolean useLE,
                         Function<ByteBuf, Object> notMappedDataFunction) {
        this.allowedPackets = allowedPackets;
        this.stringCharset = stringCharset;
        this.useLE = useLE;
        this.notMappedDataFunction = notMappedDataFunction;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int packetKey = readInt(in.copy(0, 4));
        if (allowedPackets.containsKey(packetKey)) {
            readInt(in);
            Class<? extends Packet> clazz = allowedPackets.get(packetKey);
            try {
                Packet packet = clazz.newInstance();
                FieldUtils.getAllFieldsList(clazz).stream()
                        .filter(field -> {
                            Ignore ignore = field.getAnnotation(Ignore.class);
                            return ignore == null || !ignore.read();
                        })
                        .forEach(field -> {
                            try {
                                field.setAccessible(true);
                                Class<?> type = field.getType();
                                if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
                                    int value = readInt(in);
                                    field.set(packet, value);
                                } else if (String.class.isAssignableFrom(type)) {
                                    String value = readSting(in);
                                    field.set(packet, value);
                                } else if (UUID.class.isAssignableFrom(type)) {
                                    String stringValue = readSting(in);
                                    UUID value = UUID.fromString(stringValue);
                                    field.set(packet, value);
                                } else if (type.isEnum()) {
                                    String stringValue = readSting(in);
                                    Stream.of(type.getEnumConstants())
                                            .filter(v -> v.toString().equals(stringValue))
                                            .findFirst()
                                            .ifPresent(v -> {
                                                try {
                                                    field.set(packet, stringValue);
                                                } catch (IllegalAccessException ignored) {

                                                }
                                            });
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });
                out.add(packet);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            if (notMappedDataFunction != null) {
                Object result = notMappedDataFunction.apply(in);
                if (result != null) out.add(result);
            }
        }
    }

    private String readSting(ByteBuf in) {
        int length = readInt(in);
        byte[] array = new byte[length];
        in.readBytes(array, 0, length);
        return new String(array, stringCharset);
    }

    private int readInt(ByteBuf in) {
        return useLE ? in.readIntLE() : in.readInt();
    }
}
