package com.cssl.springboot.service;


import com.cssl.springboot.pojo.Users;

import java.util.List;

public interface UsersService {

    List<Users> findAllUsers();

    Users findById(Integer id);

    Integer save(Users users);

    Integer update(Users users);

    Integer delete(Integer id);
}
