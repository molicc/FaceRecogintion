package com.dx.facerecognition.service;/**
 * Created by Administrator on 2019/5/9.
 *
 * @author Administrator
 */

import com.dx.facerecognition.entity.User;
import com.dx.facerecognition.util.ExcutionResultUtil;

/**
 *
 *@ClassName UserDeal
 *@Description 用户处理功能接口
 *@Autor Administrator
 *@Date 2019/5/9 17:08
 **/
public interface UserDeal {

    public ExcutionResultUtil userRegister(User user,String img);

    public ExcutionResultUtil checkUserexits(String username);

    public User getUserInfo(String username);

    public ExcutionResultUtil userRecognit(String img,int userid);

    public ExcutionResultUtil userUpdate(User user,String img);
}
