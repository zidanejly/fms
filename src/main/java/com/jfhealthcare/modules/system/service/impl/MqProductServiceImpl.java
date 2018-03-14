package com.jfhealthcare.modules.system.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendResult;
import com.jfhealthcare.common.enums.MQTargetEnum;
import com.jfhealthcare.common.enums.MQTopicEnum;
import com.jfhealthcare.modules.system.service.MqProductService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MqProductServiceImpl implements MqProductService {
	@Autowired
	private Producer producer;

	@Override
	public void sendMessage(String message,MQTopicEnum mqEnum,MQTargetEnum target) {
		Message msg = new Message( //
				// 在控制台创建的Topic，即该消息所属的Topic名称
				mqEnum.getTopic(),
				// Message Tag,
				// 可理解为Gmail中的标签，对消息进行再归类，方便Consumer指定过滤条件在MQ服务器过滤
				target.getTarget(),
				// Message Body
				// 任何二进制形式的数据， MQ不做任何干预，
				// 需要Producer与Consumer协商好一致的序列化和反序列化方式
				message.getBytes());
		// 设置代表消息的业务关键属性，请尽可能全局唯一，以方便您在无法正常收到消息情况下，可通过MQ控制台查询消息并补发
		// 注意：不设置也不会影响消息正常收发
		msg.setKey("fup" + UUID.randomUUID().toString());
		// 发送消息，只要不抛异常就是成功
		// 打印Message ID，以便用于消息发送状态查询
		SendResult sendResult = producer.send(msg);
		log.info("Send Message success. Message ID is: " + sendResult.getMessageId());

	}

}
