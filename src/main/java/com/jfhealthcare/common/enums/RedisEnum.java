package com.jfhealthcare.common.enums;

/**
 * redis key值枚举
 */
public enum RedisEnum {
	
	LOGINCODE("logincode：");//loginuser 的key值      例如： logincode：cjbg
	
    private String value;
    
	private RedisEnum(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
	
}
