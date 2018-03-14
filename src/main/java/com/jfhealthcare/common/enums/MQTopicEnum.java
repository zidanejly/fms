package com.jfhealthcare.common.enums;

public enum MQTopicEnum {
	//Boss
	FILE_UPLOAD_TOPIC("ArchiveResult", "MQ文件上传TOPIC");
	//Test
	//FILE_UPLOAD_TOPIC("DcmMessage","MQ文件上传TOPIC");
	//DEMO
	//FILE_UPLOAD_TOPIC("DemoRmis","MQ文件上传TOPIC");

	private String topic;
	private String message;

	private MQTopicEnum(String topic, String message) {
		this.topic = topic;
		this.message = message;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
