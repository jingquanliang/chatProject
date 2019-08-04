package controller;

import dao.bean.*;
import dao.mapper.UserMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@EnableAutoConfiguration
public class UserController {

    /**
     * 引入日志，注意都是"org.slf4j"包下
     */
    private final static Logger logger = LoggerFactory.getLogger(SampleController.class);

    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/insert")
    public User insertUser(@RequestParam(value="name", defaultValue="World")String name,@RequestParam(value="age")int age) {

        logger.info(name+"-"+age);
        User user = new User(name,age);
        Integer flag = userMapper.addUser(user);
        logger.info("flag:"+flag);
        return user;
    }

    @RequestMapping("/findAll")
    public List<User> findAll() {
        return userMapper.findAll();
    }
}