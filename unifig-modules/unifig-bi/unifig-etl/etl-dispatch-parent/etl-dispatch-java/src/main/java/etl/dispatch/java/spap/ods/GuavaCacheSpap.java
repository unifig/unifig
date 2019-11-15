package etl.dispatch.java.spap.ods;

public interface GuavaCacheSpap {
	// 设置并发数为10，即同一时间最多只能有10个线程往cache执行写入操作
	public final static int DEFAULT_CACHE_CONCURRENCYLEVEL = 8;
	// 设置cache的初始大小为10，要合理设置该值
	public final static int DEFAULT_CACHE_INITIAL_CAPACITY = 1024;
	// Guava 缓存将尝试回收最近没有使用或总体上很少使用的缓存项。
	public final static int DEFAULT_CACHE_MAX_SIZE = 40960;
	// Guava 缓存项在给定时间内没有被读/写访问，则回收。请注意这种缓存的回收顺序和基于大小回收一样。
	public final static long DEFAULT_CACHE_EXPIRE = 7200;
	// 中文名称最大长度
	public final static int DEFAULT_NAME_MAX_LENGTH = 64;
}
