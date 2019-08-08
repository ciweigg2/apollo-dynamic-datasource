//package com.example.apollodynamicdatasource.ds;
//
//import com.ctrip.framework.apollo.model.ConfigChangeEvent;
//import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.sql.DataSource;
//
//@Slf4j
//@Configuration
//@EnableConfigurationProperties(DataSourceProperties.class)
//public class DataSourceConfig {
//    @Autowired
//    ApplicationContext context;
//
//    @Autowired
//    private org.springframework.cloud.context.scope.refresh.RefreshScope refreshScope;
//
//    @ApolloConfigChangeListener("application")
//    private void onChange(ConfigChangeEvent changeEvent) {
//        DataSourceProperties dataSourceProperties = context.getBean(DataSourceProperties.class);
//        changeEvent.changedKeys().stream().forEach(s -> {
//            if (s.contains("spring.datasource.password")) {
//                dataSourceProperties.setPassword(changeEvent.getChange(s).getNewValue());
//            }
//        });
//        refreshScope.refresh("dataSource");
//    }
//
//    @RefreshScope
//    @Bean
//    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
//        return dataSourceProperties.initializeDataSourceBuilder().build();
//    }
//
//}
