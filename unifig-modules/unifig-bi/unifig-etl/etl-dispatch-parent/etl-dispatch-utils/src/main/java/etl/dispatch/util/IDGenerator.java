package etl.dispatch.util;

public class IDGenerator {

    private static long primarykeyId;

    private final static long twepoch = 1288834974657L;

    private static long sequence = 0L;

    private final static long primarykeyIdBits = 4L;

    public final static long maxWorkerId = -1L ^ -1L << primarykeyIdBits;

    private final static long sequenceBits = 10L;

    private final static long primarykeyIdBitsShift = sequenceBits;

    private final static long timestampLeftShift = sequenceBits + primarykeyIdBits;

    public final static long sequenceMask = -1L ^ -1L << sequenceBits;

    private static long lastTimestamp = -1L;

    @SuppressWarnings("static-access")
    public IDGenerator(final long primarykeyId) {
        super();
        if (primarykeyId > this.maxWorkerId || primarykeyId < 0) {
            throw new IllegalArgumentException(String.format(
                "worker Id can't be greater than %d or less than 0", this.maxWorkerId));
        }
        this.primarykeyId = primarykeyId;
    }

    /**
     * 获取主键生成
     * 
     * @return String
     */
    public static synchronized String getId() {
        // 转换成十六进制
        return Long.toHexString(nextId()).toUpperCase();
    }

    private static long nextId() {
        long timestamp = timeGen();
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // System.out.println("###########" + sequenceMask);
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        if (timestamp < lastTimestamp) {
            try {
                throw new Exception(String.format(
                    "Clock moved backwards.  Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        lastTimestamp = timestamp;
        long nextId = ( (timestamp - twepoch << timestampLeftShift))
                      | (primarykeyId << primarykeyIdBitsShift) | (sequence);
        return nextId;
    }

    private static long tilNextMillis(final long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private static long timeGen() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        System.out.println(IDGenerator.getId());
    }

}
