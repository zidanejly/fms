package com.jfhealthcare.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.MessageDigest;

import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DigestUtil {
	/**
	 * 
	 * @param file
	 * @param algorithm
	 *            所请求算法的名称 for example: MD5, SHA1, SHA-256, SHA-384, SHA-512
	 *            etc.
	 * @return
	 */
	public static final String SHA1 = "SHA1";

	public static String getFileMD5(String algorithm, RandomAccessFile raf) {
		MessageDigest digest = null;
		byte buffer[] = new byte[1024];
		int len;

		try {
			raf.seek(0);
			digest = MessageDigest.getInstance(algorithm);
			while ((len = raf.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			String bytes2hex03 = bytes2hex03(digest.digest());
			return bytes2hex03;
		} catch (Exception e) {
			log.error("HASH算法失败!", e);
			return null;
		}
		/*
		 * BigInteger bigInt = new BigInteger(1, digest.digest()); return
		 * bigInt.toString(16);
		 */
	}

	public static String getFilePartMD5(String algorithm, MultipartFile file) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance(algorithm);
			digest.update(file.getBytes());
			String bytes2hex03 = bytes2hex03(digest.digest());
			return bytes2hex03;
		} catch (Exception e) {
			log.error("HASH算法失败!", e);
			return null;
		}
	}

	public static String getFileMD5Test(String algorithm, File file, int i) {
		if (!file.exists()) {
			return null;
		}
		MessageDigest digest = null;
		byte buffer[] = new byte[1024];
		int len;
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			digest = MessageDigest.getInstance(algorithm);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			log.error("HASH算法失败!", e);
			return null;
		}
		if (i == 1) {
			String bytes2hex03 = bytes2hex03(digest.digest());
			System.out.println(bytes2hex03 + "," + bytes2hex03.length());
			return bytes2hex03;
		} else {
			BigInteger bigInt = new BigInteger(1, digest.digest());
			System.out.println("数组长度:" + digest.digest().length);
			return bigInt.toString(16);
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		// String fileMD5Test = getFileMD5Test("SHA1",new
		// File("C:\\Users\\pc00098\\Desktop\\dicom文件\\(PAT015953)_1-1_1_WI_20170722_003E270B.DCM"));
		//String fileMD5Test = getFileMD5Test("SHA1", new File("C:\\Users\\pc00098\\Desktop\\上线准备文件\\Erie_2.0.1_windows_7sp1_x64.zip"), 1);
		String fileMD5Test = getFileMD5Test("SHA1", new File("C:\\Users\\pc00098\\Desktop\\上线准备文件\\Erie_2.0.1_windows_xpsp3_x86.zip"), 1);
		//String fileMD5Test = getFileMD5Test("SHA1", new File("D:\\fileSystemTest\\file\\Erie_2.0.1_windows_xpsp3_x86.zip"), 1);
		// String fileMD5Test = getFileMD5Test("SHA1",new
		// File("C:\\Users\\pc00098\\Desktop\\dicom文件\\GUO LIE
		// ZHANG(CT16007792)_4-15_1_WI_20160313_00118009.DCM"),1);
		System.out.println(fileMD5Test.length());
		String s = "0f69cc27cf16bd6dca3efe1c1a1220df6678b450";
		System.out.println(s.length());
		System.out.println("fileMD5值计算出来为:" + fileMD5Test + "\r\n"
				+ "69096090f62678d3b0fe1f85e6de1de2d4547104".equals(fileMD5Test));

		// RandomAccessFile raf_all = new RandomAccessFile(new
		// File("D:\\fileSystemTest\\file\\suan.dcm"), "rwd");
		// String filePartMD5 = getFilePartMD5("SHA1", raf_all, 6144, 6655);
		// System.out.println(filePartMD5 + "<--计算出的文件部分MD5");

	}

	public static String bytes2hex03(byte[] bytes) {
		{
			final String HEX = "0123456789abcdef";
			StringBuilder sb = new StringBuilder(bytes.length * 2);
			for (byte b : bytes) {
				// 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
				sb.append(HEX.charAt((b >> 4) & 0x0f));
				// 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
				sb.append(HEX.charAt(b & 0x0f));
			}

			return sb.toString();
		}
	}

}
