package com.dx.facerecognition.dao;

import com.dx.facerecognition.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
        user.setQuestion("q");
        user.setAnswer("a");
        int i = userDao.insertNewUser(user);
        assertEquals(1,i);
    }

    @Test
    public void updateUserInfo() {
        User user = new User("n",1,null,null);
        int i = userDao.updateUserInfo(user);
        assertEquals(1,i);
    }
}