package com.dx.facerecognition.controller;/**
 * Created by Administrator on 2019/5/2.
 *
 * @author Administrator
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 *@ClassName Index
 *@Description 前端页面转发
 *@Autor Administrator
 *@Date 2019/5/2 21:33
 **/
@Controller
public class Index {
    @GetMapping("/")
    public String index(){return "register";}
    @GetMapping("register")
    public String register(){return "register";}
    @GetMapping("login")
    public String login(){return "login";}

}
