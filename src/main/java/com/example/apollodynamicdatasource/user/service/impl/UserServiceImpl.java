package com.example.apollodynamicdatasource.user.service.impl;

import com.example.apollodynamicdatasource.dynamicDataSource.context.DynamicDataSourceContextHolder;
import com.example.apollodynamicdatasource.user.model.UserModel;
import com.example.apollodynamicdatasource.user.mapper.UserMapper;
import com.example.apollodynamicdatasource.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author Ciwei
 * @since 2019-08-18
 */
@Service
@Transactional(propagation = Propagation.REQUIRED ,rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, UserModel> implements IUserService {

	@Override
	public void insertUsers(UserModel user) throws Exception {
		this.save(user);
		DynamicDataSourceContextHolder.setDataSourceKey("default-db");
		this.save(user);
		throw new Exception("测试回滚呀");
	}

	@Override
	public void saves(UserModel userModel) {
		this.save(userModel);
	}

}
