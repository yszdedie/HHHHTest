package com.cssl.springboot.mapper;

import com.cssl.springboot.pojo.Users;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UsersMapper {

    List<Users> findAllUsers();

    Users findById(Integer id);

    Integer save(Users users);

    Integer update(Users users);

    Integer delete(Integer id);


}
