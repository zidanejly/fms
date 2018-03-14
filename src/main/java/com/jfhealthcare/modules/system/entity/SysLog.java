package com.jfhealthcare.modules.system.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "sys_log")
public class SysLog {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 记录创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 日志内容
     */
    private String content;

    /**
     * 日志级别
     */
    @Column(name = "log_level")
    private String logLevel;

    /**
     * 用户账号
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * C端时间
     */
    @Column(name = "log_time")
    private Date logTime;

    /**
     * 文件名
     */
    @Column(name = "file_name")
    private String fileName;

    /**
     * 文件行号
     */
    private Integer line;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取记录创建时间
     *
     * @return create_time - 记录创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置记录创建时间
     *
     * @param createTime 记录创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取日志内容
     *
     * @return content - 日志内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置日志内容
     *
     * @param content 日志内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取日志级别
     *
     * @return log_level - 日志级别
     */
    public String getLogLevel() {
        return logLevel;
    }

    /**
     * 设置日志级别
     *
     * @param logLevel 日志级别
     */
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * 获取用户账号
     *
     * @return user_id - 用户账号
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户账号
     *
     * @param userId 用户账号
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取C端时间
     *
     * @return log_time - C端时间
     */
    public Date getLogTime() {
        return logTime;
    }

    /**
     * 设置C端时间
     *
     * @param logTime C端时间
     */
    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    /**
     * 获取文件名
     *
     * @return file_name - 文件名
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 设置文件名
     *
     * @param fileName 文件名
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 获取文件行号
     *
     * @return line - 文件行号
     */
    public Integer getLine() {
        return line;
    }

    /**
     * 设置文件行号
     *
     * @param line 文件行号
     */
    public void setLine(Integer line) {
        this.line = line;
    }
}