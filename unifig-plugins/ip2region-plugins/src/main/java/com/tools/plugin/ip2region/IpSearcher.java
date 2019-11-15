package com.tools.plugin.ip2region;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.tools.plugin.ip2region.bean.IPInformation;
import com.tools.plugin.utils.FilesUtil;
import com.tools.plugin.utils.StringUtil;

public class IpSearcher {

	private static final Logger logger = LoggerFactory.getLogger(IpSearcher.class);

	private static final String NAME = "config.properties";
	private static final String DEFAULT_DB_PATH = "data/ip2region.db";
	private static final String DEFAULT_USE_ALGO = "memory";
	private static Map<String, Long> lastFile = new HashMap<String, Long>();

	private static BufferedReader reader;
	private static DbSearcher searcher;

	public IpSearcher() {

	}

	private static Properties getProperties() throws IOException {
		Properties props = new Properties();
		try {
			InputStream ips = IpSearcher.class.getClassLoader().getResourceAsStream(NAME);
			BufferedReader ipss = new BufferedReader(new InputStreamReader(ips));
			props.load(ipss);
			return props;
		} catch (IOException e) {
			logger.error("读properties文件出错", e);
			throw e;
		}
	}

	public void dbFileCopy() {

	}

	/**
	 * 从JAR中复制文件到磁盘
	 * @param srcFilePath：源路径，既JAR包中的资源文件，路径相对于CLASSPATH
	 * @return int：返回执行后的状态；0：失败；1：成功；（可以扩充其它状态）
	 */
	private static int fileCopy(String srcFilePath, File destFile) throws IOException {
		int flag = 0;
		try {
//			BufferedInputStream fis = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(srcFilePath));
			BufferedInputStream fis = new BufferedInputStream(IpSearcher.class.getClassLoader().getResourceAsStream(srcFilePath));
			FileOutputStream fos = new FileOutputStream(destFile);
			byte[] buf = new byte[1024];
			int c = 0;
			while ((c = fis.read(buf)) != -1) {
				fos.write(buf, 0, c);
			}
			fis.close();
			fos.close();
			flag = 1;
		} catch (IOException e) {
			throw e;
		}
		return flag;
	}

	private static String getDataFilePath() throws IOException {
		String wildcardPath = DEFAULT_DB_PATH;
		Properties properties = getProperties();
		if (null != properties && properties.isEmpty()) {
			wildcardPath = properties.getProperty("ip2region.path");
		}
		String tmpPath = System.getProperty("java.io.tmpdir");
		String pid = getPid();
		File destFile = new File(tmpPath, "ip-" + pid + "-ip2region.db");
		if (null == lastFile.get(pid)) {
			FilesUtil.deleteFiles(tmpPath, "-ip2region.db");
			if (fileCopy(wildcardPath, destFile) > 0) {
				lastFile.put(pid, new Date().getTime());
				return destFile.getPath();
			}
		} else {
			return destFile.getPath();
		}
		return null;
	}

	private static String getPid() {
		final Logger sysout = LoggerFactory.getLogger("System.out");
		final Logger syserr = LoggerFactory.getLogger("System.err");
		String pid = null;
		String runtimeName = ManagementFactory.getRuntimeMXBean().getName(); // runtime
																				// name
																				// format:
																				// pid@hostname
		if (runtimeName == null || runtimeName.length() == 0) {
			sysout.error("cannot pid by MX: ManagementFactory.getRuntimeMXBean().getName() returns null");
		} else {
			int delimiter = runtimeName.indexOf('@');
			if (delimiter == -1) {
				sysout.error("cannot pid by MX: ManagementFactory.getRuntimeMXBean().getName(): {}", runtimeName);
			} else {
				pid = runtimeName.substring(0, delimiter);
			}
		}

		if (pid == null) {
			String osName = System.getProperty("os.name");
			if (osName != null) {
				osName = osName.toLowerCase();
				if ("linux".equals(osName) || "unix".equals("osName")) {
					InputStream in = null;
					try {
						Process p = Runtime.getRuntime().exec(new String[] { "bash", "-c", "echo $PPID" });
						byte[] bbuf = new byte[100];
						in = p.getInputStream();
						int length = p.getInputStream().read(bbuf);
						pid = new String(bbuf, 0, length);
					} catch (IOException ex) {
						syserr.error("IO exception while exec 'echo $PPID': " + ex.getMessage(), ex);
					} finally {
						if (in != null) {
							try {
								in.close();
							} catch (IOException ex) {
								syserr.error("IO exception while close stream for cmd 'echo $PPID': " + ex.getMessage(), ex);
							}
						}
					}
				}
			}
		}

		return pid;
	}

