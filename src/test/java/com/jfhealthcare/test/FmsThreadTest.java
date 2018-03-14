package com.jfhealthcare.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.apache.http.HttpStatus;

import com.jfhealthcare.common.exception.UploadFileToDicomException;
import com.jfhealthcare.common.utils.DigestUtil;

public class FmsThreadTest {


	public static void main(String[] args) {
		ArrayList<String> fileNames = getFileNames("D:\\fileSystemTest\\file\\测试专用");
		System.out.println(fileNames.size() + "===" + fileNames.get(1));
		for (int i = 0; i < 10000; i++) {
			Random random = new Random();
			int ni = random.nextInt(10001);
			MyRunnable my = new FmsThreadTest().new MyRunnable(fileNames.get(ni));
			Thread t = new Thread(my);
			t.start();
		}
		

	}

	class MyRunnable implements Runnable {
		public static final String dcm4cheeUrl = "http://v2.jfhealthcare.cn/v1/picl/dcm";
		public static final String boundary = "dicomdicomdicomdicomdicomdicomdicomdicom";
		// 换行符
		public static final String newLine = "\r\n";
		public static final String boundaryPrefix = "--";
		private String fileName1 = null;

		public MyRunnable(String fileName1) {
			this.fileName1 = fileName1;
		}

		@Override
		public void run() {
			String signuature = DigestUtil.getFileMD5Test(DigestUtil.SHA1, new File(fileName1), 1);
			while(true){
			OutputStream out = null;
			DataInputStream in = null;
			BufferedReader reader = null;
			try {


				// 传输字节大小
				long allSize = 0;

				// 定义数据分隔线
				String BOUNDARY = boundary;
				// 定义最后数据分隔线，即--加上BOUNDARY再加上--。
				byte[] end_data = (boundaryPrefix + BOUNDARY + boundaryPrefix + newLine).getBytes();
				// 上传文件头
				File file = new File(fileName1);
				StringBuilder sb = new StringBuilder();
				sb.append(boundaryPrefix);
				sb.append(BOUNDARY);
				sb.append(newLine);
				sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"" + newLine);
				sb.append("Content-Type:application/octet-stream");
				
				// 参数头设置完以后需要两个换行，然后才是参数内容
				sb.append(newLine);
				sb.append(newLine);
				StringBuilder sb1 = new StringBuilder();
				sb1.append(boundaryPrefix);
				sb1.append(BOUNDARY);
				sb1.append(newLine);
				sb1.append("Content-Disposition: form-data;name=\"size\";"+ "\"" + newLine);
				sb1.append(""+file.length());
				sb1.append(boundaryPrefix);
				sb1.append(BOUNDARY);
				sb1.append(newLine);
				sb1.append("Content-Disposition: form-data;name=\"range\";"+ "\"" + newLine);
				sb1.append("[0,"+(file.length()-1)+"]");
				sb1.append(boundaryPrefix);
				sb1.append(BOUNDARY);
				sb1.append(newLine);
				sb1.append("Content-Disposition: form-data;name=\"signature\";"+ "\"" + newLine);
				sb1.append(signuature);
				sb1.append(boundaryPrefix);
				sb1.append(BOUNDARY);
				sb1.append(newLine);
				sb1.append("Content-Disposition: form-data;name=\"fragment_signature\";"+ "\"" + newLine);
				sb1.append(signuature);
				sb1.append(boundaryPrefix);
				sb1.append(BOUNDARY);
				sb1.append(newLine);
				sb1.append("Content-Disposition: form-data;name=\"userId\";"+ "\"" + newLine);
				sb1.append("laoyuan");
				sb1.append(boundaryPrefix);
				sb1.append(BOUNDARY);
				sb1.append(newLine);
				sb1.append("Content-Disposition: form-data;name=\"zip\";"+ "\"" + newLine);
				sb1.append("none");
				allSize = allSize + sb.length() + newLine.length() + end_data.length + file.length()+sb1.length();

				// 服务器的域名
				URL url = new URL(dcm4cheeUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(300 * 1000);
				// 服务器响应response时间
				conn.setReadTimeout(300 * 1000);
				// 设置为POST请求
				conn.setRequestMethod("POST");
				// 发送POST请求必须设置如下两行
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setUseCaches(false);
				// 设置请求头参数
				// conn.setRequestProperty("Host", "192.168.10.92:8080");
				conn.setRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
				conn.setRequestProperty("Accept", "application/dicom+json");
				conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
				conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
				conn.setRequestProperty("Content-Type",
						"multipart/form-data;type=application/octet-stream;boundary=" + BOUNDARY);
				// conn.setRequestProperty("Referer",
				// "http://192.168.10.92:8080/dcm4chee-arc/ui2/");
				conn.setRequestProperty("Connection", "keep-alive");
				conn.setRequestProperty("Content-Length", Long.toString(allSize));

				out = new DataOutputStream(conn.getOutputStream());

				// 上传文件
				// 将参数头的数据写入到输出流中
				out.write(sb.toString().getBytes());

				// 数据输入流,用于读取文件数据
				byte[] bufferOut = new byte[1024 * 512];
				int bytes = 0;
				// 每次读512KB数据,并且将文件数据写入到输出流中
				in = new DataInputStream(new FileInputStream(file));
				while ((bytes = in.read(bufferOut)) != -1) {
					out.write(bufferOut, 0, bytes);
				}
				// 最后换添加行
				out.write(newLine.getBytes());
				in.close();

				// 写上结尾标识
				
				Date d = new Date();
				Long time_start = d.getTime();
				out.write(sb1.toString().getBytes());
				out.write(end_data);
				out.flush();
				out.close();

				StringBuffer jsonResult = new StringBuffer();
				String line = null;
				// 判断是否文件上传异常
				System.out.println("文件上传返回码:"+conn.getResponseCode()+",返回描述:"+conn.getResponseMessage());
				Date d2 = new Date();
				Long time_end = d2.getTime();
				System.out.println("用时:" + ((time_end - time_start) / 1000));
				if (conn.getResponseCode() != HttpStatus.SC_OK && conn.getResponseCode() != HttpStatus.SC_ACCEPTED) {
					reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
					while ((line = reader.readLine()) != null) {
						jsonResult.append(line);
					}
					System.out.println("调用dicom服务器,返回的错误信息如下:"+jsonResult.toString());
					if (conn.getResponseCode() == HttpStatus.SC_CONFLICT) {
						System.out.println(jsonResult.toString());
					} else {
						throw new UploadFileToDicomException("上传业务失败,失败返回码" + conn.getResponseCode());
					}
				}
				// 定义BufferedReader输入流来读取URL的响应
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while ((line = reader.readLine()) != null) {
					jsonResult.append(line);
				}
				System.out.println(jsonResult.toString());

			} catch (Exception e) {
				e.printStackTrace();
			}
			}
		}

	}

	private static ArrayList<String> getFileNames(String rootFilePath) {
		ArrayList<String> fileNames = new ArrayList<String>();
		File f = new File(rootFilePath);
		fileName(fileNames, f);
		return fileNames;
	}

	private static void fileName(ArrayList<String> fileNames, File f) {
		File[] listFiles = f.listFiles();
		for (File file : listFiles) {
			if (file.isDirectory()) {
				fileName(fileNames, file);
			} else {
				fileNames.add(file.getAbsolutePath());
			}
		}
	}

}
