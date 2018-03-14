package com.jfhealthcare.modules.system.request;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
@Data
public class ClientInfoCollectionRequest extends BaseRequest implements Serializable {
	private static final long serialVersionUID = 123123123411234123L;
	//采集信息类型
	private String code;
	//提示级别（用于RMIS-web系统）
	private String level;
	//采集的信息
	private String message;
	//客户端发现问题时间
	private Date timestamp;
}
