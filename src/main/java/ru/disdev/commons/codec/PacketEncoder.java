package ru.disdev.commons.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.lang3.reflect.FieldUtils;
import ru.disdev.commons.Packet;

import java.nio.charset.Charset;
import java.util.UUID;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    private final Charset stringCharset;
    private final boolean useLE;

    public PacketEncoder(Charset stringCharset, boolean useLE) {
        this.stringCharset = stringCharset;
        this.useLE = useLE;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
        if (msg == null) return;
        Class<? extends Packet> clazz = msg.getClass();
        FieldUtils.getAllFieldsList(clazz).forEach(field -> {
            field.setAccessible(true);
            Class<?> type = field.getType();
            try {
                if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
                    int intValue = field.getInt(msg);
                    out.writeInt(intValue);
                } else if (String.class.isAssignableFrom(type)
                        || UUID.class.isAssignableFrom(type)
                        || type.isEnum()) {
                    Object object = field.get(msg);
                    if (object != null) {
                        String value = object.toString();
                        writeString(value, out);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void writeString(String value, ByteBuf out) {
        byte[] bytes = value.getBytes(stringCharset);
        writeInt(bytes.length, out);
        out.writeBytes(bytes);
    }

    private void writeInt(int value, ByteBuf out) {
        if (useLE) {
            out.writeIntLE(value);
        } else {
            out.writeInt(value);
        }
    }
}
