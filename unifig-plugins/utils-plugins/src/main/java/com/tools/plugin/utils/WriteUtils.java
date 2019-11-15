package com.tools.plugin.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;

/**
 * 文件写工具类
 *
 */
public class WriteUtils {
	/**
	 * 单个线程下，线程安全
	 */
	public static void addFileContent1(File outFile, String content, boolean wrap, String outCharset) {
		PrintWriter pw = null;
		OutputStreamWriter writeStream = null;
		try {
			if (!outFile.exists()) {
				String parentDir = outFile.getParent();
				File dirParent = new File(parentDir);
				dirParent.mkdirs();;
			}
			// 创建一个向指定 File 对象表示的文件中写入数据的文件输出流。如果第二个参数为
			// true，则将字节写入文件末尾处，而不是写入文件开始处。
			writeStream = new OutputStreamWriter(new FileOutputStream(outFile.getPath(), true), outCharset);
			pw = new PrintWriter(writeStream);
			if (wrap) {
				pw.println(content);
			} else {
				pw.print(content);
			}
			pw.flush();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				pw.close();
				writeStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 线程安全的，不会出现串行 多个线程操作FileWriter或者BufferedWriter时，每一次写入操作都是可以保证原子性的，也即：FileWriter或者BufferedWriter是线程安全的
	 * @param outFile
	 * @param conent
	 * @param wrap
	 */
	public static void addFileContent2(File outFile, String conent, boolean wrap, String outCharset) {
		BufferedWriter out = null;
		OutputStreamWriter writeStream = null;
		try {
			if(!outFile.exists()){
				String parentDir = outFile.getParent();
				File dirParent = new File(parentDir);
				dirParent.mkdirs();
			}
			if (StringUtils.isEmpty(outCharset)) {
				outCharset = "UTF-8";
			}
			// 创建一个向指定 File 对象表示的文件中写入数据的文件输出流。如果第二个参数为
			// true，则将字节写入文件末尾处，而不是写入文件开始处。
			writeStream = new OutputStreamWriter(new FileOutputStream(outFile.getPath(), true), outCharset);
			out = new BufferedWriter(writeStream);
			if (wrap) {
				out.write(conent + "\r\n");
			} else {
				out.write(conent);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				writeStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void addFileContent3(File outFile, String content, boolean wrap, String outCharset) {
		try {
			if (!outFile.exists()) {
				String parentDir = outFile.getParent();
				File dirParent = new File(parentDir);
				dirParent.mkdirs();
			}
			if (StringUtils.isEmpty(outCharset)) {
				outCharset = "UTF-8";
			}
			// 打开一个随机访问文件流，按读写方式
			RandomAccessFile randomFile = new RandomAccessFile(outFile.getPath(), "rw");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			// 将写文件指针移到文件尾。
			randomFile.seek(fileLength);
			if (wrap) {
				randomFile.writeBytes(new String(content.getBytes(), outCharset) + "\r\n");
			} else {
				randomFile.writeBytes(new String(content.getBytes(), outCharset));
			}
			randomFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
