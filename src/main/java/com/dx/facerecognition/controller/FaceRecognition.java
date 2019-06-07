package com.dx.facerecognition.controller;/**
 * Created by Administrator on 2019/5/2.
 *
 * @author Administrator
 */

import com.dx.facerecognition.entity.User;
import com.dx.facerecognition.service.EmailDeal;
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
    @Autowired
    EmailDeal emailDeal;

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
            String verification = request.getParameter("verification").replaceAll("\\s*", "");
            if (!request.getSession().getAttribute(user.getEmail()).equals(verification)) {
                //验证码不匹配
                modelMap.put("success", false);
                modelMap.put("msg", "验证码错误!");
                return modelMap;
            } else {
                //解除风险控制并调用更新模块
                request.getSession().setAttribute("loginFailTimes", 0);
                ExcutionResultUtil userUpdate = userDeal.userUpdate(user, img);
                if (userUpdate.isSuccess()) {
                    //更新成功
                    modelMap.put("success", true);
                    modelMap.put("msg", "更新人脸成功!");
                    return modelMap;
                } else {
                    //更新失败
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
            modelMap.put("msg", user.getEmail());
            emailDeal.sendMail(user.getEmail());
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
        String email = request.getParameter("email").replaceAll("\\s*", "");
        String verification = request.getParameter("verification").replaceAll("\\s*", "");
        String img = request.getParameter("img");
        if (username == null || "".equals(username)) {
            modelMap.put("success", false);
            modelMap.put("msg", "请输入用户名!");
            return modelMap;
        }
        if (email == null || "".equals(email)) {
            modelMap.put("success", false);
            modelMap.put("msg", "请输入安全邮箱!");
            return modelMap;
        }
        if (verification == null || "".equals(verification)) {
            modelMap.put("success", false);
            modelMap.put("msg", "请输入验证码!");
            return modelMap;
        }

        //检测验证码是否正确
        String checkVer = (String) request.getSession().getAttribute(email);
        if (!verification.equals(checkVer)){
            modelMap.put("success", false);
            modelMap.put("msg","验证码错误，请核对！");
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
        User user = new User(username, 1, email);
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


    @PostMapping("/sendEmailReq")
    @ResponseBody
    public Map<String, Object> sendEmail(HttpServletRequest request, HttpServletResponse response) {
        HashMap<String, Object> modelMap = new HashMap<>();
        String email = request.getParameter("email").replaceAll("\\s*", "");
        if (email == null || "".equals(email)) {
            modelMap.put("success", false);
            modelMap.put("msg", "请输入安全邮箱!");
            return modelMap;
        }

        String verification = emailDeal.sendMail(email);

        if (verification == null) {
            modelMap.put("success", false);
            modelMap.put("msg", "发送失败，请稍后再试");
            return modelMap;
        }


        modelMap.put("success", true);
        modelMap.put("msg", "发送成功，请查看邮件");
        return modelMap;
    }


}
