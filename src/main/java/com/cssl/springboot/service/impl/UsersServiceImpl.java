package com.cssl.springboot.service.impl;

import com.cssl.springboot.mapper.UsersMapper;
import com.cssl.springboot.pojo.Users;
import com.cssl.springboot.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Transactional
@Service
public class UsersServiceImpl implements UsersService {
    @Autowired
    private UsersMapper usersMapper;


    @Override
    public List<Users> findAllUsers() {
        return usersMapper.findAllUsers();
    }


    //@CacheEvict(value="user", key = "#id")
    //@CachePut(value="user", key = "#id", unless="#result==null", condition="#id%2==0")
    //@Cacheable(value="user",key = "#id", unless="#result==null", condition="#id%2==0")

    //使用注解表示只有满足注解中的条件才会存入redis中
    @Cacheable(value="user",key="#id",unless = "#result==null")
    @Override
    public Users findById(Integer id) {
        System.out.println("UsersServiceImpl findById:"+id);
        return usersMapper.findById(id);
    }

    //新增
    @CacheEvict(value="user",key="''")
    @Override
    public Integer save(Users users) {
        System.out.println("UsersServiceImpl save");
        return usersMapper.save(users);
    }

    //修改
    @CacheEvict(value="user",key="''")
    @Override
    public Integer update(Users users) {
        return usersMapper.update(users);
    }

    @CacheEvict(value="user",key="''")
    @Override
    public Integer delete(Integer id) {
        return usersMapper.delete(id);
    }



}
