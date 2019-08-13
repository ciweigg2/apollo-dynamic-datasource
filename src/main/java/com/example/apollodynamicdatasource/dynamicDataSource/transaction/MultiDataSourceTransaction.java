package com.example.apollodynamicdatasource.dynamicDataSource.transaction;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.example.apollodynamicdatasource.dynamicDataSource.context.DynamicDataSourceContextHolder;
import org.apache.ibatis.transaction.Transaction;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * 多数据源切换的支持事务
 */
public class MultiDataSourceTransaction implements Transaction {

	private static final Log LOGGER = LogFactory.getLog(MultiDataSourceTransaction.class);

	private final DataSource dataSource;

	private Connection mainConnection;

	private String mainDatabaseIdentification;

	private ConcurrentMap<String, Connection> otherConnectionMap;

	private boolean isConnectionTransactional;

	private boolean autoCommit;

	public MultiDataSourceTransaction(DataSource dataSource) {
		notNull(dataSource, "No DataSource specified");
		this.dataSource = dataSource;
		otherConnectionMap = new ConcurrentHashMap<>();
		mainDatabaseIdentification = DynamicDataSourceContextHolder.getDataSourceKey();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Connection getConnection() throws SQLException {
		String databaseIdentification = DynamicDataSourceContextHolder.getDataSourceKey();
		if (databaseIdentification.equals(mainDatabaseIdentification)) {
			if (mainConnection != null) return mainConnection;
			else {
				openMainConnection();
				mainDatabaseIdentification = databaseIdentification;
				return mainConnection;
			}
		} else {
			if (!otherConnectionMap.containsKey(databaseIdentification)) {
				try {
					Connection conn = dataSource.getConnection();
					otherConnectionMap.put(databaseIdentification, conn);
				} catch (SQLException ex) {
					throw new CannotGetJdbcConnectionException("Could not get JDBC Connection", ex);
				}
			}
			return otherConnectionMap.get(databaseIdentification);
		}

	}

	private void openMainConnection() throws SQLException {
		this.mainConnection = DataSourceUtils.getConnection(this.dataSource);
		this.autoCommit = this.mainConnection.getAutoCommit();
		//判断是否是声明式事务 service @Transactional注解后 都是声明式事务
//		this.isConnectionTransactional = DataSourceUtils.isConnectionTransactional(this.mainConnection, this.dataSource);
		//因为必须使用@Transactional开启事务 所以这边改造一下 设置默认值false
		this.isConnectionTransactional = false;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"JDBC Connection ["
							+ this.mainConnection
							+ "] will"
							+ (this.isConnectionTransactional ? " " : " not ")
							+ "be managed by Spring");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void commit() throws SQLException {
		if (this.mainConnection != null && !this.isConnectionTransactional && !this.autoCommit) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Committing JDBC Connection [" + this.mainConnection + "]");
			}
			this.mainConnection.commit();
			for (Connection connection : otherConnectionMap.values()) {
				connection.commit();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void rollback() throws SQLException {
		if (this.mainConnection != null && !this.isConnectionTransactional && !this.autoCommit) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Rolling back JDBC Connection [" + this.mainConnection + "]");
			}
			this.mainConnection.rollback();
			for (Connection connection : otherConnectionMap.values()) {
				connection.rollback();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws SQLException {
		DataSourceUtils.releaseConnection(this.mainConnection, this.dataSource);
		for (Connection connection : otherConnectionMap.values()) {
			DataSourceUtils.releaseConnection(connection, this.dataSource);
		}
	}

	@Override
	public Integer getTimeout() throws SQLException {
		return null;
	}

}
