package com.example.apollodynamicdatasource.controller;

import com.example.apollodynamicdatasource.entity.User;
import com.example.apollodynamicdatasource.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
//	@Transactional(propagation = Propagation.REQUIRED ,rollbackFor = Exception.class)
	public List<User> test() throws Exception {
		User user = new User();
		user.setId(123456);
		user.setName("事务测试呀");
//		List<User> user = jdbcTemplate.query("select * from user", new BeanPropertyRowMapper<>(User.class));
//		List<User> userLists = userMapper.selectList(null);
//		System.out.println(JSONObject.toJSONString(userLists));
//		userService.insert(user);
		//手动切换数据 接下来在这个请求中 都会使用手动切换的数据库
//		DynamicDataSourceContextHolder.setDataSourceKey("default-db");
//		userService.insert(user);
//		throw new Exception("测试回滚呀");
//		return userMapper.selectList(null);
		userService.insertUsers(user);
		return null;
	}

}
