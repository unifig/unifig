package com.unifig.files.mongo.service;

import com.unifig.files.mongo.domain.File;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: GridFSFileService
 * @Description:GridFSFile
 * @author: maxl
 * @date: 2018年8月10日 上午10:53:53
 */
@Service
public class GridFSFileService {

	@Autowired
	private MongoTemplate mongoTemplate;

	/**
	 * @param file                                      ：文件，File类型
	 * @param id                                        ：唯一标示文件，可根据id查询到文件.必须设置
	 * @param dbName                                    ：库名，每个系统使用一个库
	 * @param collectionName：集合名，如果传入的集合名库中没有，则会自动新建并保存
	 * @MethodName : uploadFile
	 * @Description : 上传文件
	 */
	public void uploadFile(File file, String id, String dbName, String collectionName) {
		// 把mongoDB的数据库地址配置在外部。
		try {
			// Mongo mongo =getMongo();
			// 每个系统用一个库
			DB db = mongoTemplate.getDb();
			System.out.println(db.toString());
			// 每个库中可以分子集
			GridFS gridFS = new GridFS(db, collectionName);

			// 创建gridfsfile文件
			GridFSFile gridFSFile = gridFS.createFile(file.getContent());
			// 判断是否已经存在文件，如果存在则先删除
			File gridFSDBFile = getFileById(id, dbName, collectionName);
			if (gridFSDBFile != null) {
				deleteFile(id, dbName, collectionName);
			}
			// 将文件属性设置到
			gridFSFile.put("_id", id);
			gridFSFile.put("name", file.getName());
			gridFSFile.put("filename", file.getName());
			gridFSFile.put("contentType", file.getContentType());
			gridFSFile.put("md5", file.getMd5());
			gridFSFile.put("size", file.getSize());
			// 保存上传
			gridFSFile.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param id：文件对应的id
	 * @param dbName：文件所在的库
	 * @param collectionName：文件所在的集合
	 * @MethodName : deleteFile
	 * @Description : 删除文件
	 */
	public void deleteFile(String id, String dbName, String collectionName) {

		try {
			// 获得mongoDB数据库连接。
			// Mongo mongo =getMongo();
			// 获得库
			DB db = mongoTemplate.getDb();
			// 获得子集
			GridFS gridFS = new GridFS(db, collectionName);
			// 删除文件
			DBObject query = new BasicDBObject("_id", id);
			gridFS.remove(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 批量删除文件
	 *
	 * @param ids
	 * @param dbName
	 * @param collectionName
	 * @MethodName : deleteFileByIds
	 * @Description : TODO
	 */
	public void deleteFileByIds(String[] ids, String dbName, String collectionName) {
		try {
			// 获得mongoDB数据库连接。
			// Mongo mongo =getMongo();
			// 获得库
			DB db = mongoTemplate.getDb();
			// 获得子集
			GridFS gridFS = new GridFS(db, collectionName);
			Map<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < ids.length; i++) {
				// 删除文件
				DBObject query = new BasicDBObject("_id", ids[i]);
				gridFS.remove(query);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param id                 ：文件Id
	 * @param dbName:            数据库名
	 * @param collectionName：集合名
	 * @return GridFSDBFile
	 * @MethodName : getFileById
	 * @Description : 根据Id获得文件
	 */
	public File getFileById(String id, String dbName, String collectionName) {
		File file = null;
		try {
			// 获得mongoDB数据库连接。
			// Mongo mongo =getMongo();
			// 获得库
			DB db = mongoTemplate.getDb();
			// 获得子集
			GridFS gridFS = new GridFS(db, collectionName);
			// 获得文件
			DBObject query = new BasicDBObject("_id", id);
			GridFSDBFile gridFSDBFile = gridFS.findOne(query);

			if (gridFSDBFile != null) {
				file = new File();
				file.setId(gridFSDBFile.getId().toString());
				file.setName(gridFSDBFile.getFilename());
				file.setSize((Long) gridFSDBFile.get("size"));
				file.setUploadDate(gridFSDBFile.getUploadDate());
				file.setContentType(gridFSDBFile.getContentType());
				file.setMd5(gridFSDBFile.getMD5());
				file.setContent(readStream(gridFSDBFile.getInputStream()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 返回数据
		return file;
	}

	/**
	 * 查询集合中所有文件
	 *
	 * @param dbName
	 * @param collectionName *
	 * @return
	 * @MethodName : getAllFile
	 * @Description : TODO
	 */
	public List<File> getAllFile(String dbName, String collectionName) {
		List<File> list = new ArrayList<File>();
		try {
			// 获得mongoDB数据库连接。
			// Mongo mongo =getMongo();
			// 获得库
			DB db = mongoTemplate.getDb();
			// 获得子集
			GridFS gridFS = new GridFS(db, collectionName);
			// 获得文件
			DBObject query = new BasicDBObject();// 空的构造
			List<GridFSDBFile> gridFSDBFileList = gridFS.find(query);
			for (GridFSDBFile file : gridFSDBFileList) {
				File f = new File();
				f.setId(file.getId().toString());
				f.setName(file.getFilename());
				f.setSize((Long) file.get("size"));
				f.setUploadDate(file.getUploadDate());
				f.setContentType(file.getContentType());
				f.setMd5(file.getMD5());
				// f.setContent(readStream(file.getInputStream()));
				list.add(f);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 返回数据
		return list;
	}

	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}

}
