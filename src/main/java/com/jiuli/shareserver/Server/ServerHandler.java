package com.jiuli.shareserver.Server;

import com.jiuli.shareserver.Server.message.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;


@Sharable
public class ServerHandler extends SimpleChannelInboundHandler<ReqModel> {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null) {
            return;
        }
        ReqModel reqModel;
        if (msg instanceof ReqModel) {
            reqModel = (ReqModel) msg;
        } else {
            ReferenceCountUtil.release(msg);
            return;
        }


//        System.out.println(reqModel.getCode());

        switch (reqModel.getCode()) {
            case ReqModel.CODE_LOGIN:
                if (reqModel.getCode() != ReqModel.CODE_LOGIN || !Util.KEY.equals(reqModel.getKEY())) {
                    remove(ctx);
                } else {
                    RespModel<Object> respModel = new RespModel<>();
                    respModel.setCode(RespModel.CODE_SUCCEED);
                    ctx.writeAndFlush(respModel);
                }
                break;
            case ReqModel.CODE_PUSH_SHARE:
                if (reqModel.getContent() instanceof UserInfo) {
                    ChannelHandlerPool.put(ctx.channel(), (UserInfo) reqModel.getContent());
                }
                break;
            case ReqModel.CODE_JOIN:

                break;
            case ReqModel.CODE_GET_GROUPS:
                RespModel<Groups> respModel = new RespModel<>();
                respModel.setType(TypeUtil.GROUPS);
                respModel.setSessionID(reqModel.getSessionID());
                respModel.setResult(Groups.getGroups(ctx.channel()));
                respModel.setCode(RespModel.CODE_SUCCEED);
                ctx.writeAndFlush(respModel);
                break;
            case ReqModel.CODE_HEARTBEAT:
                ctx.channel().writeAndFlush(new RespModel<>());
                break;
        }


//        System.out.println(ChannelHandlerPool.size());
        ReferenceCountUtil.release(msg);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ReqModel msg) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("longId : " + ctx.channel().id().asLongText());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                remove(ctx);
                System.out.println("超时" + ctx.channel().id().asShortText());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        remove(ctx);
        System.out.println("exceptionCaught" + ctx.channel().id().asShortText());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        remove(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        remove(ctx);
    }


    private void remove(ChannelHandlerContext ctx) {
        ChannelHandlerPool.remove(ctx.channel());
        try {
            ctx.close();
            System.out.println("close");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
