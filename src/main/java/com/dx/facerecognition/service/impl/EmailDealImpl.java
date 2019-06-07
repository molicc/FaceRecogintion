package com.dx.facerecognition.service.impl;/**
 * Created by Administrator on 2019/6/6.
 *
 * @author Administrator
 */

import com.dx.facerecognition.service.EmailDeal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

/**
 * @ClassName EmailDealUtil
 * @Description TODO
 * @Autor Administrator
 * @Date 2019/6/6 11:32
 **/
@Service
public class EmailDealImpl implements EmailDeal {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment environment;

    @Autowired
    HttpServletRequest request;

    @Override
    public String sendMail(String email) {

        //生成并填充消息体
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        //发送邮箱
        mailMessage.setFrom(environment.getProperty("spring.mail.username"));
        //接收邮箱
        mailMessage.setTo(email);
        //邮件主题
        mailMessage.setSubject("风险登录安全校验");
        //随机验证码
        String verifyCode = verifyCode();
        //邮件正文
        mailMessage.setText("风险校验验证码为：" + verifyCode + "\n请注意有效期为10分钟");


        //发送邮件
        mailSender.send(mailMessage);

        request.getSession().setAttribute(email, verifyCode);

        return verifyCode;

    }

    public static String verifyCode() {
        String str = "";
        char[] ch = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            char num = ch[random.nextInt(ch.length)];
            str += num;
        }
        return str;
    }
}
