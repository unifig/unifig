package com.unifig.organ.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HbaseDemo {

	private static Admin admin;

	public static void main(String[] args){
		try {
			createTable("maxl", new String[] { "information", "contact" });
			User user = new User("001", "xiaoming", "123456", "man", "20", "13355550021", "1232821@csdn.com");
			insertData("maxl", user);
			User user2 = new User("002", "xiaohong", "654321", "female", "18", "18757912212", "214214@csdn.com");
			insertData("maxl", user2);
			List<User> list = getAllData("maxl");
			System.out.println("--------------------插入两条数据后--------------------");
			for (User user3 : list){
				System.out.println(user3.toString());
			}
			System.out.println("--------------------获取原始数据-----------------------");
			getNoDealData("user_table");
			System.out.println("--------------------根据rowKey查询--------------------");
			User user4 = getDataByRowKey("user_table", "user-001");
			System.out.println(user4.toString());
			System.out.println("--------------------获取指定单条数据-------------------");
			String user_phone = getCellData("user_table", "user-001", "contact", "phone");
			System.out.println(user_phone);
			User user5 = new User("test-003", "xiaoguang", "789012", "man", "22", "12312132214", "856832@csdn.com");
			insertData("user_table", user5);
			List<User> list2 = getAllData("user_table");
			System.out.println("--------------------插入测试数据后--------------------");
			for (User user6 : list2){
				System.out.println(user6.toString());
			}
			//deleteByRowKey("user_table", "user-test-003");
			List<User> list3 = getAllData("user_table");
			System.out.println("--------------------删除测试数据后--------------------");
			for (User user7 : list3){
				System.out.println(user7.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//连接集群
	public static Connection initHbase() throws IOException {

		Configuration configuration = HBaseConfiguration.create();
		configuration.set("hbase.zookeeper.property.clientPort", "2181");
		configuration.set("hbase.zookeeper.quorum", "localhost:2181");
		//集群配置↓
		//configuration.set("hbase.zookeeper.quorum", "101.236.39.141,101.236.46.114,101.236.46.113");
		configuration.set("hbase.master", "localhost:16010");
		Connection connection = ConnectionFactory.createConnection(configuration);
		return connection;
	}

	//创建表
	public static void createTable(String tableNmae, String[] cols) throws IOException {

		TableName tableName = TableName.valueOf(tableNmae);
		admin = initHbase().getAdmin();

			HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
			for (String col : cols) {
				HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(col);
				hTableDescriptor.addFamily(hColumnDescriptor);
			}
			admin.createTable(hTableDescriptor);
	}

	//插入数据
	public static void insertData(String tableName, User user) throws IOException {
		TableName tablename = TableName.valueOf(tableName);
		Put put = new Put(("user-" + user.getId()).getBytes());
		//参数：1.列族名  2.列名  3.值
		put.addColumn("information".getBytes(), "username".getBytes(), user.getUsername().getBytes()) ;
		put.addColumn("information".getBytes(), "age".getBytes(), user.getAge().getBytes()) ;
		put.addColumn("information".getBytes(), "gender".getBytes(), user.getGender().getBytes()) ;
		put.addColumn("contact".getBytes(), "phone".getBytes(), user.getPhone().getBytes());
		put.addColumn("contact".getBytes(), "email".getBytes(), user.getEmail().getBytes());
		//HTable table = new HTable(initHbase().getConfiguration(),tablename);已弃用
		Table table = initHbase().getTable(tablename);
		table.put(put);
	}

	//获取原始数据
	public static void getNoDealData(String tableName){
		try {
			Table table= initHbase().getTable(TableName.valueOf(tableName));
			Scan scan = new Scan();
			ResultScanner resutScanner = table.getScanner(scan);
			for(Result result: resutScanner){
				System.out.println("scan:  " + result);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//根据rowKey进行查询
	public static User getDataByRowKey(String tableName, String rowKey) throws IOException {

		Table table = initHbase().getTable(TableName.valueOf(tableName));
		Get get = new Get(rowKey.getBytes());
		User user = new User();
		user.setId(rowKey);
		//先判断是否有此条数据
		if(!get.isCheckExistenceOnly()){
			Result result = table.get(get);
			for (Cell cell : result.rawCells()){
				String colName = Bytes.toString(cell.getQualifierArray(),cell.getQualifierOffset(),cell.getQualifierLength());
				String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
				if(colName.equals("username")){
					user.setUsername(value);
				}
				if(colName.equals("age")){
					user.setAge(value);
				}
				if (colName.equals("gender")){
					user.setGender(value);
				}
				if (colName.equals("phone")){
					user.setPhone(value);
				}
				if (colName.equals("email")){
					user.setEmail(value);
				}
			}
		}
		return user;
	}

	//查询指定单cell内容
	public static String getCellData(String tableName, String rowKey, String family, String col){

		try {
			Table table = initHbase().getTable(TableName.valueOf(tableName));
			String result = null;
			Get get = new Get(rowKey.getBytes());
			if(!get.isCheckExistenceOnly()){
				get.addColumn(Bytes.toBytes(family),Bytes.toBytes(col));
				Result res = table.get(get);
				byte[] resByte = res.getValue(Bytes.toBytes(family), Bytes.toBytes(col));
				return result = Bytes.toString(resByte);
			}else{
				return result = "查询结果不存在";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "出现异常";
	}

	//查询指定表名中所有的数据
	public static List<User> getAllData(String tableName){

		Table table = null;
		List<User> list = new ArrayList<User>();
		try {
			table = initHbase().getTable(TableName.valueOf(tableName));
			ResultScanner results = table.getScanner(new Scan());
			User user = null;
			for (Result result : results){
				String id = new String(result.getRow());
				System.out.println("用户名:" + new String(result.getRow()));
				user = new User();
				for(Cell cell : result.rawCells()){
					String row = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
					//String family =  Bytes.toString(cell.getFamilyArray(),cell.getFamilyOffset(),cell.getFamilyLength());
					String colName = Bytes.toString(cell.getQualifierArray(),cell.getQualifierOffset(),cell.getQualifierLength());
					String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
					user.setId(row);
					if(colName.equals("username")){
						user.setUsername(value);
					}
					if(colName.equals("age")){
						user.setAge(value);
					}
					if (colName.equals("gender")){
						user.setGender(value);
					}
					if (colName.equals("phone")){
						user.setPhone(value);
					}
					if (colName.equals("email")){
						user.setEmail(value);
					}
				}
				list.add(user);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	//删除指定cell数据
	public static void deleteByRowKey(String tableName, String rowKey) throws IOException {

		Table table = initHbase().getTable(TableName.valueOf(tableName));
		Delete delete = new Delete(Bytes.toBytes(rowKey));
		//删除指定列
		//delete.addColumns(Bytes.toBytes("contact"), Bytes.toBytes("email"));
		table.delete(delete);
	}

	//删除表
	public static void deleteTable(String tableName){

		try {
			TableName tablename = TableName.valueOf(tableName);
			admin = initHbase().getAdmin();
			admin.disableTable(tablename);
			admin.deleteTable(tablename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}