package com.lee.database.mk.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.mk.entity.VirtualMessage;
import com.lee.database.mk.mapper.VirtualMessageMapper;
import com.lee.database.mk.service.IVirtualMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * lee
 *
 * @since 2022-06-20
 */
@Service
@Transactional
public class VirtualMessageServiceImpl extends ServiceImpl<VirtualMessageMapper, VirtualMessage> implements IVirtualMessageService {


    public static final Logger log = LoggerFactory.getLogger(VirtualMessageServiceImpl.class);

    @Autowired
    private VirtualMessageMapper virtualMessageMapper;

    @Override
    public int updateMegFinish(String msg) {
        VirtualMessage virtualMsg = selectMsg(msg);
        if (virtualMsg == null) {
            return 0;
        }
        return virtualMessageMapper.update(null, new UpdateWrapper<VirtualMessage>()
                .eq("id", virtualMsg.getId())
                .eq("state", 0)
                .eq("name", virtualMsg.getName())
                .set("responsebody", msg)
                .set("state", 3)
                .set("responseTime", LocalDateTime.now()));
    }


    @Override
    public VirtualMessage selectMsg(String request) {
        JSONObject requestJson = JSONUtil.parseObj(request);
        String requestName = requestJson.getStr("messageName");

        int requestId = requestJson.getInt("id");
        List<VirtualMessage> virtualMessages = virtualMessageMapper.selectList(new QueryWrapper<VirtualMessage>()
                .eq("name", requestName).eq("state", 0));
        for (VirtualMessage virtualMessage : virtualMessages) {
            String body = virtualMessage.getMessagebody();
            JSONObject bodyJson = JSONUtil.parseObj(body);
            int id = bodyJson.getInt("id");
            if (requestId == id) {
                return virtualMessage;
            }
        }
        log.info("未找到消息：{}", request);
        return null;
    }

    @Override
    public int initMegId() {
        //获取最后一条消息的message 的 bodyId
        VirtualMessage msg = virtualMessageMapper.selectOne(new QueryWrapper<VirtualMessage>().orderByDesc("id").last("limit 1"));
        if (msg == null) {
            return 0;
        }
        JSONObject entries = JSONUtil.parseObj(msg.getMessagebody());
        return entries.getInt("id");
    }

    @Override
    public List<VirtualMessage> requestLocations() {
        return virtualMessageMapper.selectList(new QueryWrapper<VirtualMessage>().eq("name", "appointLocation").eq("state", 0));
    }
}
