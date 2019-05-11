package com.dx.facerecognition.service;/**
 * Created by Administrator on 2019/5/3.
 *
 * @author Administrator
 */

import com.dx.facerecognition.util.ExcutionResultUtil;

/**
 * @ClassName Pretreat
 * @Description 预处理接口
 * @Autor Administrator
 * @Date 2019/5/3 17:03
 **/
public interface Pretreat {


    /**
     * 对图片进行预处理
     *
     * @param img
     * @return
     */
    ExcutionResultUtil pretreatImg(String img, String username);
}
