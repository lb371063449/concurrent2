package com.rinbo.netty.chapter3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 * @author dell
 */
public class TimeServerHandler  extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)  {
        //获取消息
        String request = (String) msg;
        //设置应答
        String response;
        if ("QUERY TIME ORDER".equals(request)) {
            response = new Date(System.currentTimeMillis()).toString();
        } else {
            response = "BAD REQUEST";
        }
        //将应答转换为ByteBuf
        ByteBuf resp = Unpooled.copiedBuffer(response.getBytes());
        //通过ChannelHandlerContext异步发送resp应答信息
        //write()并不直接将消息写入SocketChannel，调用write()只是把待发送的消息放置到发送缓冲数组
        //在通过flush()将发送缓冲区中的消息全部写入到SocketChannel
        ctx.writeAndFlush(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)  {
        //将消息发送队列中的消息写入到SocketChannel，并发送给对方
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //发生异常时，关闭ChannelHandlerContext
        ctx.close();
    }
}
