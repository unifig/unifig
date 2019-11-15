package com.tools.plugin.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * 字符串工具类
 *
 *
 */
public class UncString {

	private static final char[] CHAR_SEQUENCE = { '0', '1', '2', '3', '4', '5', '6', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '_' };
	private static final Random RAND = new Random();
	public static final int HIGHEST_SPECIAL = 62;
	public static final int SCRIPT_HIGHEST_SPECIAL = 92;
	public static final int CSV_HIGHEST_SPECIAL = 44;
	public static final int HTML_HIGHEST_SPECIAL = 62;
	public static char[][] specialCharactersRepresentation = new char[63][];
	public static char[][] scriptCharactersRepresentation = new char[93][];
	public static char[][] htmlCharactersRepresentation = new char[63][];
	public static final char[] HTML_UNICODE_NEW_LINE1 = "&#x2028;".toCharArray();
	public static final char[] HTML_UNICODE_NEW_LINE2 = "&#x2029;".toCharArray();

	static {
		specialCharactersRepresentation[38] = "&amp;".toCharArray();
		specialCharactersRepresentation[60] = "&lt;".toCharArray();
		specialCharactersRepresentation[62] = "&gt;".toCharArray();
		specialCharactersRepresentation[34] = "&#034;".toCharArray();
		specialCharactersRepresentation[39] = "&#039;".toCharArray();

		scriptCharactersRepresentation[10] = "\\n".toCharArray();
		scriptCharactersRepresentation[13] = "\\r".toCharArray();
		scriptCharactersRepresentation[34] = "\\\"".toCharArray();
		scriptCharactersRepresentation[39] = "\\'".toCharArray();
		scriptCharactersRepresentation[92] = "\\\\".toCharArray();

		htmlCharactersRepresentation[38] = "&amp;".toCharArray();
		htmlCharactersRepresentation[60] = "&lt;".toCharArray();
		htmlCharactersRepresentation[62] = "&gt;".toCharArray();
		htmlCharactersRepresentation[34] = "&quot;".toCharArray();
		htmlCharactersRepresentation[32] = "&nbsp;".toCharArray();
		htmlCharactersRepresentation[10] = "<br/>".toCharArray();
		htmlCharactersRepresentation[13] = new char[0];
	}

	public static String toGB2312(String inStr) {
		String _tempStr = "";
		if (inStr == null) {
			_tempStr = "";
		} else {
			try {
				_tempStr = new String(inStr.getBytes("ISO-8859-1"), "GB2312");
			} catch (Exception e) {
				_tempStr = "";
			}
		}
		return _tempStr;
	}

	public static String replace(String inSource, String inOldStr, String inNewStr) {
		String _tempStr = "";

		int _pos = 0;
		int _temp = inOldStr.length();

		_pos = inSource.indexOf(inOldStr);
		while (_pos >= 0) {
			_tempStr = _tempStr + inSource.substring(0, _pos) + inNewStr;

			inSource = inSource.substring(_pos + _temp);

			_pos = inSource.indexOf(inOldStr);
		}
		return _tempStr + inSource;
	}

	public static String replaceIgnoreCase(String inSource, String inOldStr, String inNewStr) {
		String _tempStr = "";

		int _pos = 0;
		int _temp = inOldStr.length();

		String _source = inSource.toLowerCase();

		String _oldStr = inOldStr.toLowerCase();

		_pos = _source.indexOf(_oldStr);
		while (_pos >= 0) {
			_tempStr = _tempStr + inSource.substring(0, _pos) + inNewStr;

			_source = _source.substring(_pos + _temp);

			inSource = inSource.substring(_pos + _temp);

			_pos = _source.indexOf(_oldStr);
		}
		return _tempStr + inSource;
	}

	public static int toInt(String inStr) {
		return toInt(inStr, 0);
	}

