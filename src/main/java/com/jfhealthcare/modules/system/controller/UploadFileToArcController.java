package com.jfhealthcare.modules.system.controller;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Date;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.jfhealthcare.common.base.UploadResponse;
import com.jfhealthcare.common.entity.AreaObject;
import com.jfhealthcare.common.entity.UploadFileEntity;
import com.jfhealthcare.common.entity.UploadMessage;
import com.jfhealthcare.common.enums.FileStatusEnum;
import com.jfhealthcare.common.enums.FileUploadResponseCodeEnum;
import com.jfhealthcare.common.utils.DigestUtil;
import com.jfhealthcare.common.utils.ExceptionDealUtil;
import com.jfhealthcare.common.utils.UploadFileToDicomUtil;
import com.jfhealthcare.modules.system.entity.SysUploadFile;
import com.jfhealthcare.modules.system.request.UploadFileRequest;
import com.jfhealthcare.modules.system.service.SysUploadFileService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/picl/dcm")
@Api(value = "文件断点上传")
public class UploadFileToArcController {
	@Value("${uploadFilePath}")
	private String path;
	@Value("${uploadedRecordPath}")
	private String path2;
	@Value("${dcm4cheeUrl}")
	private String dcm4cheeUrl;
	@Value("${retryTime}")
	private String retryTime;
	@Autowired
	private SysUploadFileService sysUploadFileService;
	@Autowired
	private ExceptionDealUtil exceptionDealUtil;
	@Autowired
	private UploadFileToDicomUtil uploadFileToDicomUtil;

	@ApiOperation(value = "上传文件", notes = "断点上传文件")
	@RequestMapping(method = RequestMethod.POST)
	public UploadResponse uploadFile(@RequestParam(value = "bulk") MultipartFile file,
			@RequestParam(value = "meta") String meta) {
		log.info("传入文件大小:{},接口入参:{}", file.getSize(), meta);
		// 已存范围文件对象
		AreaObject storedRangeObject = new AreaObject();
		// 文件上传对象
		UploadFileEntity paramters = new UploadFileEntity();

		// 1- 准备文件
		try {
			// 提取传入参数
			paramters = getParamters(file, meta, paramters);
		} catch (Exception requestParamException) {
			log.error("初始化入参失败!错误码:{}", FileUploadResponseCodeEnum.ERROR_CODE_505.getCode(), requestParamException);
			return UploadResponse.getUploadResponse(FileUploadResponseCodeEnum.ERROR_CODE_500.getCode(),
					paramters.getHash(), null);
		}
		// 已存范围文件组装查询
		// --注:已存范围文件 是一个专门记录对应文件已经上传范围的文件。
		String storedRangeFileName = paramters.getHash() + ".txt";
		File storedRangeFile = new File(path2, storedRangeFileName);

		// 正文文件组装 --获取上传文件的属性
		String originalFilename = file.getOriginalFilename();
		if (originalFilename.lastIndexOf(File.separator) != -1) {
			originalFilename = originalFilename.substring(originalFilename.lastIndexOf(File.separator) + 1,
					originalFilename.length());
		}
		String type = "";
		if (originalFilename.lastIndexOf(".") != -1) {
			type = originalFilename.substring(originalFilename.lastIndexOf("."), originalFilename.length());
		}
		String fileName = paramters.getHash() + type;
		File fp = new File(path, fileName);

		// 查询文件状态
		UploadResponse queryFileStatus = queryFileStatus(paramters, fp, storedRangeFile);
		if (queryFileStatus != null) {
			return queryFileStatus;
		}
		// 2- 处理文件,并且返回文件状态
		return fileArcHandler(file, storedRangeObject, paramters, storedRangeFileName, storedRangeFile, fileName, fp);
	}

