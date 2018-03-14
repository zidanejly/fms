package com.jfhealthcare.common.utils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.channels.OverlappingFileLockException;
import java.util.Date;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jfhealthcare.common.entity.AreaObject;
import com.jfhealthcare.common.entity.UploadMessage;
import com.jfhealthcare.common.enums.FileStatusEnum;
import com.jfhealthcare.common.enums.FileUploadResponseCodeEnum;
import com.jfhealthcare.common.exception.FileHashException;
import com.jfhealthcare.common.exception.FileStandardToDicomException;
import com.jfhealthcare.common.exception.InitUploadFileException;
import com.jfhealthcare.common.exception.UploadFileToDicomException;
import com.jfhealthcare.modules.system.entity.SysUploadFile;
import com.jfhealthcare.modules.system.service.SysUploadFileService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExceptionDealUtil {
	@Autowired
	private SysUploadFileService sysUploadFileService;
	public  UploadMessage dealException(Exception e, String fileName, String hash,String userId,AreaObject checkIsNeed, long total) {
		UploadMessage up = new UploadMessage();
		if (e instanceof FileStandardToDicomException) {
			log.error("上传文件不规范!:{},错误码:{}", fileName, FileUploadResponseCodeEnum.ERROR_CODE_508.getCode(), e);
			up.getUploadResponse(FileUploadResponseCodeEnum.ERROR_CODE_203.getCode(), hash, checkIsNeed.getResult());
			up.setDelete(true);
			return up;
		}
		if (e instanceof FileHashException) {
			log.error("上传文件HASH值校验失败:{},错误码:{}", fileName, FileUploadResponseCodeEnum.ERROR_CODE_506.getCode(), e);
			up.getUploadResponse(FileUploadResponseCodeEnum.ERROR_CODE_500.getCode(), hash, checkIsNeed.getResult());
			up.setDelete(true);
			return up;
		}
		if (e instanceof OverlappingFileLockException) {
			log.error("上传文件正在被占用:{},错误码:{}", fileName, FileUploadResponseCodeEnum.ERROR_CODE_519.getCode(), e);
			up.getUploadResponse(FileUploadResponseCodeEnum.ERROR_CODE_500.getCode(), hash, checkIsNeed.getResult());
			return up;
		}
		if (e instanceof UploadFileToDicomException) {
			log.error("文件上传dicom服务器,业务失败!:{},错误码:{}", fileName, FileUploadResponseCodeEnum.ERROR_CODE_508.getCode(), e);
			if(e.getMessage().contains("上传业务失败,失败返回码400") || e.getMessage().contains("上传业务失败,失败返回码415")){
				up.setDelete(true);
				//更改文件状态为已经上传失败
				SysUploadFile queryFileStatus = sysUploadFileService.queryFileStatus(hash);
				if(queryFileStatus!=null){
					queryFileStatus.setUpdateTime(new Date());
					queryFileStatus.setFileStatus(FileStatusEnum.FILE_STATUS_3.getCode());
					queryFileStatus.setUpdater(userId);
					sysUploadFileService.updateFileStatus(queryFileStatus);
				}
				LinkedList<String> link = new LinkedList<String>();
				link.addFirst("0-" + (total - 1));
				up.getUploadResponse(FileUploadResponseCodeEnum.ERROR_CODE_203.getCode(), hash,link);
			}else{
				up.setDelete(false);
				up.getUploadResponse(FileUploadResponseCodeEnum.ERROR_CODE_500.getCode(), hash, checkIsNeed.getResult());
			}
			return up;
		}
		if (e instanceof InitUploadFileException) {
			log.error("上传文件初始化失败:{},错误码:{}",fileName,FileUploadResponseCodeEnum.ERROR_CODE_518.getCode(), e);
			up.getUploadResponse(FileUploadResponseCodeEnum.ERROR_CODE_500.getCode(), hash, checkIsNeed.getResult());
			return up;
		}
		if (e instanceof ConnectException||e instanceof SocketTimeoutException) {
			log.error("文件上传dicom服务器超时!:{},错误码:{}", fileName, FileUploadResponseCodeEnum.ERROR_CODE_507.getCode(), e);
			up.getUploadResponse(FileUploadResponseCodeEnum.ERROR_CODE_500.getCode(), hash, checkIsNeed.getResult());
			return up;
		}
		if (e instanceof IOException) {
			log.error("上传文件合成失败:{},错误码:{}", fileName, FileUploadResponseCodeEnum.ERROR_CODE_504.getCode(),e);
			up.getUploadResponse(FileUploadResponseCodeEnum.ERROR_CODE_500.getCode(), hash, checkIsNeed.getResult());
			return up;
		}
		if (e instanceof Exception) {
			log.error("系统异常!:{}", fileName, e);
			up.getUploadResponse(FileUploadResponseCodeEnum.ERROR_CODE_500.getCode(), hash, checkIsNeed.getResult());
			return up;
		}
		log.info("未知异常!",e);
		up.getUploadResponse(FileUploadResponseCodeEnum.ERROR_CODE_203.getCode(), hash, checkIsNeed.getResult());
		up.setDelete(true);
		return up;
	}
}
