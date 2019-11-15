package com.tools.plugin.utils.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.sql.DataSource;

import com.tools.plugin.utils.StringUtil;


/**
 *
 *
 */
public class SqlUtils {
	private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("GMT+8");
	private static final int SCRIPT_HIGHEST_SPECIAL = '\\';
	private static char[][] scriptCharactersRepresentation = new char[SCRIPT_HIGHEST_SPECIAL + 1][];
	static {
		scriptCharactersRepresentation['\n'] = "\\n".toCharArray();
	    scriptCharactersRepresentation['\r'] = "\\r".toCharArray();
	    scriptCharactersRepresentation['"'] = "\\\"".toCharArray();
	    scriptCharactersRepresentation['\''] = "\\'".toCharArray();
	    scriptCharactersRepresentation['\\'] = "\\\\".toCharArray();
	}
	
	/**
	 * 依paramTypes拼装SQL Value值
	 * @param sql
	 * @param params
	 * @param paramTypes
	 * @throws SQLException
	 */
	public static void appendSqlValues(StringBuilder sql, Object[] params, int[] paramTypes) throws SQLException {
		if (params == null) {
			throw new SQLException("params not present.");
		}
		if (paramTypes == null) {
			throw new SQLException("paramTypes not present.");
		}
		if (params.length != paramTypes.length) {
			throw new SQLException("length of params and paramTypes not match.");
		}
		if (sql.indexOf("values") == -1 && sql.indexOf("VALUES") == -1) {
			sql.append(" VALUES ");
		} else {
			sql.append(','); // multi-values
		}
		sql.append('(');
		for (int i = 0; i < params.length; ++i) {
			if (i != 0) {
				sql.append(',');
			}
			SqlUtils.appendSqlValue(sql, params[i], paramTypes[i]);
		}
		sql.append(')');
	}

