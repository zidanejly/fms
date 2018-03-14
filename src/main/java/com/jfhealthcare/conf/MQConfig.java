package com.jfhealthcare.conf;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.jfhealthcare.common.enums.MQTopicEnum;
import com.jfhealthcare.listener.MqConsumer4UploadFileListener;

/**
 * 阿里MQ的配置项
 */
@Configuration
public class MQConfig {
	@Value("${ali_productId}")
	private String productId;
	@Value("${ali_consumerId}")
	private String consumerId;
	@Value("${ali_accessKey}")
	private String accessKey;
	@Value("${ali_secretKey}")
	private String secretKey;
	@Value("${ali_onsAddr}")
	private String onsAddr;

	@Autowired
	private MqConsumer4UploadFileListener mqConsumer4UploadFileListener;

	@Bean
	public Producer producer() {
		Properties properties = new Properties();
		// 您在MQ控制台创建的Producer ID
		properties.put(PropertyKeyConst.ProducerId, productId);
		// 鉴权用AccessKey，在阿里云服务器管理控制台创建
		properties.put(PropertyKeyConst.AccessKey, accessKey);
		// 鉴权用SecretKey，在阿里云服务器管理控制台创建
		properties.put(PropertyKeyConst.SecretKey, secretKey);
		// 设置 TCP 接入域名（此处以公共云的公网接入为例）
		properties.put(PropertyKeyConst.ONSAddr, onsAddr);
		//设置发送超时时间，单位是毫秒
		properties.put(PropertyKeyConst.SendMsgTimeoutMillis, "10000");
		Producer producer = ONSFactory.createProducer(properties);
		// 在发送消息前，必须调用start方法来启动Producer，只需调用一次即可
		producer.start();
		return producer;
	}

	/*@Bean
	public Consumer consumer() {
		Properties properties = new Properties();
		// 您在MQ控制台创建的Consumer ID
		properties.put(PropertyKeyConst.ConsumerId, consumerId);
		// 鉴权用AccessKey，在阿里云服务器管理控制台创建
		properties.put(PropertyKeyConst.AccessKey, accessKey);
		// 鉴权用SecretKey，在阿里云服务器管理控制台创建
		properties.put(PropertyKeyConst.SecretKey, secretKey);
		// 设置 TCP 接入域名（此处以公共云公网环境接入为例）
		properties.put(PropertyKeyConst.ONSAddr, onsAddr);
		Consumer consumer = ONSFactory.createConsumer(properties);
		consumer.subscribe(MQTopicEnum.FILE_UPLOAD_TOPIC.getTopic(),"*", mqConsumer4UploadFileListener);
		consumer.start();
		return consumer;
	}*/
}
