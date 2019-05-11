package com.dx.facerecognition.dao;/**
 * Created by Administrator on 2019/5/8.
 *
 * @author Administrator
 */

import com.dx.facerecognition.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 *@ClassName UserDao
 *@Description 用户信息数据存取对象
 *@Autor Administrator
 *@Date 2019/5/8 23:05
 **/
@Mapper
public interface UserDao {

    public User getUserByName(String username);

    public int insertNewUser(User user);

    public int updateUserInfo(User user);

}
