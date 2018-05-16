package com.jfhealthcare.common.enums;

public enum MQTopicEnum {

	// PRO
	// FILE_UPLOAD_TOPIC("ArchiveResult", "MQ文件上传TOPIC");
	// Test
	   FILE_UPLOAD_TOPIC("DcmMessage","MQ文件上传TOPIC");
	// DEMO
	// FILE_UPLOAD_TOPIC("DemoRmis","MQ文件上传TOPIC");
	// FEIJIEHE
	// FILE_UPLOAD_TOPIC("DemoArchive", "MQ文件上传TOPIC");
	// FEIJIEHE BIAOZHU
	// FILE_UPLOAD_TOPIC("AnnotationArchive", "MQ文件上传TOPIC");
	// History
	// FILE_UPLOAD_TOPIC("NOTHING", "历史没有MQ");

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
