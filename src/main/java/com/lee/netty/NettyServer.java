package com.lee.netty;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyServer {
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);
    private int port;
    private ChannelInitializer<SocketChannel> initializer;
    private final AtomicInteger bossAtomic = new AtomicInteger();
    private final AtomicInteger workerAtomic = new AtomicInteger();
//    private volatile Channel channel;

    public NettyServer(int port, ChannelInitializer<SocketChannel> initializer) {
        this.port = port;
        this.initializer = initializer;
    }

    public void init() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1, (ThreadFactory) r -> new Thread(r, "netty-server-" + port + "-boss-" + bossAtomic.getAndIncrement()));
        EventLoopGroup workerGroup = new NioEventLoopGroup(0, (ThreadFactory) r -> new Thread(r, "netty-server-" + port + "-worker-" + workerAtomic.getAndIncrement()));

//        EventLoopGroup bossGroup = new NioEventLoopGroup();
//        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap sBootstrap = new ServerBootstrap();
            sBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(initializer);

            //绑定端口,开始接收进来的连接
            ChannelFuture future;
            try {
                future = sBootstrap.bind(port).sync();
                future.addListener((GenericFutureListener<ChannelFuture>) f -> {
                    log.info("Netty Server init over, result:{}, port: {}", f.isSuccess(), port);
//                    this.channel = f.channel();
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
