package com.jfhealthcare.modules.system.service;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.springframework.web.multipart.MultipartFile;

import com.jfhealthcare.common.entity.AreaObject;
import com.jfhealthcare.modules.system.entity.SysUploadFile;

public interface SysUploadFileService {

	SysUploadFile queryFileStatus(String hash);
	void saveFileStatus(SysUploadFile sysUploadFile);
	void updateFileStatus(SysUploadFile sysUploadFile);
	boolean checkIsAll(FileLock tryLock, AreaObject checkIsNeed, long total, String hash, String dcm4cheeUrl,
			RandomAccessFile raf, String filePath, String userId, FileChannel channel) throws Exception;
	void createFile(String fileNameArea, File fpa, String fileName, File fp, MultipartFile file, long from,
			long size, long total, String path, String path2, long to, AreaObject checkIsNeed, String hash,
			String dcm4cheeUrl, String userId) throws Exception;

}
