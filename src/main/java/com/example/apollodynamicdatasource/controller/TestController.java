package com.example.apollodynamicdatasource.controller;

import com.example.apollodynamicdatasource.entity.User;
import com.example.apollodynamicdatasource.service.IUserService;
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

	@RequestMapping(value = "test")
	public String test() throws Exception {
		User user = new User();
		user.setId(123456);
		user.setName("事务测试呀");
		userService.insertUsers(user);
		return "enjoy it;";
	}

}
