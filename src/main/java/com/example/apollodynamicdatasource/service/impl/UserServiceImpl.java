package com.example.apollodynamicdatasource.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.example.apollodynamicdatasource.dynamicDataSource.context.DynamicDataSourceContextHolder;
import com.example.apollodynamicdatasource.entity.User;
import com.example.apollodynamicdatasource.mapper.UserMapper;
import com.example.apollodynamicdatasource.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ciwei
 * @since 2019-08-12
 */
@Service
@Transactional(propagation = Propagation.REQUIRED ,rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

	@Override
	public void insertUsers(User user) throws Exception {
		this.insert(user);
		DynamicDataSourceContextHolder.setDataSourceKey("default-db");
		this.insert(user);
		throw new Exception("测试回滚呀");
	}

}
