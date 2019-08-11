package com.example.apollodynamicdatasource.dynamicDataSource.util;

import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * openApi操作类 参考：https://github.com/ctripcorp/apollo/wiki/Apollo%E5%BC%80%E6%94%BE%E5%B9%B3%E5%8F%B0
 *
 * @Author Ciwei
 * @Date 2019/8/10/010
 */
@Configuration
public class OpenApiConfiguration {

    /**
     * portal url
     */
    private final String portalUrl = "http://114.67.69.121:8070";

    /**
     * 这是配置中心申请的token 很简单的 不会的请百度 token忘记的话 界面中的第三方应用ID查询可以获取
     */
    private final String token = "208bddd0a84faf09dfb89073a16d0ad439c2b9d4";

    @Bean
    public ApolloOpenApiClient apolloOpenApiClient() {
        ApolloOpenApiClient client = ApolloOpenApiClient.newBuilder()
                .withPortalUrl(portalUrl)
                .withToken(token)
                .build();
        return client;
    }

}
