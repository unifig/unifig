package etl.dispatch.script.util;

public class AddressBytesUtil {

	public static  long bytesToLong(byte[] address) {
		long ipNum = 0L;
		for (int i = 0; i < 4; i++) {
			long y = address[i];
			if (y < 0L) {
				y += 256L;
			}
			ipNum += (y << (3 - i) * 8);
		}
		return ipNum;
	}
}
