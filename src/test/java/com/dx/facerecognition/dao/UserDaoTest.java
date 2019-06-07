package com.dx.facerecognition.dao;

import com.dx.facerecognition.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by Administrator on 2019/5/9.
 *
 * @author Administrator
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDaoTest {
    @Autowired
    UserDao userDao;

    @Autowired
    private JavaMailSender mailSender;

    @Test
    public void getUserByName() {
        User dx = userDao.getUserByName("n");
        System.out.println(dx.toString());
    }

    @Test
    public void insertNewUser() {
        User user = new User();
        user.setUsername("n");
        user.setImageSize(1);
        user.setEmail("q");
        int i = userDao.insertNewUser(user);
        assertEquals(1, i);
    }

    @Test
    public void updateUserInfo() {
        User user = new User("n", 1, null);
        int i = userDao.updateUserInfo(user);
        assertEquals(1, i);
    }

    @Test
    public void sendtest() {
        //生成并填充消息体
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("dengxu9979@qq.com");//发送邮箱
        mailMessage.setTo("fengtao256@qq.com");//接收邮箱
        mailMessage.setSubject("测试邮件主题"); //邮件主题
        mailMessage.setText("测试邮件"); //邮件正文


        //发送邮件
        mailSender.send(mailMessage);
    }
}