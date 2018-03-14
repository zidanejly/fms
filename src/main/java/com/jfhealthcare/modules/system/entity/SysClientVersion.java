package com.jfhealthcare.modules.system.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "sys_client_version")
public class SysClientVersion {
    /**
     * 主键 自增
     */
    @Id
    private Integer id;

    /**
     * 版本号
     */
    private String version;

    /**
     * 更新路径
     */
    @Column(name = "update_url")
    private String updateUrl;

    /**
     * 文件完整唯一标识
     */
    private String signature;

    /**
     * 应用名
     */
    private String exec;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;
    
    /**
     * 压缩格式
     */
    @Column(name = "zip")
    private String zip;

    public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	/**
     * 获取主键 自增
     *
     * @return id - 主键 自增
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键 自增
     *
     * @param id 主键 自增
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取版本号
     *
     * @return version - 版本号
     */
    public String getVersion() {
        return version;
    }

    /**
     * 设置版本号
     *
     * @param version 版本号
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 获取更新路径
     *
     * @return update_url - 更新路径
     */
    public String getUpdateUrl() {
        return updateUrl;
    }

    /**
     * 设置更新路径
     *
     * @param updateUrl 更新路径
     */
    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    /**
     * 获取文件完整唯一标识
     *
     * @return signature - 文件完整唯一标识
     */
    public String getSignature() {
        return signature;
    }

    /**
     * 设置文件完整唯一标识
     *
     * @param signature 文件完整唯一标识
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * 获取应用名
     *
     * @return exec - 应用名
     */
    public String getExec() {
        return exec;
    }

    /**
     * 设置应用名
     *
     * @param exec 应用名
     */
    public void setExec(String exec) {
        this.exec = exec;
    }

    /**
     * 获取操作系统
     *
     * @return os - 操作系统
     */
    public String getOs() {
        return os;
    }

    /**
     * 设置操作系统
     *
     * @param os 操作系统
     */
    public void setOs(String os) {
        this.os = os;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}