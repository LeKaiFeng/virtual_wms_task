package com.lee.netty;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    public static final Log log = LogFactory.get();

    public static final Map<Integer, String> cacheClientRecMsgMap = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String body) throws Exception {
        //ByteBuf buf = (ByteBuf) msg;
        //byte[] bytes = new byte[buf.readableBytes()];
        //buf.readBytes(bytes);
        //String body = new String(bytes, StandardCharsets.UTF_8);
        //body = StrUtil.replace(body, "|", "");
        log.debug("client rec from [{}] , msg: {}", ctx.channel().remoteAddress(), body);
        //JSONObject responseJson = JSONUtil.parseObj(body);
        //int id = responseJson.getInt("id");
        //cacheClientRecMsgMap.put(id, body);
        /* if (body == null) {
            return;
        }
        JSONObject responseJson = JSONUtil.parseObj(body);
        //String name = responseJson.getStr("name");
        //if (name.equals("requestLocation")) {

        cacheClientRecMsgMap.put(responseJson.getInt("id"), body);*/

        //}
        // ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //当被通知Channel是活跃的时候,发送一条消息
//         ctx.writeAndFlush(Unpooled.copiedBuffer("client is started!", CharsetUtil.UTF_8));
    }

    /**
     * 当发生异常时，打印异常 日志，释放客户端资源
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /**释放资源*/
        log.info("Unexpected exception from downstream : " + cause.getMessage());
        ctx.close();
    }
}