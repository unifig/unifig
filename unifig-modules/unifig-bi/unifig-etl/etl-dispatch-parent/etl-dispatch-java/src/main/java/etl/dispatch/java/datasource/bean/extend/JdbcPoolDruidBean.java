package etl.dispatch.java.datasource.bean.extend;

import etl.dispatch.java.datasource.bean.JdbcPoolBean;

public class JdbcPoolDruidBean extends JdbcPoolBean {
	private static final long serialVersionUID = -8742452723743006602L;

	// 常用的插件有： 监控统计用的filter:stat,日志用的filter:log4j,防御sql注入的filter:wall
	private String filters;

	// 初始化连接数量
	private int initialSize;

	// 最大连接池数量
	private int maxActive;

	// 配置获取连接等待超时的时间
	private int maxWait;

	// 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
	private int timeBetweenEvictionRunsMillis;

	// 配置一个连接在池中最小生存的时间，单位是毫秒
	private int minEvictableIdleTimeMillis;

	// 用来检测连接是否有效的sql，要求是一个查询语句
	private String validationQuery;

	// 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于  timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
	private boolean testWhileIdle;

	// 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
	private boolean testOnBorrow;

	// 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
	private boolean testOnReturn;

	// 是否缓存preparedStatement，也就是PSCache。 PSCache对支持游标的数据库性能提升巨大，比如说oracle。在mysql5.5以下的版本中没有PSCache功能，建议关闭掉。作者在5.5版本中使用PSCache，通过监控界面发现PSCache有缓存命中率记录，该应该是支持PSCache。
	private boolean poolPreparedStatements;

	// 指定每个连接上preparedStatement的大小
	private int maxPoolPreparedStatementPerConnectionSize;

	public String getFilters() {
		return filters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}

	public int getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public int getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}

	public int getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	public int getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public boolean isTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public boolean isPoolPreparedStatements() {
		return poolPreparedStatements;
	}

	public void setPoolPreparedStatements(boolean poolPreparedStatements) {
		this.poolPreparedStatements = poolPreparedStatements;
	}

	public int getMaxPoolPreparedStatementPerConnectionSize() {
		return maxPoolPreparedStatementPerConnectionSize;
	}

	public void setMaxPoolPreparedStatementPerConnectionSize(int maxPoolPreparedStatementPerConnectionSize) {
		this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
	}

	@Override
	public String toString() {
		return "JdbcPoolDruidBean [filters=" + filters + ", initialSize=" + initialSize + ", maxActive=" + maxActive + ", maxWait=" + maxWait + ", timeBetweenEvictionRunsMillis=" + timeBetweenEvictionRunsMillis + ", minEvictableIdleTimeMillis=" + minEvictableIdleTimeMillis + ", validationQuery=" + validationQuery + ", testWhileIdle=" + testWhileIdle + ", testOnBorrow=" + testOnBorrow + ", testOnReturn=" + testOnReturn + ", poolPreparedStatements=" + poolPreparedStatements + ", maxPoolPreparedStatementPerConnectionSize=" + maxPoolPreparedStatementPerConnectionSize + "]";
	}
}
