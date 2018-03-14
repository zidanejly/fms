package com.jfhealthcare.modules.system.mapper;

import org.apache.ibatis.annotations.Param;

import com.jfhealthcare.modules.system.entity.SysClientVersion;
import com.jfhealthcare.tk.mybatis.util.MyMapper;

public interface SysClientVersionMapper extends MyMapper<SysClientVersion> {
	public SysClientVersion selectLatestClientVersionInfo(@Param(value="exec") String exec,@Param(value = "os") String os);
}