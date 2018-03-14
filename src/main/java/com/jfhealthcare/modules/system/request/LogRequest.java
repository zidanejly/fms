package com.jfhealthcare.modules.system.request;

import java.util.Date;


import lombok.Data;

@Data
public class LogRequest extends BaseRequest{
	/**
	 * 日志内容
	 */
	private String content;

	/**
	 * 日志级别
	 */
	private String log_level;
	
	/**
	 * 基层技师账号
	 */
	private String user_id;

	/**
	 * C端时间
	 */
	private Date time;
	
	/**
	 * 文件名称
	 */
	private String file_name;
	
	/**
	 * 行数
	 */
	private int line;
}
