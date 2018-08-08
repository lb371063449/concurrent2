package com.rinbo.netty.chapter4;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class TimerClient {
    public static void main(String[] args) {
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer() {

                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                int counter ;
                                private byte[] req = ("QUERY TIME ORDER").getBytes();
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ByteBuf message;
                                    for (int i=0;i<100;i++) {
                                        message = Unpooled.buffer(req.length);
                                        message.writeBytes(req);
                                        ctx.writeAndFlush(message);
                                    }
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    String body = (String) msg;
                                    System.out.println("Now is : " + body + "; the counter is : " + ++counter);
                                }
                            });
                        }
                    });
            ChannelFuture future = bootstrap.connect("localhost", 9999).sync();
            future.channel().closeFuture();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

    }
}
