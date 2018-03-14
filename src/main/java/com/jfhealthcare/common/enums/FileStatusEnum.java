package com.jfhealthcare.common.enums;

public enum FileStatusEnum {
	FILE_STATUS_0(0,"文件正在上传中"),
	FILE_STATUS_1(1,"文件已经上传完毕，等待或正在请求dcm服务器"),
	FILE_STATUS_2(2,"上传至dcm服务器，文件解析成功"),
	FILE_STATUS_3(3,"上传至dcm服务器，文件解析失败");
	
	
    private int code;
    private String message;
    
	private FileStatusEnum(int code ,String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
