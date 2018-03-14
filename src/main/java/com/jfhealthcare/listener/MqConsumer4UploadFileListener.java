package com.jfhealthcare.listener;

import org.springframework.stereotype.Component;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MqConsumer4UploadFileListener implements MessageListener {
	@Override
	public Action consume(Message message, ConsumeContext context) {
		try {
			byte[] body = message.getBody();
			String messageBody = "";
			if(body!=null&&body.length>0){
				messageBody = new String(body);
			}
			log.info("接收到的信息为:"+messageBody);
			return Action.CommitMessage;
		} catch (Exception e) {
			log.info("接受消息失败!");
			return Action.ReconsumeLater;
		}
	}

}
