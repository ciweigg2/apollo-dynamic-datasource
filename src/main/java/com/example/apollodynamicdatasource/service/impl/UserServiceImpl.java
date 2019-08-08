package com.example.apollodynamicdatasource.service.impl;

import com.example.apollodynamicdatasource.entity.User;
import com.example.apollodynamicdatasource.mapper.UserMapper;
import com.example.apollodynamicdatasource.service.IUserService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ciwei
 * @since 2019-08-08
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
