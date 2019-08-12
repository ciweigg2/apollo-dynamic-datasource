package com.example.apollodynamicdatasource.service;

import com.example.apollodynamicdatasource.entity.User;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ciwei
 * @since 2019-08-12
 */
public interface IUserService extends IService<User> {

	void insertUsers(User user) throws Exception;

}
