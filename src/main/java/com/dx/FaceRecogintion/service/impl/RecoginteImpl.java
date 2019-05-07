package com.dx.FaceRecogintion.service.impl;/**
 * Created by Administrator on 2019/5/5.
 *
 * @author Administrator
 */

import com.dx.FaceRecogintion.service.Pretreat;
import com.dx.FaceRecogintion.service.Recognit;
import com.dx.FaceRecogintion.util.ContextPropertiesUtil;
import com.dx.FaceRecogintion.util.ExcutionResultUtil;
import com.dx.FaceRecogintion.util.PathUtil;
import org.bytedeco.opencv.opencv_core.CvHistogram;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.File;

import static org.bytedeco.opencv.global.opencv_core.CV_HIST_ARRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.opencv.helper.opencv_imgproc.cvCalcHist;

/**
 * @ClassName RecoginteImpl
 * @Description 识别实现类
 * @Autor Administrator
 * @Date 2019/5/5 22:10
 **/
@Service
@EnableConfigurationProperties({ContextPropertiesUtil.class})
public class RecoginteImpl implements Recognit {
    @Autowired
    Pretreat pretreat;
    @Autowired
    ContextPropertiesUtil contextPropertiesUtil;

    @Override
    public ExcutionResultUtil compare(String img, String username) {
        //判断用户是否存在
        if (!new File(contextPropertiesUtil.getFacesPath() + username).exists()) {
            return new ExcutionResultUtil(false, "用户名不存在，请先注册");
        }
        //对图片进行预处理
        ExcutionResultUtil pretreatImg = pretreat.pretreatImg(img, username + "compare");
        if (!pretreatImg.isSuccess()) {
            //预处理失败则返回
            return new ExcutionResultUtil(false, pretreatImg.getMsg());
        }
        //成功则进行比较
        try {
            //CV_LOAD_IMAGE_GRAYSCALE = 0 ,将指定图像处理为灰度图
            IplImage image1 = cvLoadImage(contextPropertiesUtil.getFacesPath() + username + "\\face.jpg", 0);
            IplImage image2 = cvLoadImage(contextPropertiesUtil.getFacesPath() + username + "compare\\face.jpg", 0);
            if (null == image1 || null == image2) {
                return new ExcutionResultUtil(false, "比较对象为空");
            }

            int l_bins = 256;
            int hist_size[] = {l_bins};
            float v_ranges[] = {0, 255};
            float ranges[][] = {v_ranges};

            IplImage imageArr1[] = {image1};
            IplImage imageArr2[] = {image2};
            //创建直方图 https://blog.csdn.net/t1234xy4/article/details/51713895
            CvHistogram Histogram1 = CvHistogram.create(1, hist_size, CV_HIST_ARRAY, ranges, 1);
            CvHistogram Histogram2 = CvHistogram.create(1, hist_size, CV_HIST_ARRAY, ranges, 1);
            //计算图片的直方图
            cvCalcHist(imageArr1, Histogram1, 0, null);
            cvCalcHist(imageArr2, Histogram2, 0, null);
            //通过缩放来归一化直方块，使得所有块的和等于 factor.
            cvNormalizeHist(Histogram1, 100.0);
            cvNormalizeHist(Histogram2, 100.0);
            // 参考：http://blog.csdn.net/nicebooks/article/details/8175002
            double c1 = cvCompareHist(Histogram1, Histogram2, CV_COMP_CORREL) * 100;
            double c2 = cvCompareHist(Histogram1, Histogram2, CV_COMP_INTERSECT);
            if ((c1 + c2) / 2 > 80) {
                return new ExcutionResultUtil(true, "匹配度高");
            } else {
                return new ExcutionResultUtil(false, "匹配度低,请重新登录");
            }
        } catch (Exception e) {
            return new ExcutionResultUtil(false, "比较失败" + e.getMessage());
        } finally {
            //将比对图片删除
            PathUtil.deleteDirPath(contextPropertiesUtil.getImagePath() + username + "compare");
            PathUtil.deleteDirPath(contextPropertiesUtil.getFacesPath() + username + "compare");
        }
    }

}
