package websocket;


import dao.bean.User;
import dao.bean.UserMessage;
import net.sf.json.JSONObject;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

//@Controller
//@EnableScheduling //让方法自动执行
//@EnableAsync // 两个自动执行的方法，异步执行

/**
 * 该类是websocket的信息发送类
 */
public class SenderMessageController {

    private Logger logger = LoggerFactory.getLogger(SenderMessageController.class);
//    @Autowired
    private SimpMessagingTemplate template=SpringContextUtils.getBean(SimpMessagingTemplate.class);

    //openfire接收到信息之后，
    // 这里的user代表接收者
    public void sendUserMessage(User user, Message msg){

//        logger.info("==================in SenderMessageController============================");

        UserMessage message = new UserMessage(msg.getBody());
        message.setFrom(msg.getFrom());
        message.setTo(msg.getTo());

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String tsStr = "";
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            //方法一
            tsStr = sdf.format(ts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        message.setTime(tsStr);

        try {
//            String domainUserName=user.getName();
//            String[] strArr = domainUserName.split("@");
//            JSONObject contentJson=JSONObject.fromObject(msg.getBody());
//            String strArr=(String)contentJson.get("desId");
//            logger.info("已经进入了websocket的函数，返回给用户："+user.getName());
            this.template.convertAndSendToUser(user.getName(),"/queue/getResponse",message);
            System.out.println("传送的消息为："+message.getContent());
            //返回是针对ge.getId()的用户
        }catch (Exception e){

            e.printStackTrace();
        }

    }
}
