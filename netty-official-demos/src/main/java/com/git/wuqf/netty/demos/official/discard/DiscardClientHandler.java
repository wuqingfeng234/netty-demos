package com.git.wuqf.netty.demos.official.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.ByteBuffer;

public class DiscardClientHandler extends SimpleChannelInboundHandler<Object> {

    private ByteBuf content;

    private ChannelHandlerContext ctx;

    int count = 0;

    final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        content = ctx.alloc().directBuffer(SIZE).writeZero(SIZE);
        generateTraffic();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        content.release();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        exceptionCaught(ctx, cause);
        ctx.close();
    }

    private void generateTraffic() {
        ctx.writeAndFlush(content.retainedDuplicate()).addListener(trafficGenerator);
    }

    private final ChannelFutureListener trafficGenerator = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                generateTraffic();
                count++;
                System.out.println(count);
            } else {
                future.cause().printStackTrace();
                future.channel().closeFuture();
            }
        }
    };
}