	static {

		try {
			searcher = new DbSearcher(new DbConfig(), getDataFilePath());
			reader = new BufferedReader(new InputStreamReader(System.in));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("加载出错", e);
		}


	}

	public static IPInformation getIpInfo(String ipAddr) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException {
		String algo = DEFAULT_USE_ALGO;
		Properties properties = getProperties();
		if (null != properties && properties.isEmpty()) {
			algo = properties.getProperty("ip2region.algo");
		}
		int algorithm = DbSearcher.BTREE_ALGORITHM;
		switch (algo) {
		case "btree":
			algorithm = DbSearcher.BTREE_ALGORITHM;
			break;
		case "binary":
			algorithm = DbSearcher.BINARY_ALGORITHM;
			break;
		case "memory":
			algorithm = DbSearcher.MEMORY_ALGORITYM;
			break;
		}
		try {
			// define the method
			Method method = null;
			switch (algorithm) {
			case DbSearcher.BTREE_ALGORITHM:
				method = searcher.getClass().getMethod("btreeSearch", String.class);
				break;
			case DbSearcher.BINARY_ALGORITHM:
				method = searcher.getClass().getMethod("binarySearch", String.class);
				break;
			case DbSearcher.MEMORY_ALGORITYM:
				method = searcher.getClass().getMethod("memorySearch", String.class);
				break;
			}
			DataBlock dataBlock = (DataBlock) method.invoke(searcher, ipAddr);

			return getIPInformation(dataBlock);
		}  catch (Exception e) {
			throw e;
		}
//		return null;
	}

	private static IPInformation getIPInformation(DataBlock dataBlock) {
		if (null != dataBlock) {
			String regionInfo = dataBlock.getRegion();
			if (StringUtil.isNullOrEmpty(regionInfo) || regionInfo.startsWith("未分配或者内网IP|")) {
				IPInformation.Location loc = new IPInformation.Location();
				IPInformation.ISP isp = new IPInformation.ISP();
				loc = new IPInformation.Location(null, "N/A", 0, "N/A", 0, "N/A");
				isp = new IPInformation.ISP(0, "N/A");
				IPInformation ipInformation = new IPInformation();
				ipInformation.setLocation(loc);
				ipInformation.setIsp(isp);
				return ipInformation;
			} else {
				String[] regionArr = regionInfo.split("\\|");
				if (regionArr.length > 4) {
					IPInformation.Location loc = new IPInformation.Location();
					IPInformation.ISP isp = new IPInformation.ISP();
					loc = new IPInformation.Location(null, regionArr[0], 0, regionArr[2], 0, regionArr[3]);
					isp = new IPInformation.ISP(0, regionArr[4]);
					IPInformation ipInformation = new IPInformation();
					ipInformation.setLocation(loc);
					ipInformation.setIsp(isp);
					return ipInformation;
				}
			}
		}
		return null;
	}

	public static void main(String arges[]) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException {
		long a1 = System.currentTimeMillis();
		System.out.println(JSON.toJSONString(IpSearcher.getIpInfo("182.150.24.10")));
		long a2 = System.currentTimeMillis();
		System.out.println(JSON.toJSONString(IpSearcher.getIpInfo("182.150.24.10")));
		long a3 = System.currentTimeMillis();
		System.out.println(JSON.toJSONString(IpSearcher.getIpInfo("103.77.56.156")));
		long a4 = System.currentTimeMillis();
		System.out.println(a2-a1);
		System.out.println(a3-a2);
		System.out.println(a4-a3);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		reader.close();
		searcher.close();
	}
}
