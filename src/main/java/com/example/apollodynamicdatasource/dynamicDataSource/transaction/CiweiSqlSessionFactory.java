package com.example.apollodynamicdatasource.dynamicDataSource.transaction;

import com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean;
import com.example.apollodynamicdatasource.dynamicDataSource.DataSourceConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

/**
 * @author 马秀成
 * @date 2019/8/12
 * @jdk.version 1.8
 * @desc 配置sqlSessionFactory
 */
@Configuration
@Slf4j
public class CiweiSqlSessionFactory {

    @Autowired
    DataSourceConfiguration.DynamicDataSource dataSource;

    @Bean
    public SqlSessionFactory sqlSessionFactory() {
        log.info("--------------------  sqlSessionFactory init ---------------------");
        try {
//            SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
            MybatisSqlSessionFactoryBean sessionFactoryBean = new MybatisSqlSessionFactoryBean();
            sessionFactoryBean.setDataSource(dataSource);
            sessionFactoryBean.setTransactionFactory(new MultiDataSourceTransactionFactory());
            // 读取配置
            sessionFactoryBean.setTypeAliasesPackage("com.example.apollodynamicdatasource.entity");

            //设置mapper.xml文件所在位置
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath*:/mapper/*.xml");
            sessionFactoryBean.setMapperLocations(resources);
            return sessionFactoryBean.getObject();
        } catch (IOException e) {
            log.error("mybatis resolver mapper*xml is error",e);
            return null;
        } catch (Exception e) {
            log.error("mybatis sqlSessionFactoryBean create error",e);
            return null;
        }
    }

}
