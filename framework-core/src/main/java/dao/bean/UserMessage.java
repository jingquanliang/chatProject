package dao.bean;

import  java.sql.Timestamp;

public class UserMessage {

    String from;
    String to;

    private String content;
    String password; //用户的token，这个token是经过实际处理之后，可以转为真正的密码



    String time;



    public UserMessage() {
    }

    public UserMessage(String content) {
        this.content = content;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFrom() {
        return from;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
