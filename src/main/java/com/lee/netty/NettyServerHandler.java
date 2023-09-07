package com.lee.netty;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.lee.netty.deal.DealResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Netty 服务端处理器，用于对网络事件进行读写操作
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {

    public static final Log log = LogFactory.get();

    public static final Map<String, ChannelHandlerContext> channels = new ConcurrentHashMap<>();

    public Lock lock = new ReentrantLock(true);

    //缓存收到的requestLocation请求
    public static final Map<Integer, String> cacheRequestLocationMap = new ConcurrentHashMap<>();
    public static final Map<Integer, String> cacheBoxAnnounceMap = new ConcurrentHashMap<>();

    //缓存收到的请求，除requestLocation之外的所有
    public static final Map<Integer, String> cacheOtherMap = new ConcurrentHashMap<>();


    public static void write(String msg) {
        channels.values().forEach(ctx -> {
            ctx.writeAndFlush(msg);
        });
    }

    /**
     * 收到客户端消息，自动触发
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String body) throws Exception {

        /**
         * 以  | 分割解析，并缓存
         * 重发
         * 间隔200ms
         *
         */
        //ByteBuf buf = (ByteBuf) msg;
        //byte[] bytes = new byte[buf.readableBytes()];
        //buf.readBytes(bytes);
        //String body = new String(bytes, "UTF-8");
        //10 up

        //List<String> split = StrUtil.split(body, "\r\n");
        //split.forEach(str -> {
        //
        //    if (!str.isEmpty()) {
        //        if (!str.endsWith("}")) {
        //            return;
        //        }
        //        //body = StrUtil.replace(body, "|", "");
        //
        //        JSONObject responseJson = JSONUtil.parseObj(str);
        //        String messageName = responseJson.getStr("messageName");
        //        if ("requestLocation".equals(messageName)) {
        //            log.info("server rec from ->> [{}], msg: {}", ctx.channel().remoteAddress(), str);
        //            int msgId = responseJson.getInt("id");
        //            cacheRequestLocationMap.put(msgId, str);
        //        } else if ("BoxAnnounceOrSupplement".equals(messageName)) {
        //            log.info("server rec from gis ->> [{}], msg: {}", ctx.channel().remoteAddress(), str);
        //            int msgId = responseJson.getInt("id");
        //            cacheBoxAnnounceMap.put(msgId, str);
        //        } else {
        //            //cacheOtherMap.put(msgId, str);
        //        }
        //        lock.lock();
        //
        //        /**回复消息
        //         * copiedBuffer：创建一个新的缓冲区，内容为里面的参数
        //         * 通过 ChannelHandlerContext 的 write 方法将消息异步发送给客户端
        //         * */
        //
        //        try {
        //            //TODO 按照收到的消息，进行回复
        //            //如果收到的消息太多，先缓存，再一个个回复
        //            String response = DealResponse.response(str) + "\r\n";
        //            if (!response.equals("BoxAnnounceOrSupplement")) {
        //                // byte[] sendBytes = response.getBytes();
        //                ctx.write(Unpooled.copiedBuffer(response.getBytes()));
        //                // ctx.writeAndFlush(sendBytes);
        //                //log.info("server send to  ->> [{}], msg: {}", ctx.channel().remoteAddress(), StrUtil.replace(response, "\r\n", ""));
        //            }
        //
        //        } catch (Exception e) {
        //            log.info(e.getMessage());
        //        } finally {
        //            lock.unlock();
        //        }
        //    }
        //});
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        String key = getKey(ctx);
        channels.put(key, ctx);
        log.info("{} active to {}...", key, ctx.channel().localAddress().toString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        String key = getKey(ctx);
        channels.remove(key);
        log.info("{} inactive to {}...", key, ctx.channel().localAddress().toString());
    }


    //@Override
    //public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    //    /**flush：将消息发送队列中的消息写入到 SocketChannel 中发送给对方，为了频繁的唤醒 Selector 进行消息发送
    //     * Netty 的 write 方法并不直接将消息写如 SocketChannel 中，调用 write 只是把待发送的消息放到发送缓存数组中，再通过调用 flush
    //     * 方法，将发送缓冲区的消息全部写入到 SocketChannel 中
    //     * */
    //    ctx.flush();
    //}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /**当发生异常时，关闭 ChannelHandlerContext，释放和它相关联的句柄等资源 */
        log.info("netty: {}", cause.getMessage());
        ctx.close();
    }

    private String getKey(ChannelHandlerContext ctx) {
        return ctx.channel().remoteAddress().toString();
    }

}