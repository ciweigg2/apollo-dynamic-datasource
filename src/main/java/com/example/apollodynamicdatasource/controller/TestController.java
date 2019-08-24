package com.example.apollodynamicdatasource.controller;

import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.ctrip.framework.apollo.spring.annotation.ApolloJsonValue;
import com.example.apollodynamicdatasource.dynamicDataSource.properties.DynamicDataSourceProperties;
import com.example.apollodynamicdatasource.user.model.UserModel;
import com.example.apollodynamicdatasource.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 马秀成
 * @date 2019/8/8
 * @jdk.version 1.8
 * @desc
 */
@RestController
public class TestController {

	@Autowired
	private IUserService userService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * @ApolloJsonValue 这个注解会自动刷新bean 不需要手动去刷新了
	 */
	@ApolloJsonValue("${new-db}")
	private DynamicDataSourceProperties dynamicDataSourceProperties;

	@RequestMapping(value = "test")
	public String test() throws Exception {
		UserModel user = new UserModel();
		user.setId(123456);
		user.setName("事务测试呀");
		userService.insertUsers(user);
		return "enjoy it;";
	}

	/**
	 * 获取json配置
	 *
	 * @return json配置
	 */
	@RequestMapping(value = "test2")
	public String test2() {
		String someNamespace = "configuration2";
		ConfigFile configFile = ConfigService.getConfigFile(someNamespace, ConfigFileFormat.JSON);
		String content = configFile.getContent();
		return content;
	}

	/**
	 * 获取json配置
	 *
	 * @return json配置
	 */
	@RequestMapping(value = "test3")
	public void test3() {
		UserModel userModel = new UserModel().setId(3213123).setName("222");
		userService.saves(userModel);
		System.out.println("测试分支呀");
	}

}
