package com.yiqiandewo.service.impl;

import com.yiqiandewo.mapper.UserMapper;
import com.yiqiandewo.pojo.User;
import com.yiqiandewo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User selectOne(String username) {
        return userMapper.selectOne(username);
    }
}
