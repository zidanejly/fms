package com.jfhealthcare.modules.system.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jfhealthcare.common.base.BaseResponse;
import com.jfhealthcare.modules.system.entity.SysClientVersion;
import com.jfhealthcare.modules.system.service.ClientVersionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/piclHis/client-ver")
@Api(value = "客户端版本信息")
public class ClientVersionController {
	@Autowired
	private ClientVersionService clientVersionService;

	@ApiOperation(value = "客户端最新版本信息", notes = "获取客户端最新版本的信息以及内容")
	@RequestMapping(method = RequestMethod.GET)
	public BaseResponse latestClientVersion(@RequestParam String os, @RequestParam String version,
			@RequestParam String user_id, @RequestParam String exec) {
		log.info("latestClientVersion--ReqeustParamter:os-{},version-{},user_id-{},exec-{}",os,version,user_id,exec);
		try {
			if (StringUtils.isBlank(exec) || StringUtils.isBlank(os)) {
				return BaseResponse.getFailResponse("RequestParamterError");
			}
			SysClientVersion scv = clientVersionService.queryLatestClientVersionInfo(exec,os);
			if (scv != null) {
				return BaseResponse.getSuccessResponse("success", scv);
			}
			return BaseResponse.getSuccessResponse("success", "");
		} catch (Exception e) {
			log.error("latestClientVersionError", e);
			return BaseResponse.getFailResponse();
		}
	}
}
