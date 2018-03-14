package com.jfhealthcare.modules.system.request;

import java.io.Serializable;

import lombok.Data;

@Data
public class UploadFileRequest extends BaseRequest implements Serializable{
	private static final long serialVersionUID = 6200336587406916275L;
	//文件总大小
	private long size;
	//范围
	private long[] range;
	//string文件Hash值,用于文件上传成功后的文件校验
	private String signature;
	//zip 算法  没有时 会传none
	private String zip;
	//上传用户ID
	private String userId;
	//本次上传片段校验值
	private String fragment_signature;
}
