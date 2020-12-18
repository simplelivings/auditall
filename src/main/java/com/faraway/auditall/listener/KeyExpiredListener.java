package com.faraway.auditall.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.*;
import com.faraway.auditall.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Redis过期key监听类
 * 有2处，存在key过期问题：
 * 1- token 设置时间为60分钟
 * 2- 密码输入错误，设置时间为60分钟
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

    @Autowired
    private InspectInfoMapper inspectInfoMapper;

    @Autowired
    private InspectPhotoMapper inspectPhotoMapper;


    public KeyExpiredListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }


    //redis过期后，自动清空用户信息；
    @Override
    public void onMessage(Message message, @Nullable byte[] pattern) {

        String userName = new String(message.getBody(),StandardCharsets.UTF_8);
        if (userName!=null){
            QueryWrapper<BasicInfo> basicInfoQueryWrapper = new QueryWrapper<>();
            basicInfoQueryWrapper.eq("userName",userName);
            QueryWrapper<AuditInfo> auditInfoQueryWrapper = new QueryWrapper<>();
            auditInfoQueryWrapper.eq("userName",userName);
            QueryWrapper<AuditPhoto> auditPhotoQueryWrapper = new QueryWrapper<>();
            auditPhotoQueryWrapper.eq("userName",userName);


            QueryWrapper<InspectInfo> inspectInfoQueryWrapper = new QueryWrapper<>();
            inspectInfoQueryWrapper.eq("userName",userName);
            QueryWrapper<InspectPhoto> inspectPhotoQueryWrapper = new QueryWrapper<>();
            inspectPhotoQueryWrapper.eq("userName",userName);

            basicInfoMapper.delete(basicInfoQueryWrapper);
            auditInfoMapper.delete(auditInfoQueryWrapper);
            auditPhotoMapper.delete(auditPhotoQueryWrapper);

            inspectInfoMapper.delete(inspectInfoQueryWrapper);
            inspectPhotoMapper.delete(inspectPhotoQueryWrapper);
            System.out.println("===redis expired==数据库清理完成=");

        }

        System.out.println("redis expired-----"+"==="+userName+"==="+new String(pattern));
    }
}
