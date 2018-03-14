package com.jfhealthcare.modules.system.service;

import com.jfhealthcare.modules.system.entity.SysClientVersion;

public interface ClientVersionService {

	SysClientVersion queryLatestClientVersionInfo(String exec,String os) throws Exception;
	
}
