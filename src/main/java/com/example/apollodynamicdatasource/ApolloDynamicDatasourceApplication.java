package com.example.apollodynamicdatasource;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableApolloConfig
@SpringBootApplication
@MapperScan("com.example.apollodynamicdatasource.mapper")
public class ApolloDynamicDatasourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApolloDynamicDatasourceApplication.class, args);
    }

}
