package com.dx.facerecognition.service.impl;/**
 * Created by Administrator on 2019/5/9.
 *
 * @author Administrator
 */

import com.dx.facerecognition.dao.UserDao;
import com.dx.facerecognition.entity.User;
import com.dx.facerecognition.service.UserDeal;
import com.dx.facerecognition.util.ExcutionResultUtil;
import com.dx.facerecognition.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName UserDealImpl
 * @Description 用户处理功能实现类
 * @Autor Administrator
 * @Date 2019/5/9 17:14
 **/
@Service
public class UserDealImpl implements UserDeal {

    @Autowired
    UserDao userDao;

    @Override
    public ExcutionResultUtil userRegister(User user) {
        try {
            int i = userDao.insertNewUser(user);
            if (i == 1) {
                return new ExcutionResultUtil(true, "注册成功!");
            } else {
                //如果用户信息持久化失败需要删除文件夹
                ExcutionResultUtil delectDirPath = PathUtil.deleteDirPath(user.getImagePath());
                if (delectDirPath.isSuccess()) {
                    //删除成功则返回裁剪失败原因
                    return new ExcutionResultUtil(false, "注册失败");
                } else {
                    //删除失败则返回删除失败原因
                    return new ExcutionResultUtil(false, "注册失败" + delectDirPath.getMsg());
                }
            }
        } catch (Exception e) {
            //如果用户信息持久化失败需要删除文件夹
            ExcutionResultUtil delectDirPath = PathUtil.deleteDirPath(user.getImagePath());
            if (delectDirPath.isSuccess()) {
                //删除成功则返回裁剪失败原因
                return new ExcutionResultUtil(false, e.getMessage());
            } else {
                //删除失败则返回删除失败原因
                return new ExcutionResultUtil(false, e.getMessage() + delectDirPath.getMsg());
            }
        }

    }

    @Override
    public ExcutionResultUtil checkUserexits(String username) {
        User user = userDao.getUserByName(username);
        if (user != null) {
            return new ExcutionResultUtil(false, "该用户名已经被注册，请重新输入");
        }
        return new ExcutionResultUtil(true, "该用户名可用");
    }

    @Override
    public User getUserInfo(String username) {
        return userDao.getUserByName(username);

    }
}
