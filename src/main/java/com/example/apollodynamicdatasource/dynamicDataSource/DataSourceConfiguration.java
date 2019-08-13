package com.example.apollodynamicdatasource.dynamicDataSource;

import com.alibaba.fastjson.JSONObject;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.enums.PropertyChangeType;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.example.apollodynamicdatasource.dynamicDataSource.context.DynamicDataSourceContextHolder;
import com.example.apollodynamicdatasource.dynamicDataSource.properties.DynamicDataSourceProperties;
import com.example.apollodynamicdatasource.dynamicDataSource.util.DataSourceTerminationTask;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 动态数据源核心操作类
 */
@Configuration
@Slf4j
public class DataSourceConfiguration {

	@Autowired
	private ApplicationContext context;

	private Hashtable<Object, Object> dataSourceHashtable = new Hashtable<>();

	@ApolloConfig
	private Config config;

	@Value("${useDataSources}")
	private String useDataSources;

	private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

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
		return source;
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
			//如果包含db是数据库操作
			if (key.contains("db")) {
				//获取所有数据库配置的属性 根据配置的变化状态ADDED MODIFIED DELETED增删改数据源
				ConfigChange configChange = changeEvent.getChange(key);
				String values = configChange.getNewValue();
				if (configChange.getChangeType().equals(PropertyChangeType.ADDED) || configChange.getChangeType().equals(PropertyChangeType.MODIFIED)) {
					//新增和删除都往map中做put操作
					dataSourceHashtable.put(key, dataSource(values));
				}
				if (configChange.getChangeType().equals(PropertyChangeType.DELETED)) {
					//删除数据源并关闭数据连接池
					HikariDataSource hikariDataSource = (HikariDataSource) dataSourceHashtable.get(key);
					if (!hikariDataSource.isClosed()) {
						//关闭连接池 可能连接池还有用户在使用 所以需要异步尝试关闭保证数据的正确性
						if (hikariDataSource.getHikariPoolMXBean() != null) {
							log.warn("当前数据源之前在使用中 需要尝试关闭");
							asyncTerminate(hikariDataSource);
						} else {
							log.warn("当前数据源并未使用 无需尝试关闭");
						}
					}
					dataSourceHashtable.remove(key);
				}
			}else if (key.equals("useDataSources")){
				//选择数据库的key变化
				DynamicDataSourceContextHolder.setDataSourceKey(useDataSources);
				log.info("配置中心的动态切换的数据库为：{}" ,changeEvent.getChange(key).getNewValue());
			}else {
				log.info("key：{}有变化 变化的数据：{} enjoy it;" ,key , JSONObject.toJSONString(changeEvent.getChange(key)));
			}
		});
		source.setTargetDataSources(dataSourceHashtable);
		source.afterPropertiesSet();
	}

	/**
	 * 异步线程等待活动链接为0关闭数据源
	 *
	 * @param dataSource 数据源
	 */
	private void asyncTerminate(DataSource dataSource) {
		DataSourceTerminationTask task = new DataSourceTerminationTask(dataSource, scheduledExecutorService);
		scheduledExecutorService.schedule(task, 0, TimeUnit.MILLISECONDS);
	}

	/**
	 * 创建新数据源 这边根据规则来就行了 我这边配置的规则是key包含db然后格式是json的都是数据库的
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
			//必须关闭自动提交否则多数据源事务失效
			dataSource.setAutoCommit(false);
		}
		return dataSource;
	}

	/**
	 * 选择对应的数据源
	 * 每次执行mybatis的时候都会经过这边的 但是配置了事务会失效
	 * 所以可以根据需求选择对应的数据源
	 */
	public class DynamicDataSource extends AbstractRoutingDataSource {
		@Override
		protected Object determineCurrentLookupKey() {
			String dataSourceKey = DynamicDataSourceContextHolder.getDataSourceKey();
			if (dataSourceKey == null) {
				//初始化的时候值为空
				dataSourceKey = useDataSources;
				DynamicDataSourceContextHolder.setDataSourceKey(useDataSources);
			}
			return dataSourceKey;
		}
	}

}