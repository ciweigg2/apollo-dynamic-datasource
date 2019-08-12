package com.example.apollodynamicdatasource.dynamicDataSource.context;

/**
 * 动态数据源上下文
 */
public class DynamicDataSourceContextHolder {

	private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();

	/**
	 * 切换数据源
	 *
	 * @param key
	 */
	public static void setDataSourceKey(String key) {
		contextHolder.set(key);
	}

	/**
	 * 获取数据源
	 *
	 * @return
	 */
	public static String getDataSourceKey() {
		return contextHolder.get() == null ? "new-db" : contextHolder.get();
	}

	/**
	 * 重置数据源
	 */
	public static void clearDataSourceKey() {
		contextHolder.remove();
	}

}
