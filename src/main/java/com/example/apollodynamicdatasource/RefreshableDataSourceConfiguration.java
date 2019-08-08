//package com.example.apollodynamicdatasource;
//
//import com.example.apollodynamicdatasource.ds.DynamicDataSource;
//import com.example.apollodynamicdatasource.util.DataSourceManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class RefreshableDataSourceConfiguration {
//
//  @Bean
//  public DynamicDataSource dataSource(DataSourceManager dataSourceManager) {
//    DataSource actualDataSource = dataSourceManager.createDataSource();
//    return new DynamicDataSource(actualDataSource);
//  }
//}
