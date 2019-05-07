package com.dx.FaceRecogintion.service.impl;/**
 * Created by Administrator on 2019/5/3.
 *
 * @author Administrator
 */

import com.dx.FaceRecogintion.service.Pretreat;
import com.dx.FaceRecogintion.util.ContextPropertiesUtil;
import com.dx.FaceRecogintion.util.ExcutionResultUtil;
import com.dx.FaceRecogintion.util.PathUtil;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.equalizeHist;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;

/**
 * @ClassName PretreatImpl
 * @Description 预处理实现类
 * @Autor Administrator
 * @Date 2019/5/3 17:04
 **/
@Service
@EnableConfigurationProperties({ContextPropertiesUtil.class})
public class PretreatImpl implements Pretreat {
    @Autowired
    private ContextPropertiesUtil contextProperties;

    //用于存放检测出人脸的区域
    RectVector faces = new RectVector();

    /**
     * 通过图片路径识别人脸
     *
     * @param path
     * @return
     */
    private ExcutionResultUtil detectFaceByPath(String path) {

        //以8位单通道灰度的形式读入指定图片
        Mat image = imread(path, 0);
        //对图片做均衡化处理
        equalizeHist(image, image);
        RectVector facestemp = new RectVector();

        //创建分类器
        CascadeClassifier faceClassifier = new CascadeClassifier(System.getProperty("user.dir") +
                "\\src\\main\\resources\\classifiler\\lbpcascade_frontalface.xml");
        CascadeClassifier eyeClassifier = new CascadeClassifier(System.getProperty("user.dir")+
                "\\src\\main\\resources\\classifiler\\haarcascade_eye_tree_eyeglasses.xml");
        //从照片中检测人脸区域
        faceClassifier.detectMultiScale(image, facestemp, 1.1, 3, IMREAD_GRAYSCALE, new Size(30, 30), new Size(0,
                0));

        //遍历检测出来的人脸
        for (int i = 0; i < facestemp.size(); i++) {
            //截取当前人脸
            Mat face = image.apply(facestemp.get(i));
            RectVector eye =new RectVector();
            eyeClassifier.detectMultiScale(face,eye,1.1, 1, IMREAD_GRAYSCALE, new Size(3, 3),new Size(0,0));
            if (eye.size()==2){
                faces.put(facestemp.get(i));
            }
        }

        if (faces.size() <= 0) {
            return new ExcutionResultUtil(false, "没有检测到人脸，请重新拍摄");
        } else if (faces.size() > 1) {
            return new ExcutionResultUtil(false, "检测到多个人脸，请重新拍摄");
        }
        return new ExcutionResultUtil(true, "检测人脸成功");

    }

    /**
     * 裁剪人脸
     *
     * @param imagePath
     * @param username
     * @return
     */
    private ExcutionResultUtil cutFace(String imagePath, String username) {
        //人脸存储路径
        String dirPath = contextProperties.getFacesPath() + username;
        //生成指定目录
        ExcutionResultUtil makeDirPath = PathUtil.makeDirPath(dirPath);
        if (!makeDirPath.isSuccess()) {
            return makeDirPath;
        }

        try {
            //获取源图片
            Mat image = imread(imagePath, 0);
            //获取当前人脸位置信息
            Rect rect = faces.get(0);
            //截取当前人脸
            Mat face = image.apply(rect);
            //接收大小规格化后的人脸
            Mat mat = new Mat();
            //指定规格化大小
            Size size = new Size(100, 100);
            //规格化
            resize(face, mat, size);
            //保存到指定路径
            imwrite(dirPath + "\\face.jpg", mat);
            return new ExcutionResultUtil(true, "截取人脸成功");
        } catch (Exception e) {
            return new ExcutionResultUtil(false, "截取人脸失败:" + e.getMessage());
        }


    }
    /**
     * 存储图片
     *
     * @param username
     * @param img
     * @return 保存成功则返回存储路径，保存失败则返回失败信息
     */
    private ExcutionResultUtil saveImg(String username, String img) {
        //用base64替代Base64Decoder
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] bytes = decoder.decode(img);
        FileOutputStream fileOutputStream = null;
        //根据用户名生成保存图片的文件夹路径
        String dirPath = contextProperties.getImagePath() + username;
        //生成指定文件夹
        ExcutionResultUtil makeDirPath = PathUtil.makeDirPath(dirPath);
        if (!makeDirPath.isSuccess()) {
            //当用户名已存在则返回
            return makeDirPath;
        }
        //图片存储路径
        String imagePath = dirPath + "\\image.jpg";
        try {
            //当为新用户则保存当前图片
            fileOutputStream = new FileOutputStream(imagePath);
            fileOutputStream.write(bytes);
            return new ExcutionResultUtil(true, imagePath);

        } catch (IOException e) {
            return new ExcutionResultUtil(false, "保存图片失败");
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return new ExcutionResultUtil(false, "保存图片失败");
            }
        }
    }

    @Override
    public ExcutionResultUtil pretreatImg(String img, String username) {
        //保存图片
        ExcutionResultUtil saveImg = saveImg(username, img);
        if (saveImg.isSuccess()) {

            //保存成功则进行预处理

            //获取图片保存路径
            String imagePath = saveImg.getMsg();

            //检测出人脸区域并保存至RectVector中
            ExcutionResultUtil detectFaceByPath = detectFaceByPath(imagePath);
            if (!detectFaceByPath.isSuccess()) {
                //如果检测人脸失败需要删除文件夹
                ExcutionResultUtil delectDirPath = PathUtil.deleteDirPath(contextProperties.getImagePath() + username);
                if (delectDirPath.isSuccess()) {
                    //删除成功则返回裁剪失败原因
                    return new ExcutionResultUtil(false, detectFaceByPath.getMsg());
                } else {
                    //删除失败则返回删除失败原因
                    return new ExcutionResultUtil(false, detectFaceByPath.getMsg() + delectDirPath.getMsg());
                }
            }


            //裁剪人脸
            ExcutionResultUtil cutFace = cutFace(imagePath, username);
            if (cutFace.isSuccess()) {
                //人脸裁剪成功
                return new ExcutionResultUtil(true, cutFace.getMsg());
            } else {

                //失败则需要删除已经保存的图片路径
                ExcutionResultUtil delectDirPath = PathUtil.deleteDirPath(contextProperties.getImagePath() + username);
                if (delectDirPath.isSuccess()) {
                    //删除成功则返回裁剪失败原因
                    return new ExcutionResultUtil(false, cutFace.getMsg());
                } else {
                    //删除失败则返回删除失败原因
                    return new ExcutionResultUtil(false, cutFace.getMsg() + delectDirPath.getMsg());
                }
            }

        } else {
            //保存失败
            return new ExcutionResultUtil(false, saveImg.getMsg());
        }


    }

}
