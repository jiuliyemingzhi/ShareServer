package com.jiuli.shareserver.Server;

import com.jiuli.shareserver.Server.message.TypeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.CharsetUtil;

public class MyDecoder extends LengthFieldBasedFrameDecoder {

    public MyDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }


    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf buf = (ByteBuf) super.decode(ctx, in);
        if (buf==null){
            return  null;
        }
        buf.readMedium();
        byte type = buf.readByte();
//        System.out.println(buf.readableBytes());
        String json = buf.toString(CharsetUtil.UTF_8);
//        System.out.println("====>" + json);
        return TypeUtil.getGson().fromJson(json, TypeUtil.getReqType(type));
    }
}
