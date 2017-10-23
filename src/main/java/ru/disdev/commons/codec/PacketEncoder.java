package ru.disdev.commons.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.lang3.reflect.FieldUtils;
import ru.disdev.commons.annotations.Ignore;
import ru.disdev.commons.annotations.Key;

import java.nio.charset.Charset;
import java.util.UUID;

public class PacketEncoder extends MessageToByteEncoder<Object> {

    private final Charset stringCharset;
    private final boolean useLE;

    public PacketEncoder(Charset stringCharset, boolean useLE) {
        this.stringCharset = stringCharset;
        this.useLE = useLE;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        encode(msg, out);
    }

    private void encode(Object object, ByteBuf out) {
        if (object == null) return;
        encode(object, object.getClass(), out);
    }

    private void encode(Object object, Class<?> type, ByteBuf out) {
        if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
            out.writeInt((Integer) object);
        } else if (String.class.isAssignableFrom(type)
                || UUID.class.isAssignableFrom(type)
                || type.isEnum()) {
            if (object != null) {
                String value = object.toString();
                writeString(value, out);
            }
        } else {
            Key key = type.getAnnotation(Key.class);
            if (key != null) {
                int value = key.value();
                writeInt(value, out);
            }
            FieldUtils.getAllFieldsList(type).stream()
                    .filter(field -> {
                        Ignore ignore = field.getAnnotation(Ignore.class);
                        return ignore == null || !ignore.write();
                    })
                    .forEach(field -> {
                        field.setAccessible(true);
                        Class<?> fieldType = field.getType();
                        try {
                            Object value = field.get(object);
                            encode(value, fieldType, out);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    });
        }
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
