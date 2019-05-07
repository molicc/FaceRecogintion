package com.dx.FaceRecogintion.util;/**
 * Created by Administrator on 2019/5/3.
 *
 * @author Administrator
 */

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName ContextPropertiesUtil
 * @Description 配置参数
 * @Autor Administrator
 * @Date 2019/5/3 21:43
 **/
@ConfigurationProperties(prefix = "save")
public class ContextPropertiesUtil {
    private String imagePath;
    private String facesPath;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getFacesPath() {
        return facesPath;
    }

    public void setFacesPath(String facesPath) {
        this.facesPath = facesPath;
    }
}
