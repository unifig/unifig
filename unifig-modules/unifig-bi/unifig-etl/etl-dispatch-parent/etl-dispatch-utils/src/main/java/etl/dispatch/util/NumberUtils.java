package etl.dispatch.util;

/**
 *
 *
 */
public class NumberUtils {
	public static boolean booleanValue(String v) {
		return (v != null && "true".equals(v));
	}

	public static boolean booleanValue(Integer v) {
		return (v != null && v.intValue() != 0);
	}

	public static boolean booleanValue(Byte v) {
		return (v != null && v.byteValue() != 0);
	}

	public static boolean booleanValue(Short v) {
		return (v != null && v.shortValue() != 0);
	}

	public static boolean booleanValue(int v) {
		return (v != 0);
	}

	public static boolean booleanValue(short v) {
		return (v != (short) 0);
	}

	public static boolean booleanValue(byte v) {
		return (v != (byte) 0);
	}

	public static int intValue(String v) {
		return v == null || v.length() == 0 ? 0 : Integer.parseInt(v);
	}

	public static int intValue(Number v) {
		return v == null ? 0 : v.intValue();
	}

	public static int intValue(Object v) {
		return intValue(v, 0);
	}

	public static int intValue(Object v, int defaultValue) {
		if (v == null)
			return defaultValue;

		if (v instanceof String) {
			return intValue((String) v);
		} else if (v instanceof Number) {
			return ((Number) v).intValue();
		}

		return defaultValue;
	}

	public static byte byteValue(String v) {
		return v == null || v.length() == 0 ? 0 : Byte.parseByte(v);
	}

	public static byte byteValue(Number v) {
		return v == null ? 0 : v.byteValue();
	}

	public static byte byteValue(Object v) {
		return byteValue(v, (byte) 0);
	}

	public static byte byteValue(Object v, byte defaultValue) {
		if (v == null)
			return defaultValue;

		if (v instanceof String) {
			return byteValue((String) v);
		} else if (v instanceof Number) {
			return ((Number) v).byteValue();
		}

		return defaultValue;
	}

	public static short shortValue(String v) {
		return v == null || v.length() == 0 ? 0 : Short.parseShort(v);
	}

	public static short shortValue(Number v) {
		return v == null ? 0 : v.shortValue();
	}

	public static short shortValue(Object v) {
		return shortValue(v, (short) 0);
	}

	public static short shortValue(Object v, short defaultValue) {
		if (v == null)
			return defaultValue;

		if (v instanceof String) {
			return shortValue((String) v);
		} else if (v instanceof Number) {
			return ((Number) v).shortValue();
		}

		return defaultValue;
	}

	public static long longValue(String v) {
		return v == null || v.length() == 0 ? 0L : Long.parseLong(v);
	}

	public static long longValue(Number v) {
		return v == null ? 0L : v.longValue();
	}

	public static long longValue(Object v) {
		return longValue(v, 0L);
	}

	public static long longValue(Object v, long defaultValue) {
		if (v == null)
			return defaultValue;

		if (v instanceof String) {
			return longValue((String) v);
		} else if (v instanceof Number) {
			return ((Number) v).longValue();
		}

		return defaultValue;
	}

	public static float floatValue(String v) {
		return v == null || v.length() == 0 ? 0 : Float.parseFloat(v);
	}

	public static float floatValue(Number v) {
		return v == null ? 0 : v.floatValue();
	}

	public static float floatValue(Object v) {
		return floatValue(v, 0);
	}

	public static float floatValue(Object v, float defaultValue) {
		if (v == null)
			return defaultValue;

		if (v instanceof String) {
			return floatValue((String) v);
		} else if (v instanceof Number) {
			return ((Number) v).floatValue();
		}

		return defaultValue;
	}

	/**
	 * 将int转化为4字节数组
	 * 
	 * @param i
	 *            int数值
	 * @return 4字节数组
	 */
	public static byte[] intToByteArray(int i) {
		byte high1 = (byte) ((i & 0xFF000000) >> 24); // 1 high-byte
		byte high2 = (byte) ((i & 0xFF0000) >> 16); // 1 high-byte
		byte low1 = (byte) ((i & 0xFF00) >> 8);
		byte low2 = (byte) (i & 0xFF);
		return new byte[] { high1, high2, low1, low2 };
	}

	// long类型转成byte数组
	public static byte[] longToByteArray(long number) {
		long temp = number;
		byte[] b = new byte[8];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Long(temp & 0xff).byteValue();// 将最低位保存在最低位
			temp = temp >> 8; // 向右移8位
		}
		return b;
	}

	// byte数组转成long
	public static long byteArrayToLong(byte[] b) {
		long s = 0;
		long s0 = b[0] & 0xff;// 最低位
		long s1 = b[1] & 0xff;
		long s2 = b[2] & 0xff;
		long s3 = b[3] & 0xff;
		long s4 = b[4] & 0xff;// 最低位
		long s5 = b[5] & 0xff;
		long s6 = b[6] & 0xff;
		long s7 = b[7] & 0xff;

		// s0不变
		s1 <<= 8;
		s2 <<= 16;
		s3 <<= 24;
		s4 <<= 8 * 4;
		s5 <<= 8 * 5;
		s6 <<= 8 * 6;
		s7 <<= 8 * 7;
		s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
		return s;
	}

	/**
	 * 将4字节数组转化为int
	 * 
	 * @param intBytes
	 *            4字节数组
	 * @return int值
	 */
	public static int byteArrayToInt(byte[] intBytes) {
		return byteArrayToInt(intBytes, 0);
	}

	/**
	 * 将4字节数组转化为int
	 * 
	 * @param intBytes
	 *            4字节数组
	 * @return int值
	 */
	public static int byteArrayToInt(byte[] intBytes, int offset) {
		if (intBytes == null || intBytes.length < offset + 4) {
			throw new IllegalArgumentException("illegal argument: intBytes, must be bytes of length 4");
		}

		int high1 = (int) intBytes[offset];
		int high2 = (int) intBytes[offset + 1];
		int low1 = (int) intBytes[offset + 2];
		int low2 = (int) intBytes[offset + 3];

		if (high1 < 0) {
			high1 += 256;
		}

		if (high2 < 0) {
			high2 += 256;
		}

		if (low1 < 0) {
			low1 += 256;
		}

		if (low2 < 0) {
			low2 += 256;
		}

		return ((high1 << 24) | (high2 << 16) | (low1 << 8) | low2);
	}

}
