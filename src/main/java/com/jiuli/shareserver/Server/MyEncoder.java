package com.jiuli.shareserver.Server;

import com.jiuli.shareserver.Server.message.RespModel;
import com.jiuli.shareserver.Server.message.TypeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

public class MyEncoder extends MessageToByteEncoder<RespModel> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RespModel responseModel, ByteBuf out) throws Exception {
        String json = TypeUtil.toJson(responseModel);
        byte[] bytes = json.getBytes(CharsetUtil.UTF_8);
        out.writeMedium(bytes.length + 1);
        out.writeByte(responseModel.getType());
        out.writeBytes(bytes);
    }
}
