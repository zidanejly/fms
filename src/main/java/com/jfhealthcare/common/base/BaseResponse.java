package com.jfhealthcare.common.base;


import lombok.Data;

import java.util.Date;

import org.apache.http.HttpStatus;

/**
 * 返回数据
 * 
 * @author xujinma
 */
@Data
public class BaseResponse {
	private static final long serialVersionUID = 1L;
	
    /**
     * 返回时间
     */
	private Date resultDate = new Date();
	
	/**
	 * 返回结果码
	 */
	private int code;
	
	/**
	 * 返回信息
	 */
	private String msg;
	
	/**
	 * 返回数据
	 */
	private Object data;
	
	public static BaseResponse getSuccessResponse() {
		return getSuccessResponse(new Date());
	}
	
	public static BaseResponse getSuccessResponse(String message) {
		return getSuccessResponse(HttpStatus.SC_OK,message,new Date());
	}
	
	public static BaseResponse getSuccessResponse(Object data) {
		return getSuccessResponse(null,data);
	}
	
	public static BaseResponse getSuccessResponse(String msg,Object data){
		return getSuccessResponse(HttpStatus.SC_OK,msg,data);
	}
	
	public static BaseResponse getSuccessResponse(int code, String msg,Object data){
		BaseResponse response = new BaseResponse();
		response.setCode(code);
		response.setMsg(msg);
		response.setData(data);
		return response;
	}
	
	public static BaseResponse getFailResponse() {
		return getFailResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR,null);
	}
	
	public static BaseResponse getFailResponse(String msg) {
		return getFailResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR,msg);
	}
	
	public static BaseResponse getFailResponse(int code,String msg) {
		return getFailResponse(code,msg,null);
	}
	
	public static BaseResponse getFailResponse(int code, String msg,Object data) {
		BaseResponse response = new BaseResponse();
		response.setCode(code);
		response.setMsg(msg);
		response.setData(data);
		return response;
	}
}
