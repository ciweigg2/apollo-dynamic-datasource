package com.example.apollodynamicdatasource.dynamicDataSource.util;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DataSourceTerminationTask implements Runnable {

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_TIMES = 10;

    /**
     * 重试时间5s
     */
    private static final int RETRY_DELAY_IN_MILLISECONDS = 5000;

    private final DataSource dataSourceToTerminate;

    private final ScheduledExecutorService scheduledExecutorService;

    private volatile int retryTimes;

    public DataSourceTerminationTask(DataSource dataSourceToTerminate, ScheduledExecutorService scheduledExecutorService) {
        this.dataSourceToTerminate = dataSourceToTerminate;
        this.scheduledExecutorService = scheduledExecutorService;
        this.retryTimes = 0;
    }

    @Override
    public void run() {
        if (terminate(dataSourceToTerminate)) {
            //没有活动的链接 关闭数据源
            log.info("Data source {} terminated successfully!", dataSourceToTerminate);
        } else {
            //尝试继续关闭数据源
            scheduledExecutorService.schedule(this, RETRY_DELAY_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 判断是否还需要检查活动的链接
     *
     * @param dataSource 数据源
     * @return boolean
     */
    private boolean terminate(DataSource dataSource) {
        log.info("Trying to terminate data source: {}", dataSource);
        try {
            if (dataSource instanceof HikariDataSource) {
                //可能抛出异常 所以次数到达上限 不需要再尝试
                if(retryTimes > MAX_RETRY_TIMES){
                    return true;
                }
                return terminateHikariDataSource((HikariDataSource) dataSource);
            }
            log.error("Not supported data source: {}", dataSource);
            return true;
        } catch (Throwable ex) {
            log.warn("Terminating data source {} failed, will retry in {} ms, error message: {}", dataSource,
                    RETRY_DELAY_IN_MILLISECONDS, ex.getMessage());
            return false;
        } finally {
            //重试次数累加
            retryTimes++;
        }
    }

    /**
     * 校验是否有活动链接 直到没有任何活动链接时 我们需要关闭
     *
     * @param dataSource 数据源
     * @return 是否存在活动的链接
     */
    private boolean terminateHikariDataSource(HikariDataSource dataSource) {
        HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();
        poolMXBean.softEvictConnections();
        //如果活动的链接大于0并且重试次数小于最大重试次数返回false 说明需要继续重试
        if (poolMXBean.getActiveConnections() > 0 && retryTimes < MAX_RETRY_TIMES) {
            log.warn("Data source {} still has {} active connections, will retry in {} ms.", dataSource,
                    poolMXBean.getActiveConnections(), RETRY_DELAY_IN_MILLISECONDS);
            return false;
        }
        //输出重试日志
        if (poolMXBean.getActiveConnections() > 0) {
            log.warn("Retry times({}) >= {}, force closing data source {}, with {} active connections!", retryTimes,
                    MAX_RETRY_TIMES, dataSource, poolMXBean.getActiveConnections());
        }
        //关闭连接池
        dataSource.close();
        return true;
    }
    
}
