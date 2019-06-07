package com.dx.facerecognition.service.impl;/**
 * Created by Administrator on 2019/5/9.
 *
 * @author Administrator
 */

import com.dx.facerecognition.dao.FacesDao;
import com.dx.facerecognition.dao.UserDao;
import com.dx.facerecognition.entity.Faces;
import com.dx.facerecognition.entity.User;
import com.dx.facerecognition.service.Pretreat;
import com.dx.facerecognition.service.Recognit;
import com.dx.facerecognition.service.UserDeal;
import com.dx.facerecognition.util.ExcutionResultUtil;
import com.dx.facerecognition.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @Autowired
    FacesDao facesDao;
    @Autowired
    Recognit recognit;
    @Autowired
    Pretreat pretreat;

    @Override
    @Transactional
    public ExcutionResultUtil userRegister(User user, String img) {

        //对图片进行预处理
        ExcutionResultUtil pretreatImg = pretreat.pretreatImg(img, user.getUsername());
        if (!pretreatImg.isSuccess()) {
            return pretreatImg;
        }
        //获取预处理后的人脸路径
        String facesPath = pretreatImg.getMsg();

        try {

            //将用户信息持久化到数据库
            int i = userDao.insertNewUser(user);
            if (i == 1) {
                //当用户注册成功，持久化人脸数据
                facesDao.insertNewFace(new Faces(facesPath, user.getId()));
                return new ExcutionResultUtil(true, "注册成功!");
            } else {
                //如果用户信息持久化失败需要删除文件夹
                ExcutionResultUtil delectDirPath = PathUtil.deleteDirPath(facesPath);
                if (delectDirPath.isSuccess()) {
                    //删除成功则返回裁剪失败原因
                    return new ExcutionResultUtil(false, "注册失败");
                } else {
                    //删除失败则返回删除失败原因
                    return new ExcutionResultUtil(false, "注册失败" + delectDirPath.getMsg());
                }
            }
        } catch (Exception e) {

            if (pretreatImg.isSuccess()) {
                //如果用户信息持久化失败需要删除文件
                PathUtil.deleteDirPath(facesPath);
            }
            throw new RuntimeException("事务回滚" + e.getMessage());
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

    @Override
    public ExcutionResultUtil userRecognit(String img, int userid) {
        //通过用户id获取用户人脸库
        List<Faces> facesByUserId = facesDao.getFacesByUserId(userid);
        //对图片进行预处理
        ExcutionResultUtil pretreatImg = pretreat.pretreatImg(img, "temp");

        if (!pretreatImg.isSuccess()) {
            //预处理失败则返回
            return new ExcutionResultUtil(false, pretreatImg.getMsg());
        }

        //需要进行比对的人脸路径
        String tempPath = pretreatImg.getMsg();

        try {
            for (Faces face :
                    facesByUserId) {
                //将人脸库图片进行比对
                ExcutionResultUtil compare = recognit.compare(tempPath, face.getPath());
                if (compare.isSuccess()) {
                    return compare;
                }
            }
            return new ExcutionResultUtil(false, "匹配失败");
        } finally {
            if (pretreatImg.isSuccess()) {
                //将比对图片删除
                PathUtil.deleteDirPath(tempPath);
            }
        }

    }

    @Override
    @Transactional
    public ExcutionResultUtil userUpdate(User user, String img) {
        ExcutionResultUtil pretreatImg = pretreat.pretreatImg(img, user.getUsername());
        if (!pretreatImg.isSuccess()) {
            return pretreatImg;
        }

        //获取预处理后的人脸路径
        String facePath = pretreatImg.getMsg();

        try {

            if (user.getImageSize() < 3) {
                //当未到达指定阈值，则新加一个
                userDao.updateUserInfo(new User(user.getUsername(), user.getImageSize() + 1, null));
            } else {

                //当到达则删除最旧的
                //获取最旧路径
                String oldestFacePath = facesDao.getOldestFacePath(user.getId());
                //本地删除
                ExcutionResultUtil deleteDirPath = PathUtil.deleteDirPath(oldestFacePath);
                if (!deleteDirPath.isSuccess()){
                    return deleteDirPath;
                }
                //数据库删除
                facesDao.deleteFaceByUserId(user.getId());
            }
            //将新人脸信息录入
            facesDao.insertNewFace(new Faces(facePath, user.getId()));

            return new ExcutionResultUtil(true,"更新人脸信息成功");
        } catch (Exception e) {
            if (pretreatImg.isSuccess()) {
                PathUtil.deleteDirPath(facePath);
            }

            throw new RuntimeException("事务回滚" + e.getMessage());
        }

    }
}
