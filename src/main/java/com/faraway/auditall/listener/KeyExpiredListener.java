package com.faraway.auditall.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.AuditInfo;
import com.faraway.auditall.entity.AuditPhoto;
import com.faraway.auditall.entity.BasicInfo;
import com.faraway.auditall.mapper.AuditInfoMapper;
import com.faraway.auditall.mapper.AuditPhotoMapper;
import com.faraway.auditall.mapper.BasicInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Redis过期key监听类
 *
 * @version: 1.0
 * @author: faraway
 * @date: 2020-11-11 14:34
 */

@Component
public class KeyExpiredListener extends KeyExpirationEventMessageListener {

    @Autowired
    private BasicInfoMapper basicInfoMapper;

    @Autowired
    private AuditInfoMapper auditInfoMapper;

    @Autowired
    private AuditPhotoMapper auditPhotoMapper;


    public KeyExpiredListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, @Nullable byte[] pattern) {
//        String channel = new String(message.getChannel());
        String userName = new String(message.getBody(),StandardCharsets.UTF_8);
        if (userName!=null){
            QueryWrapper<BasicInfo> basicInfoQueryWrapper = new QueryWrapper<>();
            basicInfoQueryWrapper.eq("userName",userName);
            QueryWrapper<AuditInfo> auditInfoQueryWrapper = new QueryWrapper<>();
            auditInfoQueryWrapper.eq("userName",userName);
            QueryWrapper<AuditPhoto> auditPhotoQueryWrapper = new QueryWrapper<>();
            auditPhotoQueryWrapper.eq("userName",userName);

            basicInfoMapper.delete(basicInfoQueryWrapper);
            auditInfoMapper.delete(auditInfoQueryWrapper);
            auditPhotoMapper.delete(auditPhotoQueryWrapper);
            System.out.println("===redis expired==数据库清理完成=");

        }

        System.out.println("redis expired-----"+"==="+userName+"==="+new String(pattern));
    }
}
