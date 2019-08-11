### 介绍

* 结合apollo配置中心实现动态多数据源自动切换
* 实现无需重启
* 根据需求选择适当的数据源

参考：

```
http://www.kailing.pub/article/index/arcid/198.html
https://github.com/ctripcorp/apollo-use-cases/tree/master/dynamic-datasource
https://7le.top/2018/07/28/springcloud%EF%BC%9A%E5%AE%9E%E7%8E%B0%E5%A4%9A%E6%95%B0%E6%8D%AE%E6%BA%90%E4%BA%8B%E5%8A%A1/#more
```

### apollo配置

本项目使用apollo的配置 默认的环境env=DEV 多环境请参考官网配置

```
key:useDataSources
value:new-db

key:new-db
value:{ "url":"jdbc:mysql://127.0.0.1:3309/test1?autoReconnect=true&useUnicode=true&characterEncoding=utf-8", "username":"root", "password":"123456", "maximumPoolSize":"10" }

key:default-db
value:{ "url":"jdbc:mysql://127.0.0.1:3309/test2?autoReconnect=true&useUnicode=true&characterEncoding=utf-8", "username":"root", "password":"123456", "maximumPoolSize":"10" }
```

### 事务问题

@Transactional注解后事务问题

动态数据源使用determineCurrentLookupKey方式在开启事务Connection之前选择数据源

事务会使用缓存中的Connection连接所以手动设置不会生效 如果要手动切换数据源做查询操作的话 要在@Transactional注解的方法前使用

但是不能新增操作 因为不受事务控制 事务只控制第一次开启Connection链接的数据源

@Transactional注解配合动态数据源是没问题的 但是不能在该接口中手动切换数据源 这个是不生效的