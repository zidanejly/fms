package com.jfhealthcare.modules.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jfhealthcare.modules.system.entity.SysLog;
import com.jfhealthcare.modules.system.mapper.SysLogMapper;
import com.jfhealthcare.modules.system.service.SysLogService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SysLogServiceImpl implements SysLogService {
	@Autowired
	private SysLogMapper sysLogMapper;

	@Override
	public void addLog(SysLog sysLog) throws Exception{
		try {
		sysLogMapper.insertSelective(sysLog);
		} catch (Exception e) {
			log.error("新增日志记录数据库操作异常!",e);
			throw e;
		}
	}

}
