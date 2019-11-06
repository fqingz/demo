package com.fqingz.demo.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author FangQing
 * @date 2019/11/6 1:29
 */
public class DiscardServer {

    public static void main(String[] args) {
        int port = 999;
        System.out.println ("准备运行端口" + port );
        try (EventLoopGroup bossGroup = new NioEventLoopGroup ( );
             EventLoopGroup workerGroup = new NioEventLoopGroup ( )){
            new ServerBootstrap ( )
                    .group (bossGroup, workerGroup)
                    .channel (NioServerSocketChannel.class)
                    .childHandler (new ChannelInitializer<SocketChannel> ( ) {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline ().addLast (new DicardServerHandler ());
                        }
                    })
                    .option (ChannelOption.SO_BACKLOG, 128)
                    .childOption (ChannelOption.SO_KEEPALIVE, true)
                    .bind (port).sync ( )
                    .channel ( ).closeFuture ( ).sync ( );
        } catch (Exception e) {
            e.printStackTrace ( );
        }

    }
}
