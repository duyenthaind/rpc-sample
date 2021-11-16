package com.sample.nety.rpc.codec;

import com.sample.nety.rpc.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author duyenthai
 */
public class RpcDecoder extends ByteToMessageDecoder {
    private Class<?> clazz;
    private Serializer serializer;

    public RpcDecoder(Class<?> clazz, Serializer serializer) {
        this.clazz = clazz;
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // encoding use int type which has 4 bytes to indicate the length
        if (in.readableBytes() < 4) {
            return;
        }

        // mark current position
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }

        byte[] data = new byte[dataLength];
        // read data in the bytebuff to the data byte array
        in.readBytes(data);
        Object object = serializer.deserialize(clazz, data);
        out.add(object);
    }
}
