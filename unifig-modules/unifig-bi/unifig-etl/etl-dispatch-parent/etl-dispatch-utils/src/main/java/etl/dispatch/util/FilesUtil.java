package etl.dispatch.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 该类为文件帮助类，提供内容主要有跟据文件(夹)路径对文件(夹)的创建、删除等操作。<br/>
 * 
 *
 *
 */
public class FilesUtil {

	/**
	 * 通过文件目录进行创建文件。<br/> 详细描述:通过传入的文件路径创建文件。<br/> 使用方式:通过本类的类名直接调用该方法，并传入所需参数。<br/>
	 * 
	 * @param path 文件路径。<br/>
	 * @return true表示创建成功，false表示创建失败。<br/>
	 */
	public static boolean createFile(String path) {
		boolean isok = true;
		try {
			File file = new File(path);
			if ( !file.exists())// 文件不存在情况
			{
				isok = file.createNewFile();
			}
		} catch (IOException e) {
			System.out.println("创建文件出错！");
			isok = false;
			e.printStackTrace();
		}
		return isok;
	}

	/**
	 * 通过文件路径删除文件。<br/> 详细描述:通过传入的文件路径删除一个文件。<br/> 使用方式:通过本类的类名直接调用该方法，并传入所需参数。<br/>
	 * 
	 * @param path 文件路径。<br/>
	 * @return true表示删除成功，false表示删除失败。<br/>
	 */
	public static boolean deleteFile(String path) {
		File f = new File(path);
		if (f.exists() && f.isFile())
			return f.delete();
		else
			return true;
	}

	/**
	 * 通过文件夹路径删除该文件夹。<br/> 详细描述:通过传入的文件夹路径删除文件夹，先删除文件夹里面的所有文件，再删除空的文件夹。<br/> 使用方式:通过本类的类名直接调用该方法，并传入所需参数。<br/>
	 * 
	 * @param path 文件路径。<br/>
	 * @return true表示创建成功，false表示创建失败。<br/>
	 */
	public static boolean deleteFolder(String path) {
		boolean isok = true;
		try {
			deleteAllFiles(path);// 删除文件夹里的所有文件！
			File file = new File(path);
			if (file.exists()) {
				file.delete();// 删除空的文件夹
			}
		} catch (Exception e) {

			System.out.println("删除文件夹操作有误！");
			isok = false;
			e.printStackTrace();
		}
		return isok;
	}
	
	/**
	 * 判断路径文件夹是否存在
	 * @param path
	 * @return
	 */
	public static boolean isExistedDir(String path) {
		File file = new File(path);
		// 文件夹不存在
		if (!file.exists()) {
			return false;
		}
		// 不是文件夹
		if (!file.isDirectory()) {
			return false;
		}
		return true;
	}

	/**
	 * 通过文件夹路径删除该文件夹下的所有文件。<br/> 详细描述:通过传入的文件夹路径删除文件夹下的所有文件。<br/> 使用方式:通过本类的类名直接调用该方法，并传入所需参数。<br/>
	 * 
	 * @param path 文件路径。<br/>
	 */
	public static boolean deleteAllFiles(String path) {

		File file = new File(path);
		// 文件夹不存在
		if ( !file.exists()){
			return false;
		}
		// 不是文件夹
		if ( !file.isDirectory()){
			return false;
		}
		// tempList数组得到的只是该文件夹下所有的相对路径名！
		String[] tempList = file.list();
		File tempfile = null;
		for (int i = 0; i < tempList.length; i++ ) {
			if (path.endsWith(File.separator)) {
				tempfile = new File(path + tempList[i]);
			} else {
				tempfile = new File(path + File.separator + tempList[i]);
			}
			if (tempfile.isFile()) {
				tempfile.delete();
			}
			if (tempfile.isDirectory()) {
				deleteFolder(tempfile.getAbsolutePath());
			}
		}
		return true;
	}

	/**
	 * 将源文件路径下的文件拷贝到目标文件路径下。<br/> 详细描述:通过原文件路径的文件拷贝到目标文件路径下。<br/> 使用方式:通过本类的类名直接调用该方法，并传入所需参数。<br/>
	 * 
	 * @param srcPath 原文件路径。<br/>
	 * @param destPath 目标文件路径。<br/>
	 * @return true表示创建成功，false表示创建失败。<br/>
	 */
	public static boolean copyFile(String srcPath, String destPath) {
		boolean isok = true;
		try {
			int byteread = 0;
			File srcFile = new File(srcPath);
			if (srcFile.exists()) {
				InputStream inputStream = new FileInputStream(srcFile);
				OutputStream outputStream = new FileOutputStream(destPath);
				byte[] buffer = new byte[1024];
				while ( (byteread = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, byteread);
				}
				inputStream.close();
				outputStream.close();
			} else {
				isok = false;
				System.out.println("源文件不存在！");
			}
		} catch (Exception e) {
			isok = false;
			System.out.println("单个文件复制有误！");
			e.printStackTrace();
		}
		return isok;
	}

