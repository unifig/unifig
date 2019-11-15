package etl.dispatch.java.datasource;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import com.alibaba.druid.pool.DruidDataSource;
import etl.dispatch.java.datasource.bean.extend.JdbcPoolDruidBean;
import etl.dispatch.util.MD5;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.convert.MapConvertUtil;

@Configuration
@Service
public class DataSourcePool {
	private static final Logger logger = LoggerFactory.getLogger(DataSourcePool.class);
	private Lock lock = new ReentrantLock();

	public DataSource getDataSource(Map<String, Object> dataSourceMap) {
		if (null != dataSourceMap && !dataSourceMap.isEmpty()) {
			JdbcPoolDruidBean jdbcPoolBean = MapConvertUtil.toBean(JdbcPoolDruidBean.class, dataSourceMap);
			if (null != jdbcPoolBean) {
				return this.getDataSource(jdbcPoolBean);
			}else{
				logger.error("Convert dataSourceMap to JdbcPoolDruidBean, bean is null or empty, and the job task fails");
			}
		}else{
			logger.error("Gets dataSource, map is null or empty, and the job task fails");
		}
		return null;
	}

	public DataSource getDataSource(JdbcPoolDruidBean jdbcPoolBean) {
		String jdbcPoolKey = MD5.encryptToHex(jdbcPoolBean.getUrl());
		if (StringUtil.isNullOrEmpty(jdbcPoolKey)) {
			logger.error("Get jdbcPoolKey with " + jdbcPoolBean.getUrl() + " ; is null or empty, and the job task fails");
			return null;
		}
		this.lock.lock();
		try {
			return this.createDataSource(jdbcPoolBean);
		} finally {
			this.lock.unlock();
		}
	}

	public DataSource createDataSource(JdbcPoolDruidBean jdbcPoolBean) {
		if (StringUtil.isNullOrEmpty(jdbcPoolBean.getDriverClass())) {
			logger.error("Get DriverClass with " + jdbcPoolBean.getDriverClass() + " ; is null or empty, and the job task fails");
			return null;
		}
		if (StringUtil.isNullOrEmpty(jdbcPoolBean.getUrl())) {
			logger.error("Get Url with " + jdbcPoolBean.getUrl() + " ; is null or empty, and the job task fails");
			return null;
		}
		if (StringUtil.isNullOrEmpty(jdbcPoolBean.getUser())) {
			logger.error("Get User with " + jdbcPoolBean.getUser() + " ; is null or empty, and the job task fails");
			return null;
		}
		if (StringUtil.isNullOrEmpty(jdbcPoolBean.getPassword())) {
			logger.error("Get Password with " + jdbcPoolBean.getPassword() + " ; is null or empty, and the job task fails");
			return null;
		}
		if (StringUtil.isNullOrEmpty(jdbcPoolBean.getFilters())) {
			logger.error("Get Filters with " + jdbcPoolBean.getFilters() + " ; is null or empty, and the job task fails");
			jdbcPoolBean.setFilters("stat");
		}
		if (jdbcPoolBean.getInitialSize() > 10) {
			jdbcPoolBean.setInitialSize(5);
		}
		if (jdbcPoolBean.getMaxActive() > 50) {
			jdbcPoolBean.setMaxActive(10);
		}
		if (jdbcPoolBean.getMaxWait() < 1000 * 60 * 30) {
			jdbcPoolBean.setMaxWait(1000 * 60 * 30);
		}
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(jdbcPoolBean.getDriverClass());
		dataSource.setUrl(jdbcPoolBean.getUrl());
		dataSource.setUsername(jdbcPoolBean.getUser());
		dataSource.setPassword(jdbcPoolBean.getPassword());

		try {
			dataSource.setFilters(jdbcPoolBean.getFilters());
		} catch (SQLException ex) {
			jdbcPoolBean.setUrl(null);
			jdbcPoolBean.setPassword(null);
			logger.error(jdbcPoolBean.toString() + "; filters is null or error value" + ex.getMessage());
		}
		dataSource.setInitialSize(jdbcPoolBean.getInitialSize());
		dataSource.setMaxActive(jdbcPoolBean.getMaxActive());
		dataSource.setMaxWait(jdbcPoolBean.getMaxWait());
		dataSource.setTimeBetweenEvictionRunsMillis(jdbcPoolBean.getTimeBetweenEvictionRunsMillis());
		dataSource.setMinEvictableIdleTimeMillis(jdbcPoolBean.getMinEvictableIdleTimeMillis());
		dataSource.setValidationQuery(jdbcPoolBean.getValidationQuery());
		dataSource.setTestWhileIdle(jdbcPoolBean.isTestWhileIdle());
		dataSource.setTestOnBorrow(jdbcPoolBean.isTestOnBorrow());
		dataSource.setTestOnReturn(jdbcPoolBean.isTestOnReturn());
		dataSource.setPoolPreparedStatements(jdbcPoolBean.isPoolPreparedStatements());
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(jdbcPoolBean.getMaxPoolPreparedStatementPerConnectionSize());
		return dataSource;
	}
}
