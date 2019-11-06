package com.fqingz.demo.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * @author FangQing
 * @date 2019/11/6 1:27
 */
public class DicardServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ctx.fireChannelRead(msg);
        try {
            ByteBuf in = (ByteBuf) msg;
            System.out.print (in.toString (CharsetUtil.UTF_8));
        } finally {
            ReferenceCountUtil.release (msg );
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        ctx.fireExceptionCaught(cause);
        cause.printStackTrace();
        ctx.close();
    }
}