	/**
	 * 复制整个文件夹到目标文件夹下。<br/> 详细描述:复制整个文件夹，目标文件夹不存在，则创建。<br/> 使用方式:通过本类的类名直接调用该方法，并传入所需参数。<br/>
	 * 
	 * @param srcPath 原文件路径。<br/>
	 * @param destPath 目标文件路径。<br/>
	 * @return true表示创建成功，false表示创建失败。<br/>
	 */
	public static boolean copyAllFolder(String srcPath, String destPath) {
		boolean isok = true;
		try {
			new File(destPath).mkdirs();
			File file = new File(srcPath);
			String[] tempList = file.list();
			File tempfile = null;
			File tempNewFile = null;
			for (int i = 0; i < tempList.length; i++ ) {
				if (srcPath.endsWith(File.separator) && destPath.endsWith(File.separator)) {
					tempfile = new File(srcPath + tempList[i]);
					tempNewFile = new File(destPath + tempList[i]);
				} else {
					tempfile = new File(srcPath + File.separator + tempList[i]);
					tempNewFile = new File(srcPath + File.separator + tempList[i]);
				}
				if (tempfile.isFile()) {
					copyFile(tempfile.getAbsolutePath(), tempNewFile.getAbsolutePath());
				}
				if (tempfile.isDirectory()) {
					copyAllFolder(tempfile.getAbsolutePath() + File.separator, tempNewFile.getAbsolutePath()
																				+ File.separator);
				}
			}
		} catch (Exception e) {
			isok = false;
			System.out.println("文件夹复制出现问题！");
			e.printStackTrace();
		}
		return isok;
	}

	/**
	 * 创建文件目录。<br/> 详细描述:创建文件夹，如果父文件夹不存在，则会创建。<br/> 使用方式:通过本类的类名直接调用该方法，并传入所需参数。<br/>
	 * 
	 * @param path 路径。<br/>
	 * @return true表示创建成功，false表示创建失败。<br/>
	 */
	public static boolean createDirectory(String path) {
		boolean isok = true;
		File file = new File(path);
		// 不是为目录情况
		if ( !file.isDirectory()){
			isok = file.mkdirs();
		}
		return isok;
	}

	/**
	 * 移动文件到指定的目录下。<br/> 详细描述:移动文件到指定的目录下。<br/> 使用方式:通过本类的类名直接调用该方法，并传入所需参数。<br/>
	 * 
	 * @param oldPath 旧文件路径。<br/>
	 * @param newPath 新文件路径。<br/>
	 */
	public static void moveFile(String oldPath, String newPath) {
		copyFile(oldPath, newPath);
		deleteFile(oldPath);
	}

	/**
	 * 移动文件夹到指定的目录下。<br/> 详细描述:移动文件夹到指定的目录下。<br/> 使用方式:通过本类的类名直接调用该方法，并传入所需参数。<br/>
	 * 
	 * @param oldPath 旧文件路径。<br/>
	 * @param newPath 新文件路径。<br/>
	 */
	public static void moveFolder(String oldPath, String newPath) {
		copyAllFolder(oldPath, newPath);
		deleteFolder(oldPath);
	}

	/**
	 * 获取文件名的后缀名。<br/> 详细描述:得到文件名的后缀名，有包括小数点，如.txt。<br/> 使用方式:通过本类的类名直接调用该方法，并传入所需参数。<br/>
	 * 
	 * @param fileName 文件名称。<br/>
	 * @return true表示创建成功，false表示创建失败。<br/>
	 */
	public static String getPostfix(String fileName) {
		return getPostfix(fileName, true);
	}

	/**
	 * 获取文件名的后缀名。<br/> 详细描述:得到文件名的后缀名。<br/> 使用方式:通过本类的类名直接调用该方法，并传入所需参数。<br/>
	 * 
	 * @param fileName 文件名称。<br/>
	 * @param isDot 返回的格式是否包含'.',true表示包含,false表示不包含。<br/>
	 * @return true表示创建成功，false表示创建失败。<br/>
	 */
	public static String getPostfix(String fileName, boolean isDot) {
		int start = fileName.lastIndexOf(".");
		if (start == -1)// 如果文件没有后缀名，则返回空字符串
		{
			return "";
		}
		int length = fileName.length();
		String result = null;
		if (isDot) {
			result = fileName.substring(start, length);

		} else {
			result = fileName.substring(start + 1, length);
		}
		return result;
	}

	/**
	 * 读取文本文件内容。<br/> 详细描述:读取文本文件内容，以行的形式读取。<br/> 使用方式:通过本类的类名直接调用该方法，并传入所需参数。<br/>
	 * 
	 * @param filePath 带有完整绝对路径的文件名。<br/>
	 * @return String 返回文本文件的内容。<br/>
	 * @throws IOException IO流异常。<br/>
	 */
	public static String readFile(String filePath) throws IOException {
		return readFile(filePath, "", "", 1024);
	}

