package com.lee.database.mk.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.mk.entity.MessageReceive;
import com.lee.database.mk.entity.VirtualMessage;
import com.lee.database.mk.mapper.MessageReceiveMapper;
import com.lee.database.mk.mapper.VirtualMessageMapper;
import com.lee.database.mk.service.IMessageReceiveService;
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
public class MessageReceiveServiceImpl extends ServiceImpl<MessageReceiveMapper, MessageReceive> implements IMessageReceiveService {


    public static final Logger log = LoggerFactory.getLogger(MessageReceiveServiceImpl.class);

    @Autowired
    private MessageReceiveMapper messageReceiveMapper;


    @Override
    public int initMegId() {
        //获取最后一条消息的message 的 bodyId
        MessageReceive msg = messageReceiveMapper.selectOne(new QueryWrapper<MessageReceive>()
                .orderByDesc("id").last("limit 1"));
        if (msg == null) {
            return 1;
        }
        return msg.getId() + 1;
    }


}
