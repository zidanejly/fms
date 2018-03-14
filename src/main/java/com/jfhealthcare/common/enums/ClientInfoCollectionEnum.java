package com.jfhealthcare.common.enums;

public enum ClientInfoCollectionEnum {
	FILE_PARSE_ERROR("100","文件解析异常!"),
	FILE_ARC_SUCCESS("fileArcSuccess","文件上传DICOM服务器成功!"),
	CLIENT_LOG_PERSISTENCE("0","客户端日志持久化"),
	ERROR_LEVEL("error","错误提示级别"),
	WARNNING_LEVEL("warnning","警告提示级别"),
	INFO_LEVEL("info","信息提示级别");
	
	
    private String code;
    private String message;
    
	private ClientInfoCollectionEnum(String code ,String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