	/**
	 * 读取文本文件内容。<br/> 详细描述:读取文本文件内容，以行的形式读取。<br/> 使用方式:通过本类的类名直接调用该方法，并传入所需参数。<br/>
	 * 
	 * @param filePath 带有完整绝对路径的文件名。<br/>
	 * @param encoding 文本文件打开的编码方式 例如 GBK,UTF-8。<br/>
	 * @param sep 分隔符 例如：#，默认为\n。<br/>
	 * @param bufLen 设置缓冲区大小 。<br/>
	 * @return String 返回文本文件的内容。<br/>
	 */
	public static String readFile(String filePath, String encoding, String sep, int bufLen) throws IOException {
		if (filePath == null || filePath.equals("")) {
			return "";
		}
		if (sep == null || sep.equals("")) {
			sep = "\r\n";
		}
		StringBuffer buffer = new StringBuffer();
		FileInputStream fs = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			fs = new FileInputStream(filePath);
			if (encoding == null || encoding.trim().equals("")) {
				isr = new InputStreamReader(fs);
			} else {
				isr = new InputStreamReader(fs, encoding.trim());
			}
			br = new BufferedReader(isr, bufLen);

			String data = null;
			while ( (data = br.readLine()) != null) {
				buffer.append(data).append(sep);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (br != null)
					br.close();
				if (isr != null)
					isr.close();
				if (fs != null)
					fs.close();
			} catch (IOException e) {
				throw e;
			}
		}
		return buffer.toString();
	}

	/**
	 * 新建一个文件并写入内容。<br/> 详细描述:新建一个文件并写入内容,若存在文件,则覆盖原文件。<br/> 使用方式:通过本类的类名直接调用该方法，并传入所需参数。<br/>
	 * 
	 * @param filePath 文件路径。<br/>
	 * @param content 内容信息。<br/>
	 * @return true为写入成功，false为写入失败。<br/>
	 * @throws IOException IO流异常。<br/>
	 */
	public static boolean writeFile(String filePath, String content) throws IOException {
		return writeFile(filePath, content, "UTF-8", false);
	}

	/**
	 * 新建一个文件并写入内容。<br/> 详细描述:新建一个文件并写入内容。<br/> 使用方式:通过本类的类名直接调用该方法，并传入所需参数。<br/>
	 * 
	 * @param filePath 文件全路径。<br/>
	 * @param content 内容。<br/>
	 * @param encoding 编码。<br/>
	 * @param isAppendWrite 是否追加写入文件。<br/>
	 * @return true为写入成功，false为写入失败。<br/>
	 * @throws IOException IO流异常。<br/>
	 */
	public static boolean writeFile(String filePath, String content, String encoding, boolean isAppendWrite)
		throws IOException {

		if (filePath == null || filePath.equals("") || content == null || content.equals(""))
			return false;
		boolean flag = false;
		FileOutputStream fos = null;
		OutputStreamWriter out = null;

		try {
			fos = new FileOutputStream(filePath, isAppendWrite);
			out = new OutputStreamWriter(fos, encoding);
			out.write(content);
			out.flush();
			flag = true;
		} catch (IOException e) {
			flag = false;
			throw e;
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
			if (fos != null)
				fos.close();
		}
		return flag;
	}

	/**
	 * 读取文件夹下面所有文件。<br/> 详细描述:读取文件夹下面所有文件。<br/> 使用方式:通过本类的类名直接调用该方法，并传入所需参数。<br/>
	 * 
	 * @param path 文件(或文件夹)路径。<br/>
	 * @return 文件数组。<br/>
	 */
	public static File[] getFiles(String path) {
		return new File(path).listFiles();
	}

	/**
	 * 读取文件夹下面指定的文件。<br/> 详细描述:读取文件夹下面指定的文件。<br/> 使用方式:通过本类的类名直接调用该方法，并传入所需参数。<br/>
	 * 
	 * @param path 文件夹绝对路径。<br/>
	 * @param postfix 后缀名如:.jpg,.tif。<br/>
	 * @return 文件集合。<br/>
	 */
	public static List<File> getFiles(String path, String postfix) {
		List<File> list = new ArrayList<File>();
		// 读取本地文件夹的文件
		File[] files = new File(path).listFiles();
		String temp = null;// 后缀名
		String[] postfixs = postfix.split(",");
		for (File item : files) {
			// 如果是文件夹
			if (item.isDirectory()){
				continue;
			}
			// 得到文件的后缀名,且转为小写字母
			temp = getPostfix(item.getName(), true).toLowerCase();
			for (int i = 0; i < postfixs.length; i++ ) {
				if (temp.endsWith(postfixs[i])) {
					list.add(item);
					break;
				}
			}

		}
		return list;
	}

	/**
	 * 通过文件路径返回文件的大小。<br/> 
	 * 详细描述:通过文件路径返回文件的大小，单位为字节。<br/> 
	 * 使用方式:通过本类的类名直接调用该方法，并传入所需参数。<br/>
	 * @param filepath 文件夹绝对路径，其中包括文件的名称和后缀。<br/>
	 * @return 文件的大小。<br/>
	 */
	public static int getFileSize(String filepath) {
		try {
			File f = new File(filepath);
			if(f.exists()){
				FileInputStream fis = new FileInputStream(f);
				int size = fis.available();
				fis.close();
				return size;
			}
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}