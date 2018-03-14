package com.jfhealthcare.modules.system.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.jfhealthcare.common.entity.AreaObject;
import com.jfhealthcare.common.entity.FileUploadMessage;
import com.jfhealthcare.common.enums.FileStatusEnum;
import com.jfhealthcare.common.enums.FileUploadResponseCodeEnum;
import com.jfhealthcare.common.enums.MQTargetEnum;
import com.jfhealthcare.common.enums.MQTopicEnum;
import com.jfhealthcare.common.exception.FileHashException;
import com.jfhealthcare.common.exception.FileStandardToDicomException;
import com.jfhealthcare.common.exception.InitUploadFileException;
import com.jfhealthcare.common.exception.UploadFileToDicomException;
import com.jfhealthcare.common.utils.DealFileUploadResponseUtil;
import com.jfhealthcare.common.utils.DigestUtil;
import com.jfhealthcare.common.utils.UploadFileToDicomUtil;
import com.jfhealthcare.modules.system.entity.SysUploadFile;
import com.jfhealthcare.modules.system.mapper.SysUploadFileMapper;
import com.jfhealthcare.modules.system.service.SysUploadFileService;

import lombok.extern.slf4j.Slf4j;
import tk.mybatis.mapper.entity.Example;

@Service
@Slf4j
public class SysUploadFileServiceImpl implements SysUploadFileService {
	@Autowired
	private SysUploadFileMapper sysUploadFileMapper;
	@Autowired
	private UploadFileToDicomUtil uploadFileToDicomUtil;
	@Autowired
	private DealFileUploadResponseUtil dealFileUploadResponseUtil;
	//@Autowired
	//private MqProductServiceImpl mqProductServiceImpl;

	@Override
	public SysUploadFile queryFileStatus(String hash) {
		try {
			Example ex = new Example(SysUploadFile.class);
			ex.createCriteria().andEqualTo("fileHash", hash);
			List<SysUploadFile> sysUpload = sysUploadFileMapper.selectByExample(ex);
			if (sysUpload == null || sysUpload.size() == 0) {
				return null;
			} else {
				return sysUpload.get(0);
			}
		} catch (Exception e) {
			log.error("查询文件状态表失败:{}", hash);
			return null;
		}
	}

	@Override
	public void saveFileStatus(SysUploadFile sysUploadFile) {
		try {
			sysUploadFileMapper.insert(sysUploadFile);
		} catch (Exception e) {
			log.error("插入文件状态表失败:{}", sysUploadFile.getFileHash());
		}
	}

	@Override
	public void updateFileStatus(SysUploadFile sysUploadFile) {
		try {
			sysUploadFileMapper.updateByPrimaryKeySelective(sysUploadFile);
		} catch (Exception e) {
			log.error("更新文件状态表失败:{}", sysUploadFile.getFileHash());
		}
	}