	/**
	 * 处理文件并将文件提交给DICOM服务器存储,最后将返回参数作处理，先扔进队列中，然后再返回客户端结果
	 */
	private UploadResponse fileArcHandler(MultipartFile file, AreaObject storedRangeObject, UploadFileEntity paramters,
			String storedRangeFileName, File storedRangeFile, String fileName, File fp) {
		// 文件是否需要删除
		boolean isDelete = false;
		if (storedRangeFile.exists() && fp.exists()) {
			FileChannel channel = null;
			RandomAccessFile raf = null;
			FileLock tryLock = null;
			try {
				// 获取文件锁
				raf = new RandomAccessFile(path + File.separator + fileName, "rw");
				channel = raf.getChannel();
				storedRangeObject = uploadFileToDicomUtil.checkIsNeed(paramters.getFrom(), paramters.getTo(),
						storedRangeFile);
				try {
					tryLock = channel.tryLock();
					if (tryLock == null || !tryLock.isValid()) {
						// 文件锁获取失败
						throw new OverlappingFileLockException();
					}
				} catch (IOException ioe) {
					log.error("文件正在被占用!", ioe.getMessage());
					throw new OverlappingFileLockException();
				}
				if (storedRangeObject == null) {
					log.error("文件解析异常!错误码:{}", FileUploadResponseCodeEnum.ERROR_CODE_502.getCode());
					return UploadResponse.getUploadResponse(FileUploadResponseCodeEnum.ERROR_CODE_500.getCode(),
							paramters.getHash(), null);
				}
				if (storedRangeObject.isNeed()) {
					// 写入域文件内容
					raf.seek(paramters.getFrom());
					raf.write(file.getBytes());
					uploadFileToDicomUtil.writeToRangeFile(storedRangeObject.getResult(),
							storedRangeFile.getAbsolutePath());
				}
				isDelete = sysUploadFileService.checkIsAll(tryLock, storedRangeObject, paramters.getTotal(),
						paramters.getHash(), dcm4cheeUrl, raf, path + File.separator + fileName, paramters.getUserId(),
						channel);
			} catch (Exception e) {
				UploadMessage dealException = exceptionDealUtil.dealException(e, fileName, paramters.getHash(),
						paramters.getUserId(), storedRangeObject, paramters.getTotal());
				isDelete = dealException.isDelete();
				return dealException.getResponse();
			} finally {
				// 关闭流并且删除文件(根据ISdelete来判断)
				uploadFileToDicomUtil.closeStream(channel, raf, tryLock, fileName, isDelete, fp, storedRangeFile);
			}
		} else {
			try {
				UploadResponse noFileExits = noFileExits(file, storedRangeObject, paramters.getFrom(),
						paramters.getSize(), paramters.getTo(), paramters.getTotal(), paramters.getHash(),
						paramters.getUserId(), storedRangeFileName, storedRangeFile, fileName, fp);
				if (noFileExits != null) {
					return noFileExits;
				}
			} catch (Exception e) {
				return exceptionDealUtil.dealException(e, fileName, paramters.getHash(), paramters.getUserId(),
						storedRangeObject, paramters.getTotal()).getResponse();
			}
		}
		// 判断文件是否完整
		return isFileAll(storedRangeObject, paramters.getTotal(), paramters.getHash());
	}

	/**
	 * 判断文件是否完整
	 */
	private UploadResponse isFileAll(AreaObject checkIsNeed, long total, String hash) {
		String cin = checkIsNeed.getResult().get(0);
		String[] cins = cin.split("-");
		if (checkIsNeed.getResult().size() == 1 && 0 == Long.parseLong(cins[0])
				&& total == Long.parseLong(cins[1]) + 1) {
			return UploadResponse.getUploadResponse(FileUploadResponseCodeEnum.ERROR_CODE_202.getCode(), hash,
					checkIsNeed.getResult());
		}
		return UploadResponse.getUploadResponse(FileUploadResponseCodeEnum.SUCCESS_CODE.getCode(), hash,
				checkIsNeed.getResult());
	}

	/**
	 * 正文文件和域文件缺失的情况下
	 */
	private UploadResponse noFileExits(MultipartFile file, AreaObject checkIsNeed, long from, long size, long to,
			long total, String hash, String userId, String fileNameArea, File fpa, String fileName, File fp)
			throws Exception {
		if (!fpa.exists() && fp.exists()) {
			// 区域文件不存在，正文文件存在时
			fp.delete();
			if (size == 0) {
				return getUploadResponse(hash, FileUploadResponseCodeEnum.SUCCESS_CODE.getCode(), "0-" + "-1");
			}
			sysUploadFileService.createFile(fileNameArea, fpa, fileName, fp, file, from, size, total, path, path2, to,
					checkIsNeed, hash, dcm4cheeUrl, userId);
			return null;
		} else if (fpa.exists() && !fp.exists()) {
			// 区域文件存在 正文文件不存在时
			fpa.delete();
			if (size == 0) {
				return getUploadResponse(hash, FileUploadResponseCodeEnum.SUCCESS_CODE.getCode(), "0-" + "-1");
			}
			sysUploadFileService.createFile(fileNameArea, fpa, fileName, fp, file, from, size, total, path, path2, to,
					checkIsNeed, hash, dcm4cheeUrl, userId);
			return null;
		} else if (!fpa.exists() && !fp.exists()) {
			// 两者都不存在
			if (size == 0) {
				return getUploadResponse(hash, FileUploadResponseCodeEnum.SUCCESS_CODE.getCode(), "0-" + "-1");
			}
			sysUploadFileService.createFile(fileNameArea, fpa, fileName, fp, file, from, size, total, path, path2, to,
					checkIsNeed, hash, dcm4cheeUrl, userId);
			return null;
		} else {
			log.error("文件组合情况超出正常情况!错误码:{}", FileUploadResponseCodeEnum.ERROR_CODE_509.getCode());
			return UploadResponse.getUploadResponse(FileUploadResponseCodeEnum.ERROR_CODE_500.getCode(), hash, null);
		}
	}