	public static int toInt(String inStr, int inDefault) {
		if (inStr == null) {
			return inDefault;
		}
		int _temp = inDefault;
		try {
			_temp = Integer.valueOf(inStr).intValue();
		} catch (NumberFormatException e) {
			_temp = inDefault;
		}
		return _temp;
	}

	public static String Null2Empty(String inStr) {
		if (inStr == null) {
			return "";
		}
		return inStr;
	}

	public static String Null2NBSP(String inStr) {
		if (inStr == null) {
			return Null2Default(inStr, "&nbsp;");
		}
		if (inStr.length() < 1) {
			return "&nbsp;";
		}
		return inStr;
	}

	public static String Null2Default(String inStr, String inDefault) {
		if (inStr == null) {
			return inDefault;
		}
		return inStr;
	}

	public static long toLong(String inStr, long inDefault) {
		if (inStr == null) {
			return inDefault;
		}
		long _temp = inDefault;
		try {
			_temp = Long.valueOf(inStr).longValue();
		} catch (NumberFormatException e) {
			_temp = inDefault;
		}
		return _temp;
	}

	public static long toLong(String inStr) {
		return toLong(inStr, 0L);
	}

	public static double toDouble(String inStr, double inDefault) {
		if (inStr == null) {
			return inDefault;
		}
		double _temp = inDefault;
		try {
			_temp = Double.valueOf(inStr).doubleValue();
		} catch (NumberFormatException e) {
			_temp = inDefault;
		}
		return _temp;
	}

	public static double toDouble(String inStr) {
		return toDouble(inStr, 0.0D);
	}

	public static float toFloat(String inStr, float inDefault) {
		if (inStr == null) {
			return inDefault;
		}
		float _temp = inDefault;
		try {
			_temp = Float.parseFloat(inStr);
		} catch (NumberFormatException e) {
			_temp = inDefault;
		}
		return _temp;
	}

	public static float toFloat(String inStr) {
		return toFloat(inStr, 0.0F);
	}

	public static Integer parseInteger(String strInteger) {
		Integer _integer = null;
		if ((strInteger == null) || ("".equals(strInteger))) {
			return null;
		}
		try {
			_integer = new Integer(strInteger);
		} catch (Exception ex) {
			_integer = null;
		}
		return _integer;
	}

	public static Long parseLong(String strLong) {
		Long _long = null;
		if ((strLong == null) || ("".equals(strLong))) {
			return null;
		}
		try {
			_long = new Long(strLong);
		} catch (Exception ex) {
			_long = null;
		}
		return _long;
	}

	public static Double parseDouble(String strDbl) {
		Double num = null;
		if ((strDbl == null) || ("".equals(strDbl))) {
			return null;
		}
		try {
			num = new Double(strDbl);
		} catch (Exception ex) {
			num = null;
		}
		return num;
	}

	public static Float parseFloat(String strFloat) {
		Float num = null;
		if ((strFloat == null) || ("".equals(strFloat))) {
			return null;
		}
		try {
			num = new Float(strFloat);
		} catch (Exception ex) {
			num = null;
		}
		return num;
	}

	public static String repeat(String inStr, int inTimes) {
		String _rtn = "";
		for (int k = 0; k < inTimes; k++) {
			_rtn = _rtn + inStr;
		}
		return _rtn;
	}

	public static String left(String inStr, int inLen) {
		String _rtn = "";
		if (inStr.getBytes().length > inLen) {
			if (inStr.length() < inLen) {
				inLen = inStr.length();
			}
			for (int k = inLen; k > 0; k--) {
				_rtn = inStr.substring(0, k);
				if (_rtn.getBytes().length <= inLen) {
					break;
				}
			}
		} else {
			_rtn = inStr;
		}
		return _rtn;
	}

