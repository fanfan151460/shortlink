package com.nageoffer.shortlink.project.mq.producer;

import cn.hutool.core.lang.UUID;
import com.nageoffer.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.nageoffer.shortlink.project.mq.base.MessageWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LinkStatsProducer {

    private final RocketMQTemplate rocketMQTemplate;

    @Value("${rocketmq.producer.topic}")
    private String statsSaveTopic;


    public Message<MessageWrapper<ShortLinkStatsRecordDTO>> buildMsg(ShortLinkStatsRecordDTO record) {
        String keys = UUID.randomUUID().toString();
        return MessageBuilder
                .withPayload(new MessageWrapper<>(keys, record))
                .setHeader(MessageConst.PROPERTY_KEYS, keys)
                .build();
    }
    /**
     * 发送延迟消费短链接统计
     */
    public void send(ShortLinkStatsRecordDTO record) {
        Message<MessageWrapper<ShortLinkStatsRecordDTO>> message = buildMsg(record);
        try {
            rocketMQTemplate.asyncSend(statsSaveTopic, message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("[消息访问统计监控] 消息发送结果：{}，消息ID：{}，消息Keys：{}",
                            sendResult.getSendStatus(), sendResult.getMsgId(),
                            message.getPayload().getKeys());
                }

                @Override
                public void onException(Throwable e) {
                    log.error("[消息访问统计监控] 消息发送失败，消息Keys：{}，消息体：{}",
                            message.getPayload().getKeys(), message.getPayload(), e);
                }
            });
        } catch (Throwable ex) {
            log.error("[消息访问统计监控] 消息发送异常，消息Keys：{}", message.getPayload().getKeys(), ex);
        }
    }
}
