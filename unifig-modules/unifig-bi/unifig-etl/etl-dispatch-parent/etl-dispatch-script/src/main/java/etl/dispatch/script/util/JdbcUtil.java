package etl.dispatch.script.util;

import java.sql.SQLException;

/**
 *
 *
 */
public class JdbcUtil {
	// 数据库致命错误（如数据库down机等）
	private final static int[] SQL_ERROR_CODES_HARD_ERROR = new int[] {
		
	};
	
	private final static String[] SQL_STATE_HARD_ERROR = new String[] {
		"08S01"	// mysql: Communications link failure
	};
	
	private final static String[] SQL_MESSAGE_HARD_ERROR = new String[] {
		"Communications link failure"	// mysql: Communications link failure
	};
	
	public static boolean isHardError(SQLException ex) {
		String exClassName = ex.getClass().getName();
		if("org.logicalcobwebs.proxool.FatalSQLException".equals(exClassName) ||
				"org.logicalcobwebs.proxool.FatalRuntimeException".equals(exClassName)) {
			// wrapped proxool fatal sql exception
			return true;
		}
		
		int errorCode = ex.getErrorCode();
		for(int i = 0; i < SQL_ERROR_CODES_HARD_ERROR.length; ++i) {
			if(SQL_ERROR_CODES_HARD_ERROR[i] == errorCode)
				return true;
		}
		
		String sqlState = ex.getSQLState();
		if(sqlState != null) {
			for(int i = 0; i < SQL_STATE_HARD_ERROR.length; ++i) {
				if(sqlState.equals(SQL_STATE_HARD_ERROR[i])) 
					return true;
			}
		}
		
		String message = ex.getMessage();
		if(message != null) {
			for(int i = 0; i < SQL_MESSAGE_HARD_ERROR.length; ++i) {
				if(message.contains(SQL_MESSAGE_HARD_ERROR[i])) 
					return true;
			}
		}
		
		return false;
	}
	
	public static boolean isDuplicateEntryError(SQLException ex) {
		if("com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException".equals(ex.getClass().getName())) {
			return true;
		}
		
		if(ex.getMessage() != null && ex.getMessage().startsWith("Duplicate entry")) {
			return true;
		}
		
		return false;
	}
}
