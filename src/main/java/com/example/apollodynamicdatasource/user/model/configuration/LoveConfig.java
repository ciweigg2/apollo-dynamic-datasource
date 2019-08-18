package com.example.apollodynamicdatasource.user.model.configuration;

import com.alibaba.fastjson.JSONObject;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LoveConfig {

	@Autowired
	ApplicationContext context;

	@Autowired
	private RefreshScope refreshScope;

	@Autowired
	private Love love;

	/**
	 * @ApolloConfigChanageListener 会监听到i.love前缀值的变化后重新为love的属性赋值 更新@RefreshScope的注解的bean
	 *
	 * @param changeEvent
	 */
	@ApolloConfigChangeListener(interestedKeyPrefixes = "i.love")
	private void onChange(ConfigChangeEvent changeEvent) {
		refreshScope.refresh("love");
		log.info("刷新bean成功{}" , JSONObject.toJSON(love));
	}

}
