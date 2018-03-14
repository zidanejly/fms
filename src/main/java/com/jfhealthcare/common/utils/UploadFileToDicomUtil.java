package com.jfhealthcare.common.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.LinkedList;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.jfhealthcare.common.entity.AreaObject;
import com.jfhealthcare.common.exception.UploadFileToDicomException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UploadFileToDicomUtil {

	public String uploadFile(String fileName, String boundary, String dcm4cheeUrl, RandomAccessFile raf,
			FileChannel channel) throws Exception {
		OutputStream out = null;
		DataInputStream in = null;
		BufferedReader reader = null;
		try {
			// 换行符
			final String newLine = "\r\n";
			final String boundaryPrefix = "--";
			// 传输字节大小
			long allSize = 0;
			// 定义数据分隔线
			String BOUNDARY = boundary;
			// 定义最后数据分隔线，即--加上BOUNDARY再加上--。
			byte[] end_data = (boundaryPrefix + BOUNDARY + boundaryPrefix + newLine).getBytes();
			// 上传文件头
			File file = new File(fileName);
			StringBuilder sb = new StringBuilder();
			sb.append(boundaryPrefix);
			sb.append(BOUNDARY);
			sb.append(newLine);
			sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"" + newLine);
			sb.append("Content-Type:application/dicom");
			// 参数头设置完以后需要两个换行，然后才是参数内容
			sb.append(newLine);
			sb.append(newLine);
			allSize = allSize + sb.length() + newLine.length() + end_data.length + file.length();
			// 服务器的域名
			URL url = new URL(dcm4cheeUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			setConnect(allSize, BOUNDARY, conn);

			out = new DataOutputStream(conn.getOutputStream());

			// 上传文件
			// 将参数头的数据写入到输出流中
			out.write(sb.toString().getBytes());

			// 数据输入流,用于读取文件数据
			byte[] bufferOut = new byte[1024 * 512];
			int bytes = 0;
			// 将指针位置拨回文件开头
			raf.seek(0);
			// 每次读512KB数据,并且将文件数据写入到输出流中
			while ((bytes = raf.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
				out.flush();
			}
			if (channel != null) {
				channel.close();
			}
			if (raf != null) {
				raf.close();
			}
			// 最后换添加行
			out.write(newLine.getBytes());

			// 写上结尾标识
			out.write(end_data);
			out.flush();
			out.close();
			StringBuffer jsonResult = new StringBuffer();
			String line = null;
			// 判断是否文件上传异常
			log.info("文件上传返回码:{},返回描述:{}", conn.getResponseCode(), conn.getResponseMessage());
			if (conn.getResponseCode() != HttpStatus.SC_OK && conn.getResponseCode() != HttpStatus.SC_ACCEPTED) {
				reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				while ((line = reader.readLine()) != null) {
					jsonResult.append(line);
				}
				log.info("调用dicom服务器,返回的错误信息如下:{}", jsonResult.toString());
				if (conn.getResponseCode() == HttpStatus.SC_CONFLICT) {
					return jsonResult.toString();
				} else {
					throw new UploadFileToDicomException("上传业务失败,失败返回码" + conn.getResponseCode());
				}
			}
			// 定义BufferedReader输入流来读取URL的响应
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = reader.readLine()) != null) {
				jsonResult.append(line);
			}
			return jsonResult.toString();

		} catch (UploadFileToDicomException ue) {
			throw ue;
		} catch (ConnectException ce) {
			throw ce;
		} catch (Exception e) {
			throw e;
		} finally {
			closeUpFileToDcmStream(out, in, reader);
		}
	}

	/**
	 * 检测是否需要合并
	 * 
	 * @throws Exception
	 */
	public AreaObject checkIsNeed(long from, long to, File fpa) {
		AreaObject area = new AreaObject();
		ObjectInputStream oin = null;
		LinkedList<String> readObject = new LinkedList<String>();
		try {
			if (fpa.length() == 0) {
				readObject.add(0, "0-0");
			} else {
				oin = new ObjectInputStream(new FileInputStream(fpa));
				readObject = (LinkedList<String>) oin.readObject();
			}
			if (from > to) {
				area.setResult(readObject);
				area.setNeed(false);
				return area;
			}
			LinkedList<String> lsnew = new LinkedList<String>();
			long reMaxTo = Long.parseLong(readObject.getLast().split("-")[1]);
			long reMaxFrom = Long.parseLong(readObject.getLast().split("-")[0]);
			if (from > reMaxTo + 1) {
				// 如果比所有都大，则直接处理
				readObject.addLast(from + "-" + to);
				area.setResult(readObject);
				area.setNeed(true);
				return area;
			}
			for (int i = 0; i < readObject.size(); i++) {
				String[] split = readObject.get(i).split("-");
				long sfrom = Long.parseLong(split[0]);
				long sto = Long.parseLong(split[1]);
				if (to < sfrom - 1) {
					// 完全小
					lsnew.add(from + "-" + to);
					lsnew.addAll(readObject.subList(i, readObject.size()));
					area.setNeed(true);
					break;
				} else if (from - 1 > sto) {
					// 完全大
					lsnew.add(readObject.get(i));
				} else {
					// 有覆盖
					if (from >= sfrom && to <= sto) {
						// 如果完全被包围，则告知不用整合文件了,直接返回readObject或者更改表示设置为0
						area.setNeed(false);
						break;
					} else {
						// 有覆盖,设置新边界
						from = from > sfrom ? sfrom : from;
						to = to > sto ? to : sto;
					}
				}
			}
			// 特殊情况 新区域覆盖了最后一个区域（包括新区域完全覆盖所有区域，新区域覆盖部分区域并且包含最后一个区域）
			if (to >= reMaxFrom - 1) {
				lsnew.addLast(from + "-" + to);
			}
			if (area.isNeed()) {
				area.setResult(lsnew);
			} else {
				area.setResult(readObject);
			}
			return area;
		} catch (Exception e) {
			log.error("检测错误:{}", fpa.getName(), e);
			return null;
		} finally {
			if (oin != null) {
				try {
					oin.close();
				} catch (IOException e) {
					log.error("oin流关闭失败:{}", e);
				}
			}
		}
	}

	/**
	 * 写入域文件中的内容
	 * 
	 * @throws Exception
	 */
	public void writeToRangeFile(LinkedList<String> linkList, String fileNameArea) throws Exception {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(fileNameArea));
			out.writeObject(linkList);
		} catch (Exception e) {
			throw e;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					log.error("文件流关闭异常!:{}", fileNameArea, e);
				}
			}

		}
	}

	/**
	 * 关闭流
	 * 
	 * @param isDelete
	 * @param path
	 */
	public void closeStream(FileChannel channel, RandomAccessFile raf, FileLock tryLock, String fileName,
			boolean isDelete, File fp, File fpa) {
		if (tryLock != null) {
			if (tryLock.isValid()) {
				try {
					log.info("finally执行!释放锁:{}", fileName);
					tryLock.release();
				} catch (IOException e) {
					log.error("释放文件锁失败!:{}", fileName, e);
				}
			}
		} else {
			log.info("锁在关闭方法之前已经被释放!");
		}
		if (channel != null) {
			try {
				channel.close();
			} catch (IOException e) {
				log.error("channel流关闭失败:{}", fileName, e);
			}
		}
		if (raf != null) {
			try {
				raf.close();
			} catch (IOException e) {
				log.error("Raf流关闭失败:{}", fileName, e);
			}
		}
		if (isDelete) {
			try {
				fp.delete();
				fpa.delete();
			} catch (Exception e) {
				log.error("删除文件失败!:{}", fp.getName());
			}
		}
	}

	
	/**
	 * 关闭文件流
	 */
	private void closeUpFileToDcmStream(OutputStream out, DataInputStream in, BufferedReader reader) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				log.error("dicom文件关闭in流!", e);
			}
		}
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				log.error("dicom文件关闭out流!", e);
			}
		}
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				log.error("dicom文件关闭reader流!", e);
			}
		}
	}

	/**
	 * 设置链接参数
	 */
	private void setConnect(long allSize, String BOUNDARY, HttpURLConnection conn) throws ProtocolException {
		// 连接服务器时间
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
		conn.setRequestProperty("Content-Type", "multipart/related;type=application/dicom;boundary=" + BOUNDARY);
		// conn.setRequestProperty("Referer",
		// "http://192.168.10.92:8080/dcm4chee-arc/ui2/");
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Content-Length", Long.toString(allSize));
	}
}
