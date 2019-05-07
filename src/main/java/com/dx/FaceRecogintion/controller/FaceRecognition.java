package com.dx.FaceRecogintion.controller;/**
 * Created by Administrator on 2019/5/2.
 *
 * @author Administrator
 */

import com.dx.FaceRecogintion.service.Pretreat;
import com.dx.FaceRecogintion.service.Recognit;
import com.dx.FaceRecogintion.util.ContextPropertiesUtil;
import com.dx.FaceRecogintion.util.ExcutionResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName FaceRecogintion
 * @Description 人脸识别Controller
 * @Autor Administrator
 * @Date 2019/5/2 21:39
 **/
@Controller
public class FaceRecognition {
    @Autowired
    private Pretreat pretreat;
    @Autowired
    private Recognit recognit;
    @Autowired
    ContextPropertiesUtil contextPropertiesUtil;

    @PostMapping("/loginReq")
    @ResponseBody
    public Map<String, Object> login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HashMap<String, Object> modelMap = new HashMap<>();
        String username = request.getParameter("username");
        String img = request.getParameter("img");
        if (username == null || "".equals(username)) {
            modelMap.put("success", false);
            modelMap.put("msg", "请输入用户名!");
            return modelMap;
        }

        ExcutionResultUtil compare = recognit.compare(img,username);
        if (compare.isSuccess()) {
            modelMap.put("success", true);
            modelMap.put("msg", "登录成功!");
            return modelMap;
        } else {
            modelMap.put("success", false);
            modelMap.put("msg", compare.getMsg());
            return modelMap;
        }
    }


    @PostMapping("/registerReq")
    @ResponseBody
    public Map<String, Object> register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HashMap<String, Object> modelMap = new HashMap<>();
        String username = request.getParameter("username");
        String img = request.getParameter("img");
        if (username == null || "".equals(username)) {
            modelMap.put("success", false);
            modelMap.put("msg", "请输入用户名!");
            return modelMap;
        }
        //对图片进行预处理
        ExcutionResultUtil pretreatImg = pretreat.pretreatImg(img, username);

        if (pretreatImg.isSuccess()) {
            modelMap.put("success", true);
            modelMap.put("msg", "注册成功，请登录!");
            return modelMap;
        } else {
            modelMap.put("success", false);
            modelMap.put("msg", pretreatImg.getMsg());
            return modelMap;
        }


    }
}
