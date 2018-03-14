package com.jfhealthcare.common.base;

import java.util.LinkedList;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class UploadResponse{
   
	private static final long serialVersionUID = 11L;
	/**
	 * 返回结果码
	 */
	private int code;
	/**
	 * 客户端传过来的hash
	 */
	private String signature;
	/**
	 * 已经上传区域
	 */
	private LinkedList<String> uploaded;
	
	public static UploadResponse getUploadResponse(int code,String signature,LinkedList<String> uploaded){
		UploadResponse response = new UploadResponse();
		response.setCode(code);
		response.setSignature(signature);
		response.setUploaded(uploaded);
		if(uploaded!=null&&uploaded.size()>0){
			log.info("返回参数:{},{},{}",code,signature,uploaded);
		}else{
			log.info("返回参数:{},{}",code,signature);
		}
		return response;
	}
}