	/**
	 * 查询文件的状态
	 * 
	 * @param paramters
	 */
	private UploadResponse queryFileStatus(UploadFileEntity paramters, File fp, File fpa) {
		SysUploadFile queryFileStatus = sysUploadFileService.queryFileStatus(paramters.getHash());
		if (queryFileStatus != null && queryFileStatus.getFileStatus() == FileStatusEnum.FILE_STATUS_1.getCode()) {
			// 文件已经上传，等待DCM服务器响应
			long time = System.currentTimeMillis();
			if (time - queryFileStatus.getUpdateTime().getTime() < (Long.parseLong(retryTime))) {
				// 相隔时间小于5分钟
				return getUploadResponse(paramters.getHash(), FileUploadResponseCodeEnum.ERROR_CODE_201.getCode(),
						"0-" + (paramters.getTotal() - 1));
			} else {
				if (paramters.getSize() == 0 && !fp.exists() && !fpa.exists()) {
					return getUploadResponse(paramters.getHash(), FileUploadResponseCodeEnum.SUCCESS_CODE.getCode(),
							"0-" + "-1");
				}
			}
		} else if (queryFileStatus != null
				&& queryFileStatus.getFileStatus() == FileStatusEnum.FILE_STATUS_2.getCode()) {
			// 文件上传成功!
			return getUploadResponse(paramters.getHash(), FileUploadResponseCodeEnum.ERROR_CODE_202.getCode(),
					"0-" + (paramters.getTotal() - 1));
		} else if (queryFileStatus != null
				&& queryFileStatus.getFileStatus() == FileStatusEnum.FILE_STATUS_3.getCode()) {
			// 文件上传失败(DCM文件服务器解析入库失败)!
			return getUploadResponse(paramters.getHash(), FileUploadResponseCodeEnum.ERROR_CODE_203.getCode(),
					"0-" + (paramters.getTotal() - 1));
		} else if (queryFileStatus == null) {
			if (paramters.getSize() == 0) {
				return getUploadResponse(paramters.getHash(), FileUploadResponseCodeEnum.SUCCESS_CODE.getCode(),
						"0-" + "-1");
			}
			// 新增文件记录入库
			queryFileStatus = new SysUploadFile();
			queryFileStatus.setCreater(paramters.getUserId());
			queryFileStatus.setCreateTime(new Date());
			queryFileStatus.setFileStatus(FileStatusEnum.FILE_STATUS_0.getCode());
			queryFileStatus.setFileHash(paramters.getHash());
			sysUploadFileService.saveFileStatus(queryFileStatus);
		}
		return null;
	}

	/**
	 * 获取返回的Response
	 */
	private UploadResponse getUploadResponse(String hash, int code, String first) {
		LinkedList<String> link = new LinkedList<String>();
		link.addFirst(first);
		return UploadResponse.getUploadResponse(code, hash, link);
	}

	/**
	 * 获取请求参数
	 * 
	 * @param paramters
	 */
	private UploadFileEntity getParamters(MultipartFile file, String meta, UploadFileEntity paramters)
			throws Exception {
		UploadFileRequest ufe = JSONObject.parseObject(meta, UploadFileRequest.class);
		paramters.setFrom(ufe.getRange()[0]);
		paramters.setTo(ufe.getRange()[1]);
		if (paramters.getFrom() > paramters.getTo()) {
			paramters.setSize(0);
		} else {
			paramters.setSize(paramters.getTo() - paramters.getFrom() + 1);
		}
		if (StringUtils.isBlank(ufe.getSignature())) {
			throw new Exception("signnature不能为空!");
		}
		paramters.setHash(ufe.getSignature());
		paramters.setFragment_signature(ufe.getFragment_signature());
		if (file.getSize() != paramters.getSize() || ufe.getSize() < 0) {
			throw new Exception("文件大小与范围大小不一致!");
		}
		//计算本次文件的内容和part是否一致!
		if(paramters.getSize()>0&&paramters.getFrom() <= paramters.getTo()){
			if(StringUtils.isBlank(ufe.getFragment_signature())){
				throw new Exception("文件片段Fragment_signature不能为空!");
			}
			String filePartMD5 = DigestUtil.getFilePartMD5(DigestUtil.SHA1, file);
			log.info("filePartMD5:"+filePartMD5);
			if(!paramters.getFragment_signature().equals(filePartMD5)){
				throw new Exception("文件片段HASH不一致!");
			}
		}
		paramters.setTotal(ufe.getSize());
		paramters.setUserId(ufe.getUserId());
		MDC.put("hash", ufe.getSignature());
		// 根据 zip来查看文件是否压缩 如果是的话 需要解压缩
		if ("none".equals(ufe.getZip()) || StringUtils.isBlank(ufe.getZip())) {
			log.info("无需解压缩!");
		} else {
			log.info("需要解压缩");
		}
		return paramters;
	}
}