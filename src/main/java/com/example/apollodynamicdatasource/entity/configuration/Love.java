package com.example.apollodynamicdatasource.entity.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 测试apollo自动装配 使用@ConfigurationProperties读取的配置 需要手动刷新
 */
@ConfigurationProperties(prefix = "i.love")
@Data
@RefreshScope
@Configuration
public class Love {

	private String u;

}
