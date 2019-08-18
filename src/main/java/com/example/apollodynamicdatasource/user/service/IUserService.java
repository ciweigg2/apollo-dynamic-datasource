package com.example.apollodynamicdatasource.user.service;

import com.example.apollodynamicdatasource.user.model.UserModel;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author Ciwei
 * @since 2019-08-18
 */
public interface IUserService extends IService<UserModel> {

	void insertUsers(UserModel user) throws Exception;

	void saves(UserModel userModel);

}
