package com.dx.facerecognition.controller;/**
 * Created by Administrator on 2019/5/2.
 *
 * @author Administrator
 */

import com.dx.facerecognition.entity.User;
import com.dx.facerecognition.service.Pretreat;
import com.dx.facerecognition.service.UserDeal;
import com.dx.facerecognition.util.ContextPropertiesUtil;
import com.dx.facerecognition.util.ExcutionResultUtil;
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
 * @ClassName FaceRecognition
 * @Description 人脸识别Controller
 * @Autor Administrator
 * @Date 2019/5/2 21:39
 **/
@Controller
public class FaceRecognition {
    @Autowired
    private Pretreat pretreat;
    @Autowired
    private UserDeal userDeal;
    @Autowired
    ContextPropertiesUtil contextPropertiesUtil;

    @PostMapping("/loginReq")
    @ResponseBody
    public Map<String, Object> login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HashMap<String, Object> modelMap = new HashMap<>();
        String username = request.getParameter("username").replaceAll("\\s*", "");
        boolean risklogin = Boolean.parseBoolean(request.getParameter("risklogin").replaceAll("\\s*", ""));
        String img = request.getParameter("img");
        if (username == null || "".equals(username)) {
            modelMap.put("success", false);
            modelMap.put("msg", "请输入用户名!");
            return modelMap;
        }
        User user = userDeal.getUserInfo(username);
        //检测用户是否存在
        if (user == null) {
            modelMap.put("success", false);
            modelMap.put("msg", "该用户不存在！");
            return modelMap;
        }
        //风险登录
        if (risklogin == true) {
            String answer = request.getParameter("answer").replaceAll("\\s*", "");
            if (!user.getAnswer().equals(answer)) {
                //答案不匹配
                modelMap.put("success", false);
                modelMap.put("msg", "答案错误!");
                return modelMap;
            } else {
                //解除风险控制
                request.getSession().setAttribute("loginFailTimes", 0);
                ExcutionResultUtil userUpdate = userDeal.userUpdate(user, img);
                if (userUpdate.isSuccess()) {
                    //答案不匹配
                    modelMap.put("success", true);
                    modelMap.put("msg", "更新人脸成功!");
                    return modelMap;
                } else {
                    //答案不匹配
                    modelMap.put("success", false);
                    modelMap.put("msg", userUpdate.getMsg());
                    return modelMap;
                }
            }
        }


        //用户存在时获取风险信息
        Integer loginFailTimes = (Integer) request.getSession().getAttribute("loginFailTimes");
        if (loginFailTimes == null) {
            loginFailTimes = 0;
            request.getSession().setAttribute("loginFailTimes", loginFailTimes);
        } else if (loginFailTimes >= 3) {
            modelMap.put("success", false);
            modelMap.put("risklogin", true);
            modelMap.put("msg", user.getQuestion());
            return modelMap;
        }

        //进行人脸校验
        ExcutionResultUtil userRecognit = userDeal.userRecognit(img, user.getId());
        if (userRecognit.isSuccess()) {
            request.getSession().setAttribute("loginFailTimes", 0);
            modelMap.put("success", true);
            modelMap.put("msg", "登录成功!");
            return modelMap;
        } else {
            //校验失败则增加一次风险记录
            request.getSession().setAttribute("loginFailTimes", ++loginFailTimes);
            modelMap.put("success", false);
            modelMap.put("msg", userRecognit.getMsg());
            return modelMap;
        }

    }


    @PostMapping("/registerReq")
    @ResponseBody
    public Map<String, Object> register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HashMap<String, Object> modelMap = new HashMap<>();
        String username = request.getParameter("username").replaceAll("\\s*", "");
        String question = request.getParameter("question").replaceAll("\\s*", "");
        String answer = request.getParameter("answer").replaceAll("\\s*", "");
        String img = request.getParameter("img");
        if (username == null || "".equals(username)) {
            modelMap.put("success", false);
            modelMap.put("msg", "请输入用户名!");
            return modelMap;
        }
        if (question == null || "".equals(question)) {
            modelMap.put("success", false);
            modelMap.put("msg", "请输入安全问题!");
            return modelMap;
        }
        if (answer == null || "".equals(answer)) {
            modelMap.put("success", false);
            modelMap.put("msg", "请输入问题答案!");
            return modelMap;
        }

        //检测用户名是否已经被占用
        ExcutionResultUtil checkUserexits = userDeal.checkUserexits(username);
        if (!checkUserexits.isSuccess()) {
            modelMap.put("success", false);
            modelMap.put("msg", checkUserexits.getMsg());
            return modelMap;
        }

        //将用户信息进行持久化
        User user = new User(username, 1, question, answer);
        ExcutionResultUtil userRegister = userDeal.userRegister(user, img);

        if (userRegister.isSuccess()) {
            modelMap.put("success", true);
            modelMap.put("msg", "注册成功，请登录!");
            return modelMap;
        } else {
            modelMap.put("success", false);
            modelMap.put("msg", userRegister.getMsg());
            return modelMap;
        }


    }


}
