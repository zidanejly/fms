package com.jfhealthcare.common.enums;

/**
 * 加密方式枚举值
 */
public enum EncryptionEnum {
	ALGORITHMNAME("md5"),//加密方式
	HASHITERATIONS("2"), //表示加密系数
	SALT("123456"); //表示加密所用盐值
    private String value;
    
	private EncryptionEnum(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