	/**
	 * 检测文件是否传完
	 * 
	 * @param tryLock
	 * @param queryFileStatus
	 * @param raf
	 * @param userId
	 * @param channel
	 * 
	 * @throws Exception
	 */
	@Override
	public boolean checkIsAll(FileLock tryLock, AreaObject checkIsNeed, long total, String hash, String dcm4cheeUrl,
			RandomAccessFile raf, String filePath, String userId, FileChannel channel) throws Exception {
		// 整合完毕，查看文件是否已经完全
		try {
			String cin = checkIsNeed.getResult().get(0);
			String[] cins = cin.split("-");
			SysUploadFile queryFileStatus = null;
			if (checkIsNeed.getResult().size() == 1 && 0 == Long.parseLong(cins[0])
					&& total == Long.parseLong(cins[1]) + 1) {
				// HASH校验
				String fileMD5 = DigestUtil.getFileMD5(DigestUtil.SHA1, raf);
				fileMD5 = StringUtils.isNotBlank(fileMD5) ? fileMD5 : "";
				log.info("计算出的hash为:{}",fileMD5);
				if (!fileMD5.equals(hash)) {
					throw new FileHashException("文件HASH值对比失败!");
				}
				// 更改文件状态为已经上传
				queryFileStatus = queryFileStatus(hash);
				updateFileStatus(userId, queryFileStatus, FileStatusEnum.FILE_STATUS_1.getCode());
				// 释放锁
				tryLock.release();
				// 上传文件到后续服务器中
				String jsonResult = uploadFileToDicomUtil.uploadFile(filePath, hash, dcm4cheeUrl, raf, channel);
				log.info("收到服务器返回体:{}", jsonResult);
				// 解析返回报文
				FileUploadMessage fum = new FileUploadMessage();
				int resultJsonToFum = dealFileUploadResponseUtil.getResultJsonToFum(jsonResult, fum);
				// 将对象放入消息队列中!
				if (resultJsonToFum == FileUploadResponseCodeEnum.ERROR_CODE_203.getCode()) {
					updateFileStatus(userId, queryFileStatus,
							FileStatusEnum.FILE_STATUS_3.getCode());
					throw new FileStandardToDicomException("上传文件不规范!");
				} else if (resultJsonToFum == FileUploadResponseCodeEnum.ERROR_CODE_500.getCode()) {
					throw new UploadFileToDicomException("上传业务失败,失败返回码409");
				}
				// resultJsonToFum 返回202时
				/*mqProductServiceImpl.sendMessage(
						JSONObject.toJSONString(dealFileUploadResponseUtil.getMessageMap(fum, userId)),
						MQTopicEnum.FILE_UPLOAD_TOPIC, MQTargetEnum.FILE_UPLOAD_SUCCESS_TARGET);*/
				updateFileStatus(userId, queryFileStatus, FileStatusEnum.FILE_STATUS_2.getCode());
				return true;
			}
			return false;
		} catch (FileStandardToDicomException fse) {
			throw fse;
		} catch (FileHashException fhe) {
			throw fhe;
		} catch (UploadFileToDicomException ude) {
			throw ude;
		} catch (ConnectException ce) {
			throw ce;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 创建正文文件 以及域文件
	 * 
	 * @param isDelete
	 * 
	 * @throws IOException
	 */
	@Override
	public void createFile(String fileNameArea, File fpa, String fileName, File fp, MultipartFile file, long from,
			long size, long total, String path, String path2, long to, AreaObject checkIsNeed, String hash,
			String dcm4cheeUrl, String userId) throws Exception {
		boolean isNeedDelete = false;
		LinkedList<String> linkList = new LinkedList<String>();
		RandomAccessFile raf = null;
		FileChannel channel = null;
		FileLock tryLock = null;
		try {
			// 创建一把锁
			raf = new RandomAccessFile(path + File.separator + fileName, "rw");
			raf.setLength(total);
			channel = raf.getChannel();
			try {
				tryLock = channel.tryLock();
				if (!tryLock.isValid() || tryLock == null) {
					// 文件锁获取失败
					throw new OverlappingFileLockException();
				}
			} catch (IOException ioe) {
				log.error("文件正在被占用!", ioe.getMessage());
				throw new OverlappingFileLockException();
			}
			// 将上传字节转换到正文文件中
			raf.seek(from);
			raf.write(file.getBytes());
			// 创建域文件
			fpa.createNewFile();
			// 组合数据存入域文件中!
			linkList.addFirst(from + "-" + to);
			checkIsNeed.setNeed(true);
			checkIsNeed.setResult(linkList);

			// 修改库中文件的状态为0
			SysUploadFile queryFileStatus = queryFileStatus(hash);
			updateFileStatus(userId, queryFileStatus, FileStatusEnum.FILE_STATUS_0.getCode());
			// 写入域文件内容
			uploadFileToDicomUtil.writeToRangeFile(linkList, fpa.getAbsolutePath());
			isNeedDelete = checkIsAll(tryLock, checkIsNeed, total, hash, dcm4cheeUrl, raf,
					path + File.separator + fileName, userId, channel);
		} catch (FileStandardToDicomException fse) {
			isNeedDelete = true;
			throw fse;
		} catch (FileHashException fhe) {
			isNeedDelete = true;
			throw fhe;
		} catch (UploadFileToDicomException ude) {
			if (ude.getMessage().contains("上传业务失败,失败返回码400") || ude.getMessage().contains("上传业务失败,失败返回码415")) {
				isNeedDelete = true;
			} else {
				isNeedDelete = false;
			}
			throw ude;
		} catch (ConnectException ce) {
			isNeedDelete = false;
			throw ce;
		} catch (IOException e) {
			isNeedDelete = false;
			throw new InitUploadFileException("上传文件初始化失败!", e);
		} catch (Exception e) {
			isNeedDelete = false;
			throw e;
		} finally {
			// 释放锁
			uploadFileToDicomUtil.closeStream(channel, raf, tryLock, fileName, isNeedDelete, fp, fpa);
		}
	}
	/**
	 * 更新文件状态
	 */
	private void updateFileStatus(String userId, SysUploadFile queryFileStatus, int status) {
		if (queryFileStatus != null && queryFileStatus.getFileStatus() < FileStatusEnum.FILE_STATUS_2.getCode()) {
			queryFileStatus.setUpdateTime(new Date());
			queryFileStatus.setFileStatus(status);
			queryFileStatus.setUpdater(userId);
			updateFileStatus(queryFileStatus);
		}
	}
}
