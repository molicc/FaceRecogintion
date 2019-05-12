package com.dx.facerecognition.entity;/**
 * Created by Administrator on 2019/5/8.
 *
 * @author Administrator
 */

/**
 * @ClassName User
 * @Description 用户实体
 * @Autor Administrator
 * @Date 2019/5/8 23:03
 **/
public class User {
    private int id;
    private int imageSize;
    private String username;
    private String question;
    private String answer;


    public User(String username, int imageSize, String question, String answer) {
        this.imageSize = imageSize;
        this.username = username;
        this.question = question;
        this.answer = answer;
    }

    public User() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImageSize() {
        return imageSize;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", imageSize='" + imageSize + '\'' +
                ", username='" + username + '\'' +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
