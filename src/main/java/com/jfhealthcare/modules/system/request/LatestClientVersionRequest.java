package com.jfhealthcare.modules.system.request;

import java.io.Serializable;

import lombok.Data;

@Data
public class LatestClientVersionRequest extends BaseRequest implements Serializable{
	private static final long serialVersionUID = 123123128901234123L;
	//用户ID
	private String user_id;
	//操作系统
	private String os;
	//应用名称
	private String exec;
	//当前版本号
	private String version;
}
