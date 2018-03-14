package com.jfhealthcare.modules.system.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "sys_uploadFile")
public class SysUploadFile {
    /**
     * 主键 自增 非空
     */
    @Id
    private Integer id;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 创建人
     */
    private String creater;

    /**
     * 更新人
     */
    private String updater;

    /**
     * 文件状态
0-正在上传中
1-上传完毕，发送dcm服务器
2-上传至dcm服务器，文件解析成功
3-上传至dcm服务器，文件解析失败
     */
    @Column(name = "file_status")
    private Integer fileStatus;

    /**
     * 文件hash值 每一个文件只有一个Hash值
     */
    @Column(name = "file_hash")
    private String fileHash;

    /**
     * 预留字段
     */
    private String text1;

    /**
     * 预留字段
     */
    private String text2;

    /**
     * 预留字段
     */
    private String text3;

    /**
     * 获取主键 自增 非空
     *
     * @return id - 主键 自增 非空
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键 自增 非空
     *
     * @param id 主键 自增 非空
     */
    public void setId(Integer id) {
        this.id = id;
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

    /**
     * 获取更新时间
     *
     * @return update_time - 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取创建人
     *
     * @return creater - 创建人
     */
    public String getCreater() {
        return creater;
    }

    /**
     * 设置创建人
     *
     * @param creater 创建人
     */
    public void setCreater(String creater) {
        this.creater = creater;
    }

    /**
     * 获取更新人
     *
     * @return updater - 更新人
     */
    public String getUpdater() {
        return updater;
    }

    /**
     * 设置更新人
     *
     * @param updater 更新人
     */
    public void setUpdater(String updater) {
        this.updater = updater;
    }

    /**
     * 获取文件状态
0-正在上传中
1-上传完毕，发送dcm服务器
2-上传至dcm服务器，文件解析成功
3-上传至dcm服务器，文件解析失败
     *
     * @return file_status - 文件状态
0-正在上传中
1-上传完毕，发送dcm服务器
2-上传至dcm服务器，文件解析成功
3-上传至dcm服务器，文件解析失败
     */
    public Integer getFileStatus() {
        return fileStatus;
    }

    /**
     * 设置文件状态
0-正在上传中
1-上传完毕，发送dcm服务器
2-上传至dcm服务器，文件解析成功
3-上传至dcm服务器，文件解析失败
     *
     * @param fileStatus 文件状态
0-正在上传中
1-上传完毕，发送dcm服务器
2-上传至dcm服务器，文件解析成功
3-上传至dcm服务器，文件解析失败
     */
    public void setFileStatus(Integer fileStatus) {
        this.fileStatus = fileStatus;
    }

    /**
     * 获取文件hash值 每一个文件只有一个Hash值
     *
     * @return file_hash - 文件hash值 每一个文件只有一个Hash值
     */
    public String getFileHash() {
        return fileHash;
    }

    /**
     * 设置文件hash值 每一个文件只有一个Hash值
     *
     * @param fileHash 文件hash值 每一个文件只有一个Hash值
     */
    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    /**
     * 获取预留字段
     *
     * @return text1 - 预留字段
     */
    public String getText1() {
        return text1;
    }

    /**
     * 设置预留字段
     *
     * @param text1 预留字段
     */
    public void setText1(String text1) {
        this.text1 = text1;
    }

    /**
     * 获取预留字段
     *
     * @return text2 - 预留字段
     */
    public String getText2() {
        return text2;
    }

    /**
     * 设置预留字段
     *
     * @param text2 预留字段
     */
    public void setText2(String text2) {
        this.text2 = text2;
    }

    /**
     * 获取预留字段
     *
     * @return text3 - 预留字段
     */
    public String getText3() {
        return text3;
    }

    /**
     * 设置预留字段
     *
     * @param text3 预留字段
     */
    public void setText3(String text3) {
        this.text3 = text3;
    }
}