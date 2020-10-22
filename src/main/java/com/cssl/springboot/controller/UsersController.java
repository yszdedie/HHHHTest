package com.cssl.springboot.controller;

import com.cssl.springboot.pojo.Users;
import com.cssl.springboot.service.impl.UsersServiceImpl;
import com.cssl.springboot.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class UsersController {

    @Autowired
    private UsersServiceImpl usersService;

    @Autowired
    private RedisUtil redisUtil;

    //查询
    @GetMapping("/findAll")
    public List<Users> findAll(){
        List<Users> list=(List<Users>)redisUtil.get("users");
        if(list==null){
            System.out.println("redis中不存在users.....");
            list = usersService.findAllUsers();
            redisUtil.set("users",list,300L);
        }else{
            System.out.println("redis中存在users.....");
        }
        return list;
     }

     //添加
    @GetMapping("/save")
    public String save(){
        Users u=new Users();
        u.setUname("唐三");
        u.setUpwd("123");
        u.setBirthday(new Date());
        Integer save = usersService.save(u);
        //删除
        redisUtil.remove("users");
        return "新增成功";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable("id") Integer id){
        Users u=new Users();
        u.setUname("小舞");
        u.setUpwd("123456");
        u.setBirthday(new Date());
        u.setId(id);
        Integer update = usersService.update(u);
        //删除
        redisUtil.remove("users");
        return "修改成功";
    }

    //删除
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id){
        Integer delete = usersService.delete(id);
        redisUtil.remove("users");
        return "删除成功";
    }

    //==========================注解版annotation

    @GetMapping("/aFindById/{id}")
    public Users aFindById(@PathVariable("id") Integer id){
           Users u= usersService.findById(id);
        System.out.println("aFindById controller");
        return u;
    }

    @GetMapping("/aFindById2/{id}")
    public Users aFindById2(@PathVariable("id") Integer id){
        Users u= (Users)redisUtil.get("users"+id);
        System.out.println("aFindById controller");
        return u;
    }



    @GetMapping("/asave")
    public String aSave(){
        Users u=new Users();
        u.setUname("唐三20");
        u.setUpwd("123456");
        u.setBirthday(new Date());
        Integer save = usersService.save(u);
        return "注解新增成功";
    }


    @GetMapping("/aupdate/{id}")
    public String aUpdate(@PathVariable("id") Integer id){
        Users u=new Users();
        u.setId(id);
        u.setUname("唐三222");
        u.setUpwd("123456");
        u.setBirthday(new Date());
        Integer update = usersService.update(u);
        return "修改成功";
    }

    @GetMapping("/adel/{id}")
    public String adel(@PathVariable("id") Integer id){
        Integer delete = usersService.delete(id);
        redisUtil.remove("users");
        return "删除成功";
    }


}
