package com.jfhealthcare.common.entity;

import java.util.LinkedList;

import com.jfhealthcare.common.base.UploadResponse;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class UploadMessage{
   
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
	
	/**
	 * 文件是否需要删除
	 * */
	private boolean isDelete =false;
	
	UploadResponse response = new UploadResponse();
	
	public  UploadResponse getUploadResponse(int code,String signature,LinkedList<String> uploaded){
		response.setCode(code);
		response.setSignature(signature);
		response.setUploaded(uploaded);
		if(uploaded!=null&&uploaded.size()>0){
			log.info("返回参数:{},{},{}",code,signature,uploaded.get(0));
		}else{
			log.info("返回参数:{},{}",code,signature);
		}
		return response;
	}
}
