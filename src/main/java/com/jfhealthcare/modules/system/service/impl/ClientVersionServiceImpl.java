package com.jfhealthcare.modules.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jfhealthcare.modules.system.entity.SysClientVersion;
import com.jfhealthcare.modules.system.mapper.SysClientVersionMapper;
import com.jfhealthcare.modules.system.service.ClientVersionService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class ClientVersionServiceImpl implements ClientVersionService{
	@Autowired
	private SysClientVersionMapper sysClientVersionMapper;
	@Override
	public SysClientVersion queryLatestClientVersionInfo(String exec, String os) throws Exception{
		try {
			return sysClientVersionMapper.selectLatestClientVersionInfo(exec, os);
		} catch (Exception e) {
			log.error("查询客户端版本数据库异常!");
			throw e;
		}
	}

}
