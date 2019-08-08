package com.example.apollodynamicdatasource.ds;

import com.alibaba.fastjson.JSONObject;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.example.apollodynamicdatasource.dynamicDataSource.DynamicDataSourceProperties;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Hashtable;
import java.util.Set;

@Configuration
@Slf4j
public class DataSourceConfiguration {

	private final static String DATASOURCE_TAG = "db";

	@Autowired
	private ApplicationContext context;

	private Hashtable<Object, Object> dataSourceHashtable = new Hashtable<>();

	@ApolloConfig
	private Config config;

	@Value("${useDataSources}")
	private String useDataSources;

	/**
	 * 动态数据源
	 *
	 * @return DynamicDataSource
	 */
	@Bean("dataSource")
	public DynamicDataSource dynamicDataSource() {
		//初始化加载配置中心所有的数据库
		DynamicDataSource source = new DynamicDataSource();
		Set<String> propertyNames = config.getPropertyNames();
		propertyNames.forEach(key -> {
			//根据key规则查找数据源
			if (key.contains("db")) {
				String values = config.getProperty(key, "");
				dataSourceHashtable.put(key, dataSource(values));
			}
		});
		//设置动态数据源
		source.setTargetDataSources(dataSourceHashtable);
		//设置默认数据源 如果配置中心没有配置数据源 则会使用默认的数据源
//		source.setDefaultTargetDataSource(setDefaultDataSource());
		return source;
	}

	/**
	 * 设置默认数据源
	 *
	 * @return DataSource
	 */
	public DataSource setDefaultDataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(config.getProperty("spring.datasource.url", "jdbc:mysql://115.220.10.37:3309/test3?autoReconnect=true&useUnicode=true&characterEncoding=utf-8"));
		dataSource.setUsername(config.getProperty("spring.datasource.username", "root"));
		dataSource.setPassword(config.getProperty("spring.datasource.password", "123456"));
		dataSource.setMaximumPoolSize(config.getIntProperty("spring.datasource.hikari.maximumPoolSize", 5));
		return dataSource;
	}

	/**
	 * 监听配置变化
	 *
	 * @param changeEvent
	 */
	@ApolloConfigChangeListener
	public void onChange(ConfigChangeEvent changeEvent) {
		Set<String> changedKeys = changeEvent.changedKeys();
		DynamicDataSource source = context.getBean(DynamicDataSource.class);
		changedKeys.forEach(key -> {
			//获取所有数据库配置的属性
			String keys = changeEvent.getChange(key).getNewValue();
			dataSourceHashtable.put(key, dataSource(keys));
		});
		source.setTargetDataSources(dataSourceHashtable);
		source.afterPropertiesSet();
	}

	/**
	 * 监听配置变化 使用的数据库 上下文设置
	 *
	 * @param changeEvent
	 */
	@ApolloConfigChangeListener
	public void onChangeUseDataSources(ConfigChangeEvent changeEvent) {
		DynamicDataSourceContextHolder.setDataSourceKey(useDataSources);
		log.info("数据库切换为：{}", config.getProperty(useDataSources, ""));
	}

	/**
	 * 创建新数据源
	 *
	 * @return DataSource
	 */
	public DataSource dataSource(String values) {
		HikariDataSource dataSource = new HikariDataSource();
		//只处理json格式的
		if (JSONObject.isValid(values)) {
			//加载多数据源
			DynamicDataSourceProperties dynamicDataSourceProperties = JSONObject.parseObject(values, DynamicDataSourceProperties.class);
			dataSource.setJdbcUrl(dynamicDataSourceProperties.getUrl());
			dataSource.setUsername(dynamicDataSourceProperties.getUsername());
			dataSource.setPassword(dynamicDataSourceProperties.getPassword());
			dataSource.setMaximumPoolSize(dynamicDataSourceProperties.getMaximumPoolSize());
		}
		return dataSource;
	}

	/**
	 * 选择对应的数据源
	 * 每次执行mybatis的时候都会经过这边的
	 * 所以可以根据需求选择对应的数据源
	 */
	public class DynamicDataSource extends AbstractRoutingDataSource {
		@Override
		protected Object determineCurrentLookupKey() {
			String dataSourceKey = DynamicDataSourceContextHolder.getDataSourceKey();
			if (dataSourceKey == null) {
				return useDataSources;
			}
			return dataSourceKey;
		}
	}

}