	public static void appendSqlValue(StringBuilder sql, Object param, int paramType) throws SQLException {
		if (param == null) {
			sql.append("NULL");
		} else {
			 if (paramType == Types.INTEGER) {
				 try{
				sql.append(((Number) param).intValue());
				 }catch(Exception ex ){
					 System.out.println(ex.getMessage()+"; param:"+param);
				 }
			} else if (paramType == Types.BIGINT) {
				sql.append(((Number) param).longValue());
			} else if (paramType == Types.FLOAT) {
				sql.append(((Number) param).floatValue());
			} else if (paramType == Types.REAL) {
				sql.append(((Number) param).doubleValue());
			} else if (paramType == Types.TINYINT) {
				sql.append(((Number) param).byteValue());
			} else if (paramType == Types.SMALLINT) {
				sql.append(((Number) param).shortValue());
			} else if (paramType == Types.VARCHAR) {
				sql.append('\'').append(escapeSqlTextValue(String.valueOf( param))).append('\'');
			} else if (paramType == Types.DATE || paramType == Types.TIME || paramType == Types.TIMESTAMP) {
				Calendar c = Calendar.getInstance();
				c.setTimeZone(DEFAULT_TIME_ZONE);
				c.setTimeInMillis(((Date)param).getTime());
				
				int year = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH) + 1;
				int day = c.get(Calendar.DAY_OF_MONTH);
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int minute = c.get(Calendar.MINUTE);
				int second = c.get(Calendar.SECOND);
				
				if (paramType == Types.DATE) {
					sql.append('\'').append(year).append('-')
									.append(month).append('-')
									.append(day)
									.append('\'');
				} else if (paramType == Types.TIME) {
					sql.append('\'').append(hour).append(':')
						.append(minute).append(':')
						.append(second)
						.append('\'');
				} else {
					sql.append('\'').append(year).append('-')
						.append(month).append('-')
						.append(day).append(' ')
						.append(hour).append(':')
						.append(minute).append(':')
						.append(second)
						.append('\'');
				}
			} else {
				throw new SQLException("data type not supported: " + param.getClass().getName());
			}
		}
	}
	
	/**
	 * 依参数值类型拼装SQl Value
	 * @param sql
	 * @param param
	 * @throws SQLException
	 */
	public static void appendSqlValue(StringBuilder sql, Object param) throws SQLException {
		if (param == null) {
			sql.append("NULL");
		} else {
			 if (param instanceof Integer) {
				sql.append(((Integer) param).intValue());
			} else if (param instanceof Long) {
				sql.append(((Long) param).longValue());
			} else if (param instanceof Float) {
				sql.append(((Float) param).floatValue());
			} else if (param instanceof Double) {
				sql.append(((Double) param).doubleValue());
			} else if (param instanceof Byte) {
				sql.append(((Byte) param).byteValue());
			} else if (param instanceof Short) {
				sql.append(((Short) param).shortValue());
			} else if (param instanceof String) {
				sql.append('\'').append(escapeSqlTextValue((String) param)).append('\'');
			} else if (param instanceof Date) {
				Calendar c = Calendar.getInstance();
				c.setTimeZone(DEFAULT_TIME_ZONE);
				c.setTimeInMillis(((Date)param).getTime());
				
				int year = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH) + 1;
				int day = c.get(Calendar.DAY_OF_MONTH);
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int minute = c.get(Calendar.MINUTE);
				int second = c.get(Calendar.SECOND);
				
				if (param instanceof java.sql.Date) {
					sql.append('\'').append(year).append('-')
									.append(month).append('-')
									.append(day)
									.append('\'');
				} else if (param instanceof Time) {
					sql.append('\'').append(hour).append(':')
						.append(minute).append(':')
						.append(second)
						.append('\'');
				} else {
					sql.append('\'').append(year).append('-')
						.append(month).append('-')
						.append(day).append(' ')
						.append(hour).append(':')
						.append(minute).append(':')
						.append(second)
						.append('\'');
				}
			} else if (param instanceof Calendar) {
				Calendar c = Calendar.getInstance();
				c.setTimeZone(DEFAULT_TIME_ZONE);
				c.setTimeInMillis(((Calendar)param).getTimeInMillis());
				
				int year = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH) + 1;
				int day = c.get(Calendar.DAY_OF_MONTH);
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int minute = c.get(Calendar.MINUTE);
				int second = c.get(Calendar.SECOND);
				
				if (param instanceof java.sql.Date) {
					sql.append('\'').append(year).append('-')
									.append(month).append('-')
									.append(day)
									.append('\'');
				} else if (param instanceof Time) {
					sql.append('\'').append(hour).append(':')
						.append(minute).append(':')
						.append(second)
						.append('\'');
				} else {
					sql.append('\'').append(year).append('-')
						.append(month).append('-')
						.append(day).append(' ')
						.append(hour).append(':')
						.append(minute).append(':')
						.append(second)
						.append('\'');
				}
			} else {
				throw new SQLException("data type not supported: " + param.getClass().getName());
			}
		}
	}
	
	public static String escapeSqlTextValue(String s) {
    	if(s == null)
    		return "";
    	
        int start = 0;
        int length = s.length();
        char[] arrayBuffer = s.toCharArray();
        StringBuilder escapedBuffer = null;

        for (int i = 0; i < length; i++) {
            char c = arrayBuffer[i];
            if (c <= SCRIPT_HIGHEST_SPECIAL) {
                char[] escaped = scriptCharactersRepresentation[c];
                if (escaped != null) {
                    // create StringBuilder to hold escaped xml string
                    if (start == 0) {
                        escapedBuffer = new StringBuilder(length + 5);
                    }
                    // add unescaped portion
                    if (start < i) {
                        escapedBuffer.append(arrayBuffer,start,i-start);
                    }
                    start = i + 1;
                    // add escaped xml
                    escapedBuffer.append(escaped);
                }
            }
        }
        // no xml escaping was necessary
        if (start == 0) {
            return s;
        }
        // add rest of unescaped portion
        if (start < length) {
            escapedBuffer.append(arrayBuffer,start,length-start);
        }
        return escapedBuffer.toString();
    }
	
	/**
	 * SQL查询返回List集合
	 * @param dataSource
	 * @param sqlStr
	 * @param name
	 * @return
	 * @throws SQLException 
	 */
	public static List<Map> querySqlList(DataSource dataSource, String sqlStr, String name) throws SQLException {
		Connection connection = null;
		List<Map> list = null;
		try {
			connection = dataSource.getConnection();
			if (null != connection) {
				Statement pstmt = connection.createStatement();
				if (!StringUtil.isNullOrEmpty(sqlStr)) {
					ResultSet rs = pstmt.executeQuery(sqlStr);
					list = resultSetToList(rs);
				} else {
					throw new SQLException("Statement do executeQuery ; sqlStr is null");
				}
				pstmt.close();
			} else {
				throw new SQLException("dataSource get connection is null");
			}
		} finally {
			if (null != connection) {
				connection.close();
			}
		}
		return list;
	}
	
	/**
	 * ResultSet转换为List集合
	 * @param rs
	 * @return
	 * @throws java.sql.SQLException
	 */
	public static List<Map> resultSetToList(ResultSet rs) throws java.sql.SQLException {
		if (rs == null) {
			return Collections.emptyList();
		}
		ResultSetMetaData md = rs.getMetaData(); // 得到结果集(rs)的结构信息，比如字段数、字段名等
		int columnCount = md.getColumnCount(); // 返回此 ResultSet 对象中的列数
		if (columnCount == 0) {
			return Collections.emptyList();
		}
		List<Map> list = new ArrayList<Map>();
		LinkedHashMap<String, Object> rowData = new LinkedHashMap<>();
		while (rs.next()) {
			rowData = new LinkedHashMap<>(columnCount);
			for (int i = 1; i <= columnCount; i++) {
				rowData.put(md.getColumnName(i), rs.getObject(i));
			}
			list.add(rowData);
		}
		return list;
	}
	
	/**
	 * 执行SQl 增，删除，改
	 * @param dataSource
	 * @param sqlStr
	 * @param name
	 * @throws SQLException 
	 */
	public static void sqlExecute(DataSource dataSource, String sqlStr, String name) throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			if (null != connection) {
				Statement pstmt = connection.createStatement();
				if (!StringUtil.isNullOrEmpty(sqlStr)) {
					pstmt.executeUpdate(sqlStr);
				} else {
					throw new SQLException("Statement do executeQuery ; sqlStr is null");
				}
				pstmt.close();
			} else {
				throw new SQLException("dataSource get connection is null");
			}
		} finally {
			if (null != connection) {
				connection.close();
			}
		}
	}
}
