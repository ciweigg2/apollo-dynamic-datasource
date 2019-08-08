package com.example.apollodynamicdatasource.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.apollodynamicdatasource.entity.User;
import com.example.apollodynamicdatasource.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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
public class TestController<T> {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@RequestMapping(value = "test")
	public <T> T test() {
		//手动切换数据 接下来在这个请求中 都会使用手动切换的数据库
//		DynamicDataSourceContextHolder.setDataSourceKey("new-db");
		List<User> user = jdbcTemplate.query("select * from user", new BeanPropertyRowMapper<>(User.class));
		System.out.println(JSONObject.toJSONString(user));
		return (T) userMapper.selectList(null);
	}

}
