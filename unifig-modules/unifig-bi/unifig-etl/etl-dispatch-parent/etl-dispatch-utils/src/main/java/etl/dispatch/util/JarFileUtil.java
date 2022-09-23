package etl.dispatch.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Jar文件工具
 */
public class JarFileUtil {

	/**
	 * 解压Jar文件
	 * @param jarFile 指定jar文件
	 * @param tarDir 指定解压文件夹
	 * @throws IOException 抛出异常
	 */
	public static void uncompress(File jarFile, File tarDir) throws IOException {
		JarFile jfInst = new JarFile(jarFile);
		Enumeration<JarEntry> enumEntry = jfInst.entries();
		while (enumEntry.hasMoreElements()) {
			JarEntry jarEntry = enumEntry.nextElement();
			// 构造解压文件实体
			File tarFile = new File(tarDir, jarEntry.getName());
    if (!tarFile.toPath().normalize().startsWith(tarDir.toPath().normalize())) {
      throw new IOException("Bad zip entry");
    }
			// 创建文件
			makeFile(jarEntry, tarFile);
			if (jarEntry.isDirectory()) {
				continue;
			}
			// 构造输出流
			FileChannel fileChannel = new FileOutputStream(tarFile).getChannel();
			// 取输入流
			InputStream ins = jfInst.getInputStream(jarEntry);
			transferStream(ins, fileChannel);
		}
	}

	/**
	 * 流交换
	 * 
	 * @param ins 输入流
	 * @param targetChannel 输出流
	 */
	private static void transferStream(InputStream ins, FileChannel targetChannel) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 10);
		ReadableByteChannel rbcInst = Channels.newChannel(ins);
		try {
			while (-1 != (rbcInst.read(byteBuffer))) {
				byteBuffer.flip();
				targetChannel.write(byteBuffer);
				byteBuffer.clear();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (null != rbcInst) {
				try {
					rbcInst.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != targetChannel) {
				try {
					targetChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 打印jar文件内容信息
	 * 
	 * @param fileInst jar文件
	 */
	public static void printJarEntry(File fileInst) {
		JarFile jfInst = null;
		try {
			jfInst = new JarFile(fileInst);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Enumeration<JarEntry> enumEntry = jfInst.entries();
		while (enumEntry.hasMoreElements()) {
			System.out.println((enumEntry.nextElement()));
		}
	}

	/**
	 * 创建文件
	 * 
	 * @param jarEntry jar实体
	 * @param fileInst 文件实体
	 * @throws IOException 抛出异常
	 */
	public static void makeFile(JarEntry jarEntry, File fileInst) {
		if (!fileInst.exists()) {
			if (jarEntry.isDirectory()) {
				fileInst.mkdirs();
			} else {
				try {
					fileInst.createNewFile();
				} catch (IOException e) {
					System.out.println("创建文件失败>>".concat(fileInst.getPath()));
				}
			}
		}
	}

	/**
	 * 测试入口
	 * 
	 * @param args 参数列表
	 */
	public static void main(String[] args) {
		File jarFile = new File("D:\\DEV\\works\\HelloAnt\\build\\SmartPage-Web.jar");
		// JarFileUtil.printJarEntry();
		File targetDir = new File("E:\\test");
		try {
			JarFileUtil.uncompress(jarFile, targetDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
