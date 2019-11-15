package com.tools.plugin.utils.ftp;

import java.io.File;

/**
 * 路径扩展
 */
public class PathExtend {
	/**
	 * 合并路径
	 * 
	 * @param args
	 * @return
	 */
	public static String Combine(String... args) {
		if (args == null || args.length == 0)
			return "";
		StringBuffer sbf = new StringBuffer();
		for (String s : args) {
			// //纯协议开头不处理，如：http://,d:/,linux首个/不处理
			// if(s.matches("^[a-zA-z]+://$")){
			// sbf.append(s);
			// continue;
			// }
			// 首位地址只删除尾部正反斜杠
			if (sbf.length() == 0) {
				sbf.append(s.replaceAll("/{1,}$|\\{1,}$", ""));
				continue;
			}

			if (sbf.length() > 0)
				sbf.append("/");
			// 去除首尾正反斜杠
			sbf.append(s.replaceAll("^/{1,}|^\\{1,}", "").replaceAll("/{1,}$|\\{1,}$", ""));
		}

		return sbf.toString();
	}

	/**
	 * 获取应用程序 classpath 路径
	 * 
	 * @return
	 */
	public static String getClassPath() {
		return PathExtend.class.getResource("/").getPath();
	}

	/**
	 * 将相对路径转为绝对路径（相对与 calsspath 的路径）
	 * 
	 * @param relativePath
	 * @return
	 */
	public static String getAbsolutePath(String relativePath) {
		return Combine(getClassPath(), relativePath);
	}

	/**
	 * 获取路径中的目录部分
	 * 
	 * @param path
	 * @return
	 */
	public static String getDirectory(String path) {
		return path.replaceAll("(/)([^/])+\\.([^/])+$", "");
	}

	/**
	 * 获取路径中的文件名部分
	 * 
	 * @param path
	 * @return
	 */
	public static String getFileName(String path) {
		return path.replaceAll("^.+(/)", "");
	}

	/**
	 * 创建目录(存在则不创建)
	 * 
	 * @return
	 */
	public static boolean createDirectory(String dirName) {
		File file = new File(dirName);
		if (file.exists())
			return true;
		return file.mkdirs();
	}
}
