package com.lee.netty;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;

public class NettyClient {
    public static final Log log = LogFactory.get();

    private final String host;
    private final int port;
    private final SocketAddress socketAddress;

    private final ChannelInitializer<SocketChannel> initializer;
    private final boolean reConn;
    private final long reConnDelay;

    private volatile Channel channel;

    public volatile boolean close = false;

    public NettyClient(String host, int port, ChannelInitializer<SocketChannel> initializer) {
        this(host, port, initializer, false, 0);
    }

    public NettyClient(String host, int port, ChannelInitializer<SocketChannel> initializer, boolean reConn, long reConnDelay) {
        this.host = host;
        this.port = port;
        this.initializer = initializer;
        this.reConn = reConn;
        this.reConnDelay = reConnDelay;
        this.socketAddress = InetSocketAddress.createUnresolved(host, port);
    }


    public void init() {
        do {
            NioEventLoopGroup group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(initializer);

            try {
                ChannelFuture future = bootstrap.connect(socketAddress).sync();
                future.addListener((GenericFutureListener<ChannelFuture>) f -> {
                    log.info("本地Netty Client 连接成功, result:{}, {}:{}", f.isSuccess(), host, port);
                    this.channel = f.channel();
                });

                future.channel().closeFuture().sync();
                log.info("本地Netty Client 关闭, {}:{}", host, port);
            } catch (Exception e) {
                log.warn("连接远端Server失败, {}:{}, e: {}", host, port, e.getMessage());
            } finally {
                group.shutdownGracefully();
                log.info("本地Netty Client资源已释放, {}:{}", host, port);

                if (reConn && !close) {
                    // 如果需要重连, 所有资源释放完成之后, 再次发起重连操作
                    try {
                        Thread.sleep(reConnDelay);
                        log.info("开始尝试重连, {}:{}", host, port);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        } while (reConn && !close);
    }

    public boolean isConnect() {
        return (Objects.isNull(channel)) ? false : true;
    }

    public boolean write(Object message) {
        if (Objects.isNull(channel)) {
            log.info("连接尚未建立, 请稍后...");
            return false;
        }

        if (!channel.isWritable()) {
            log.info("channel暂不可执行发送, Registered: {}, Active: {}, Open: {}, Writable: {}"
                    , channel.isRegistered(), channel.isActive(), channel.isOpen(), channel.isWritable());
            return false;
        }

        channel.writeAndFlush(message);
        return true;
    }

    public void close() {
        if (channel != null && channel.isOpen()) {
            channel.close();
            close = true;
        }
    }

}