	public static String combinePath(String p1, String p2) {
		String path = null;
		if ((p1 == null) || (p2 == null)) {
			return p1 == null ? p2 : p1;
		}
		if (((p1.endsWith(File.separator)) || (p1.endsWith("/"))) && ((p2.startsWith(File.separator)) || (p2.startsWith("/")))) {
			path = p1 + p2.substring(1);
		} else if ((!p1.endsWith(File.separator)) && (!p1.endsWith("/")) && (!p2.startsWith(File.separator)) && (!p2.startsWith("/"))) {
			path = p1 + File.separator + p2;
		} else {
			path = p1 + p2;
		}
		return path;
	}

	public static String extendedName(String fileName) {
		if ((fileName == null) || (fileName.equals(""))) {
			return "";
		}
		if (fileName.lastIndexOf('.') > 0) {
			return fileName.substring(fileName.lastIndexOf('.'));
		}
		return "";
	}

	public static String formatNumber(Number num, String pattern) {
		String fmtNum = null;
		if (num == null) {
			num = new Double(0.0D);
		}
		DecimalFormat format = (DecimalFormat) DecimalFormat.getNumberInstance();
		format.applyPattern(pattern);
		try {
			fmtNum = format.format(num);
		} catch (Exception localException) {
		}
		return fmtNum;
	}

	public static String formatNumber(Number num) {
		return formatNumber(num, "0.000E0");
	}

	public static String formatNumber(double d, String pattern) {
		String fmtNum = null;

		DecimalFormat format = (DecimalFormat) DecimalFormat.getNumberInstance();
		format.applyPattern(pattern);
		try {
			fmtNum = format.format(d);
		} catch (Exception localException) {
		}
		return fmtNum;
	}

	public static String formatNumber(double d) {
		return formatNumber(d, "0.000E0");
	}

	public static String escapeHTML(String html) {
		return escapeHTML(html, 1);
	}

	public static String escapeHTML(String html, int tabIndent) {
		if (html == null) {
			return "";
		}
		int start = 0;
		int length = html.length();
		char[] arrayBuffer = html.toCharArray();
		StringBuilder escapedBuffer = null;

		int lastCarriage = -1;
		for (int i = 0; i < length; i++) {
			char c = arrayBuffer[i];
			if ((c == '?') || (c == '?')) {
				char[] escaped = c == '?' ? HTML_UNICODE_NEW_LINE1 : HTML_UNICODE_NEW_LINE2;
				if (start == 0) {
					escapedBuffer = new StringBuilder(length + 32);
				}
				if (start < i) {
					escapedBuffer.append(arrayBuffer, start, i - start);
				}
				start = i + 1;
				if (escaped.length > 0) {
					escapedBuffer.append(escaped);
				}
			} else if (c <= '>') {
				if (c == '\t') {
					if (start == 0) {
						escapedBuffer = new StringBuilder(length + 32);
					}
					if (start < i) {
						escapedBuffer.append(arrayBuffer, start, i - start);
					}
					start = i + 1;
					if (tabIndent > 0) {
						int spaces = tabIndent - (i - lastCarriage - 1) % tabIndent;
						for (int j = 0; j < spaces; j++) {
							escapedBuffer.append("&nbsp;");
						}
					}
				} else {
					char[] escaped = htmlCharactersRepresentation[c];
					if (escaped != null) {
						if (start == 0) {
							escapedBuffer = new StringBuilder(length + 32);
						}
						if (start < i) {
							escapedBuffer.append(arrayBuffer, start, i - start);
						}
						start = i + 1;
						if (escaped.length > 0) {
							escapedBuffer.append(escaped);
						}
					}
					if (c == '\n') {
						lastCarriage = i;
					}
				}
			}
		}
		if (start == 0) {
			return html;
		}
		if (start < length) {
			escapedBuffer.append(arrayBuffer, start, length - start);
		}
		return escapedBuffer.toString();
	}

	public static String toHex(byte[] buffer) {
		StringBuilder sb = new StringBuilder(buffer.length * 2);
		for (int i = 0; i < buffer.length; i++) {
			sb.append(Character.forDigit((buffer[i] & 0xF0) >> 4, 16));
			sb.append(Character.forDigit(buffer[i] & 0xF, 16));
		}
		return sb.toString();
	}

