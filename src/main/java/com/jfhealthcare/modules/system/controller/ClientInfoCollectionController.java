package com.jfhealthcare.modules.system.controller;

import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfhealthcare.common.base.BaseResponse;
import com.jfhealthcare.common.enums.ClientInfoCollectionEnum;
import com.jfhealthcare.common.enums.MQTargetEnum;
import com.jfhealthcare.common.enums.MQTopicEnum;
import com.jfhealthcare.modules.system.entity.SysLog;
import com.jfhealthcare.modules.system.request.ClientInfoCollectionRequest;
import com.jfhealthcare.modules.system.request.LogRequest;
import com.jfhealthcare.modules.system.service.SysLogService;
import com.jfhealthcare.modules.system.service.impl.MqProductServiceImpl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/picl/info")
@Api(value = "客户端信息收集")
public class ClientInfoCollectionController{
	//@Autowired
	//private MqProductServiceImpl mqProductServiceImpl;
	@Autowired
	private SysLogService sysLogService;
	
	@ApiOperation(value = "客户端信息反馈", notes = "收集客户端的反馈并操作")
	@RequestMapping(method = RequestMethod.POST)
	public BaseResponse clientInfoHandler(@RequestBody ClientInfoCollectionRequest cic) {
		try {
			log.info(cic.toString());
			if(ClientInfoCollectionEnum.CLIENT_LOG_PERSISTENCE.getCode().equals(cic.getCode())){
				SysLog sl = new SysLog();
				LogRequest logr = JSON.parseObject(cic.getMessage(),LogRequest.class);
				sl.setContent(logr.getContent());
				sl.setLogLevel(logr.getLog_level());
				sl.setUserId(logr.getUser_id());
				sl.setLogTime(logr.getTime());
				sl.setCreateTime(new Date());
				sl.setFileName(logr.getFile_name());
				sl.setLine(logr.getLine());
				sysLogService.addLog(sl);
			}else{
				//发送MQ信息给客户端
				/*HashMap<String,Object> mp = new HashMap<String,Object>();
				mp.put("code", cic.getCode());
				mp.put("message",cic.getMessage());
				mp.put("level",cic.getLevel());
				mqProductServiceImpl.sendMessage(JSONObject.toJSONString(mp), MQTopicEnum.FILE_UPLOAD_TOPIC, MQTargetEnum.FILE_UPLOAD_FAIL_TARGET);*/
			}
			return BaseResponse.getSuccessResponse("成功!");
		} catch (Exception e) {
			log.error("发送失败!",e);
			return BaseResponse.getFailResponse("失败!");
		}
	}
}
