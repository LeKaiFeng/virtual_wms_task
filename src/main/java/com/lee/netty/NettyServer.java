package com.lee.netty;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Objects;

public class NettyServer {
    public static final Log log = LogFactory.get();


    private int port;
    private ChannelInitializer<SocketChannel> initializer;
    private volatile Channel channel;

    public NettyServer(int port, ChannelInitializer<SocketChannel> initializer) {
        this.port = port;
        this.initializer = initializer;
    }

    public void init() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap sBootstrap = new ServerBootstrap();
            sBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //.option(ChannelOption.RCVBUF_ALLOCATOR,new FixedRecvByteBufAllocator(10240))
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(initializer);

            //绑定端口,开始接收进来的连接
            ChannelFuture future;
            try {
                future = sBootstrap.bind(port).sync();
                future.addListener((GenericFutureListener<ChannelFuture>) f -> {
                    log.info("Netty Server init over, result:{}, port: {}", f.isSuccess(), port);
                    this.channel = f.channel();
                });
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                log.error("{}", e.getMessage(), e);
            }

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();

            log.warn("Netty Server 关闭了");
        }
    }


}
