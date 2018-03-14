package com.jfhealthcare.modules.system.service;

import com.jfhealthcare.common.enums.MQTargetEnum;
import com.jfhealthcare.common.enums.MQTopicEnum;

public interface MqProductService {
	void sendMessage(String message,MQTopicEnum mqEnum,MQTargetEnum target);
}
