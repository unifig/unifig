package com.tools.plugin.utils.ftp;


import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayOutputStream;  


import com.tools.plugin.utils.Exceptions;
import com.tools.plugin.utils.NewMapUtil;
import com.tools.plugin.utils.NumberUtils;
import com.tools.plugin.utils.StringUtil;

public class FtpHelper implements Closeable {
	private final static Logger logger = LoggerFactory.getLogger(FtpHelper.class);
	private static Map<String,Object> ftpMap = new HashMap<>();
	private FTPClient ftp = null;
	boolean _isLogin = false;

	public static FtpHelper getInstance() {
		return new FtpHelper();
	}

	/**
	 * 
	 * ftp 匿名登录;如果没有设置ftp用户可将username设为anonymous，密码为任意字符串
	 * 
	 * @param ip  ftp服务地址
	 * @param port 端口号
	 * @param uname 用户名
	 * @param pass 密码
	 */
	public boolean login(String ip, int port) {
		ftpMap.clear();
		ftpMap.putAll(new NewMapUtil().set("ip", ip)
				                      .set("port", port)
				                      .set("uname", "anonymous")
				                      .set("pass", "")
				                      .get());
		return login(ip, port, "anonymous", "");
	}

	/**
	 * 
	 * ftp登录
	 * 
	 * @param ip ftp服务地址
	 * @param port 端口号
	 * @param uname 用户名
	 * @param pass 密码
	 * @param workingDir ftp 根目目录
	 */
	public boolean login(String ip, int port, String uname, String pass) {
		ftp = new FTPClient();
		try {
			// 设置传输超时时间为60秒 
			ftp.setDataTimeout(60*1000*60);
			// 连接超时为60秒
			ftp.setConnectTimeout(60*1000*10);
			// 连接
			ftp.connect(ip, port);
			_isLogin = ftp.login(uname, pass);
			logger.info("ftp："+(_isLogin ? "登录成功" : "登录失败"));
			// 检测连接是否成功
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				System.err.println("FTP服务器拒绝连接 ");
				return false;
			}
			ftpMap.clear();
			ftpMap.putAll(new NewMapUtil().set("ip", ip)
					                      .set("port", port)
					                      .set("uname", uname)
					                      .set("pass", pass)
					                      .get());
			return true;
		} catch (Exception ex) {
			logger.error(Exceptions.getStackTraceAsString(ex));
			return false;
		}
	}
	
	/**
	 * Ftp 服务掉线重连
	 * @param ftpMap
	 * @return
	 */
	private boolean reLogin(Map<String, Object> ftpMap) {
		if (null == ftpMap || ftpMap.isEmpty()) {
			logger.info("ftp：登录失败 ; ftp map is null or Empty");
			return false;
		}
		if (StringUtil.isNullOrEmpty(ftpMap.get("ip"))) {
			logger.info("ftp：登录失败 ; ftp ip  is null or Empty");
			return false;
		}
		if (StringUtil.isNullOrEmpty(ftpMap.get("port"))) {
			logger.info("ftp：登录失败 ; ftp port  is null or Empty");
			return false;
		}
		if (StringUtil.isNullOrEmpty(ftpMap.get("uname"))) {
			logger.info("ftp：登录失败 ; ftp user name  is null or Empty");
			return false;
		}
		//关闭上传连接
		this.close();

		String ip    = String.valueOf(ftpMap.get("ip"));
		int port     = NumberUtils.intValue(ftpMap.get("port"), 21);
		String uname = String.valueOf(ftpMap.get("uname"));
		String pass  = String.valueOf(ftpMap.get("pass"));
		if (uname.equalsIgnoreCase("anonymous")) {
			return this.login(ip, port);
		} else {
			return this.login(ip, port, uname, pass);
		}
	}

	/**
	 * 上传后触发
	 */
	public Function<FtpFileInfo, Boolean> onUploadFileAfter;

	/**
	 * 
	 * ftp上传文件
	 * 
	 * @param localFileName 待上传文件
	 * @param ftpDirName  ftp 目录名
	 * @param ftpFileName  ftp目标文件
	 * @return true||false
	 */
	public boolean uploadFile(String localFileName, String ftpDirName, String ftpFileName) {
		return uploadFile(localFileName, ftpDirName, ftpFileName, false);
	}

	/**
	 * 
	 * ftp上传文件
	 * 
	 * @param localFileName  待上传文件
	 * @param ftpDirName  ftp 目录名
	 * @param ftpFileName ftp 目标文件
	 * @param deleteLocalFile 是否删除本地文件
	 * @return true||false
	 */
	public boolean uploadFile(String localFileName, String ftpDirName, String ftpFileName, boolean deleteLocalFile) {
		logger.debug("准备上传 [" + localFileName + "] 到 ftp://" + ftpDirName + "/" + ftpFileName);

		if (!this.isConnected()) {
			boolean _login = this.reLogin(ftpMap);
			if (!_login) {
				throw new RuntimeException("Ftp 服务重连失败！");
			}
		}
		File srcFile = new File(localFileName);
		if (!srcFile.exists()) {
			throw new RuntimeException("文件不存在：" + localFileName);
		}

		try (FileInputStream fis = new FileInputStream(srcFile)) {
			// 上传文件
			boolean flag = uploadFile(fis, ftpDirName, ftpFileName, 0);
			// 上传前事件
			if (onUploadFileAfter != null) {
				onUploadFileAfter.apply(new FtpFileInfo(localFileName, ftpDirName, ftpFileName));
			}
			// 删除文件
			if (deleteLocalFile) {
				srcFile.delete();
				logger.debug("ftp删除源文件：" + srcFile);
			}
			fis.close();
			return flag;
		} catch (Exception ex) {
			logger.error(Exceptions.getStackTraceAsString(ex));
			return false;
		}
	}

	/**
	 * 
	 * ftp上传文件 (使用inputstream)
	 * 
	 * @param localFileName 待上传文件
	 * @param ftpDirName ftp 目录名
	 * @param ftpFileName ftp目标文件
	 * @return true||false
	 */
	public boolean uploadFile(FileInputStream uploadInputStream, String ftpDirName, String ftpFileName, int cycle) {
		logger.debug("准备上传 [流] 到 ftp://"+ftpDirName+"/"+ftpFileName );
		try {
			if (!this.isConnected()) {
				boolean _login = this.reLogin(ftpMap);
				if (!_login) {
					throw new RuntimeException("Ftp 服务重连失败！");
				}
			}
			if (StringUtil.isNullOrEmpty(ftpFileName)){
				throw new RuntimeException("上传文件必须填写文件名！");
			}
			if (!createDir(ftpDirName)) {
				throw new RuntimeException("切入FTP目录失败：" + ftpDirName);
			}
			ftp.setBufferSize(1024);
			// 解决上传中文 txt 文件乱码
			ftp.setControlEncoding("GBK");
			FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
			conf.setServerLanguageCode("zh");

			// 设置文件类型（二进制）
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			// 上传
			String fileName = new String(ftpFileName.getBytes("GBK"), "iso-8859-1");
			// 每次数据连接之前，ftp client 通知ftp server开通一个端口来传输数据。
			ftp.enterLocalPassiveMode();
			if (ftp.storeFile(fileName, uploadInputStream)) {
				uploadInputStream.close();
				logger.debug("文件上传成功："+ftpDirName+"/"+ftpFileName);
				return true;
			}
			return false;
		} catch (Exception ex) {
			logger.error(Exceptions.getStackTraceAsString(ex));
			cycle++;
			if (cycle < 3) {
				this.reLogin(ftpMap);
				return this.uploadFile(uploadInputStream, ftpDirName, ftpFileName, cycle);
			}
			return false;
		}
	}
	
	/**
	 * 下载文件(使用HttpServletResponse)
	 * 
	 * @param remoteremoteAdr 远程路径
	 * @param localAdr        文件名称
	 * @return
	 */
	public void downloadFile(String remoteremoteAdr, String localAdr, HttpServletResponse response) {
		try {
			if (StringUtil.isNullOrEmpty(remoteremoteAdr)) {
				remoteremoteAdr = "/";
			}
			String dir = new String(remoteremoteAdr.getBytes("GBK"), "iso-8859-1");
			if (!ftp.changeWorkingDirectory(dir)) {
				System.out.println("切换目录失败：" + remoteremoteAdr);
				return;
			}
			ftp.enterLocalPassiveMode();
			// 由于apache不支持中文语言环境，通过定制类解析中文日期类型
			ftp.configure(new FTPClientConfig("com.zznode.tnms.ra.c11n.nj.resource.ftp.UnixFTPEntryParser"));
			FTPFile[] fs = ftp.listFiles();
			String fileName = new String(localAdr.getBytes("GBK"), "iso-8859-1");
			for (FTPFile ff : fs) {
				if (ff.getName().equals(fileName)) {
					// 这个就就是弹出下载对话框的关键代码
					response.setHeader("Content-Disposition", "attachment;fileName=" + new String(ff.getName().getBytes("gb2312"), "ISO8859-1"));
					response.setContentType("application/octet-stream; charset=utf-8");
					// 将文件保存到输出流outputStream中
					OutputStream os = response.getOutputStream();
					ftp.retrieveFile(new String(ff.getName().getBytes("UTF-8"), "ISO8859-1"), os);
					os.flush();
					os.close();
					break;
				}
			}
		} catch (Exception ex) {
			logger.error(Exceptions.getStackTraceAsString(ex));
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param ftpDirName ftp目录名
	 * @param ftpFileName  ftp文件名
	 * @param localFileFullName 本地文件名
	 * @return
	 */
	public boolean downloadFile(String ftpDirName, String ftpFileName, String localFileFullName) {
		try {
			if (StringUtil.isNullOrEmpty(ftpDirName)){
				ftpDirName = "/";
			}
			String dir = new String(ftpDirName.getBytes("GBK"), "iso-8859-1");
			if (!ftp.changeWorkingDirectory(dir)) {
				System.out.println("切换目录失败：" + ftpDirName);
				return false;
			}
			FTPFile[] fs = ftp.listFiles();
			String fileName = new String(ftpFileName.getBytes("GBK"), "iso-8859-1");
			for (FTPFile ff : fs) {
				if (ff.getName().equals(fileName)) {
					FileOutputStream is = new FileOutputStream(new File(localFileFullName));
					ftp.retrieveFile(ff.getName(), is);
					is.close();
					System.out.println("下载ftp文件已下载：" + localFileFullName);
					return true;
				}
			}
			System.out.println("下载ftp文件失败：" + ftpFileName + ";目录：" + ftpDirName);
			return false;
		} catch (Exception ex) {
			logger.error(Exceptions.getStackTraceAsString(ex));
			return false;
		}
	}

	/**
	 * 
	 * 删除ftp上的文件
	 * 
	 * @param ftpFileName
	 * @return true || false
	 */
	public boolean removeFile(String ftpFileName) {
		boolean flag = false;
		logger.debug("待删除文件："+ ftpFileName);
		try {
			ftpFileName = new String(ftpFileName.getBytes("GBK"), "iso-8859-1");
			flag = ftp.deleteFile(ftpFileName);
			logger.debug("删除文件：["+(flag ? "成功" : "失败")+"]");
			return flag;
		} catch (IOException ex) {
			logger.error(Exceptions.getStackTraceAsString(ex));
			return false;
		}
	}

	/**
	 * 删除空目录
	 * 
	 * @param dir
	 * @return
	 */
	public boolean removeDir(String dir) {
		if (dir.startsWith("/"))
			dir = "/" + dir;
		try {
			String d = new String(dir.toString().getBytes("GBK"), "iso-8859-1");
			return ftp.removeDirectory(d);
		} catch (Exception ex) {
			logger.error(Exceptions.getStackTraceAsString(ex));
			return false;
		}
	}

	/**
	 * 创建目录(有则切换目录，没有则创建目录)
	 * 
	 * @param dir
	 * @return
	 */
	public boolean createDir(String dir) throws Exception {
		if (StringUtil.isNullOrEmpty(dir)) {
			return true;
		}
		// 目录编码，解决中文路径问题
		String d = new String(dir.toString().getBytes("GBK"), "iso-8859-1");
		// 尝试切入目录
		if (ftp.changeWorkingDirectory(d)) {
			return true;
		}
		dir = StringExtend.trimStart(dir, "/");
		dir = StringExtend.trimEnd(dir, "/");
		String[] arr = dir.split("/");
		StringBuffer sbfDir = new StringBuffer();
		// 循环生成子目录
		for (String s : arr) {
			sbfDir.append("/");
			sbfDir.append(s);
			// 目录编码，解决中文路径问题
			d = new String(sbfDir.toString().getBytes("GBK"), "iso-8859-1");
			// 尝试切入目录
			if (ftp.changeWorkingDirectory(d)) {
				continue;
			}
			if (!ftp.makeDirectory(d)) {
				System.out.println("[失败]ftp创建目录：" + sbfDir.toString());
				return false;
			}
			System.out.println("[成功]创建ftp目录：" + sbfDir.toString());
		}
		// 将目录切换至指定路径
		return ftp.changeWorkingDirectory(d);
	}

	/**
	 *
	 * 销毁ftp连接
	 *
	 */
	private void closeFtpConnection() {
		_isLogin = false;
		if (ftp != null) {
			if (ftp.isConnected()) {
				try {
					ftp.logout();
					ftp.disconnect();
					logger.info("关闭 ftp 客户端 连接成功！");
				} catch (IOException ex) {
					logger.error(Exceptions.getStackTraceAsString(ex));
				}
			}
		}
	}

	/**
	 *
	 * 销毁ftp连接
	 *
	 */
	@Override
	public void close() {
		this.closeFtpConnection();
	}
	
	/**
	 *
	 * 销毁ftp连接
	 *
	 */
	public boolean isConnected() {
		if (ftp != null) {
			if (ftp.isConnected()) {
				return true;
			}
		}
		return false;
	}
	

	public static class FtpFileInfo {
		public FtpFileInfo(String srcFile, String ftpDirName, String ftpFileName) {
			this.ftpDirName = ftpDirName;
			this.ftpFileName = ftpFileName;
			this.srcFile = srcFile;
		}

		String srcFile;
		String ftpDirName;
		String ftpFileName;
		String ftpFileFullName;

		public String getSrcFile() {
			return srcFile;
		}

		public void setSrcFile(String srcFile) {
			this.srcFile = srcFile;
		}

		public String getFtpDirName() {
			return ftpDirName;
		}

		public void setFtpDirName(String ftpDirName) {
			this.ftpDirName = ftpDirName;
		}

		public String getFtpFileName() {
			return ftpFileName;
		}

		public void setFtpFileName(String ftpFileName) {
			this.ftpFileName = ftpFileName;
		}

		/**
		 * 获取ftp上传文件的完整路径名
		 * 
		 * @return
		 */
		public String getFtpFileFullName() {
			return PathExtend.Combine("/", ftpDirName, ftpFileName);
		}

	}
}