	public static int randomInt() {
		return RAND.nextInt();
	}

	public static int randomInt(int upperBound) {
		return RAND.nextInt(upperBound);
	}

	public static int randomInt(int lowerBound, int upperBound) {
		return RAND.nextInt(upperBound - lowerBound) + lowerBound;
	}

	public static long randomLong() {
		return RAND.nextLong();
	}

	public static long randomLong(long lowerBound, long upperBound) {
		return (long) ((RAND.nextDouble() * (upperBound - lowerBound)) + lowerBound);
	}

	public static long randomLong(long upperBound) {
		return randomLong(0L, upperBound);
	}

	public static String randomString(int size) {
		if (size <= 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			int randCharIndex = randomInt(CHAR_SEQUENCE.length - 1);
			sb.append(CHAR_SEQUENCE[randCharIndex]);
		}
		return sb.toString();
	}

	public static String[] tokenizeToStringArray(String str, String delimiters) {
		return tokenizeToStringArray(str, delimiters, true, true);
	}

	public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
		StringTokenizer st = new StringTokenizer(str, delimiters);
		List tokens = new ArrayList();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if ((!ignoreEmptyTokens) || (token.length() > 0)) {
				tokens.add(token);
			}
		}
		return toStringArray(tokens);
	}

	public static String[] toStringArray(Collection collection) {
		if (collection == null) {
			return null;
		}
		return (String[]) collection.toArray(new String[collection.size()]);
	}

	public static String encodeURL(String s) {
		return encodeURL(s, "UTF-8");
	}

	public static String encodeURL(String s, String encoding) {
		if ((s == null) || (s.equals(""))) {
			return s;
		}
		try {
			return URLEncoder.encode(s, encoding);
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	public static String decodeURL(String s) {
		return decodeURL(s, "UTF-8");
	}

	public static String decodeURL(String s, String encoding) {
		if ((s == null) || (s.equals(""))) {
			return s;
		}
		try {
			return URLDecoder.decode(s, encoding);
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	public static String escapeXml(String s) {
		if (s == null) {
			return "";
		}
		int start = 0;
		int length = s.length();
		char[] arrayBuffer = s.toCharArray();
		StringBuilder escapedBuffer = null;
		for (int i = 0; i < length; i++) {
			char c = arrayBuffer[i];
			if (c <= '>') {
				char[] escaped = specialCharactersRepresentation[c];
				if (escaped != null) {
					if (start == 0) {
						escapedBuffer = new StringBuilder(length + 5);
					}
					if (start < i) {
						escapedBuffer.append(arrayBuffer, start, i - start);
					}
					start = i + 1;

					escapedBuffer.append(escaped);
				}
			}
		}
		if (start == 0) {
			return s;
		}
		if (start < length) {
			escapedBuffer.append(arrayBuffer, start, length - start);
		}
		return escapedBuffer.toString();
	}

	public static String escapeScript(String s) {
		if (s == null) {
			return "";
		}
		int start = 0;
		int length = s.length();
		char[] arrayBuffer = s.toCharArray();
		StringBuilder escapedBuffer = null;
		for (int i = 0; i < length; i++) {
			char c = arrayBuffer[i];
			if (c <= '\\') {
				char[] escaped = scriptCharactersRepresentation[c];
				if (escaped != null) {
					if (start == 0) {
						escapedBuffer = new StringBuilder(length + 5);
					}
					if (start < i) {
						escapedBuffer.append(arrayBuffer, start, i - start);
					}
					start = i + 1;

					escapedBuffer.append(escaped);
				}
			}
		}
		if (start == 0) {
			return s;
		}
		if (start < length) {
			escapedBuffer.append(arrayBuffer, start, length - start);
		}
		return escapedBuffer.toString();
	}
}
