package com.jfhealthcare.common.exception;

import org.apache.http.HttpStatus;

public class FmsException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
    private String msg;
    private int code = HttpStatus.SC_INTERNAL_SERVER_ERROR;
    
    public FmsException(String msg) {
		super(msg);
		this.msg = msg;
	}
	
	public FmsException(String msg, Throwable e) {
		super(msg, e);
		this.msg = msg;
	}
	
	public FmsException(String msg, int code) {
		super(msg);
		this.msg = msg;
		this.code = code;
	}
	
	public FmsException(String msg, int code, Throwable e) {
		super(msg, e);
		this.msg = msg;
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	
}
