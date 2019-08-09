package websocket;

import config.RedisUtils;
import dao.bean.User;
import dao.bean.UserMessage;
import hello.Greeting;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import ru.own.www.smack.SendMessage;
import smack.XMPPTCPOffLIne;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.security.Principal;

import ru.own.www.smack.GetXMPPConnection;
import ru.own.www.smack.SendMessage;
import ru.own.www.smack.ReceiveMessage;
import org.jivesoftware.smack.packet.Message;

import Utility.Decryption;

@Controller
//@EnableAutoConfiguration
@EnableScheduling //让方法自动执行
@EnableAsync // 两个自动执行的方法，异步执行
public class MessageController {

    private Logger logger = LoggerFactory.getLogger(UserInterceptor.class);

    @Autowired
    private SimpUserRegistry userRegistry;

    @Autowired
    private SimpMessagingTemplate template;

    @Resource
    private RedisUtils redisUtils;  // redis 的操作

    //接收到用户发送的信息，然后通过openfire发过去
    //connectOpenF指令中name属于指令，不需要passw信息，用户和密码信息在message中
    //其他命令中，name属于发送者，passw属于发送者的加了密的密码
    @MessageMapping("/foo/{name}/{passw}")
    // principal 可以获取用户信息
    public void receiveUserMessage(Principal principal, UserMessage message, @DestinationVariable String name, @DestinationVariable String passw) throws Exception {
//    public void receiveUserMessage(UserMessage message, @DestinationVariable String name) throws Exception {
//       logger.info("已经进入了websocket的函数，接收到用户id："+id);
//        logger.info("receive message:"+message.getContent());
//        logger.info("receive name from clinet:"+name);
//        logger.info("receive name from principal:"+principal.getName());

        if(name.equals("connectOpenF")){
            //这里的name代表的是指令，让后台打开openfire
            String pass= Decryption.parseTokenToPassword(message.getPassword());
            if("admin".equals(message.getContent())){
                //说明是后台上线了
                new Constants().setIsAdminOnline(1); //把标志位设为1
                UserMessage me=new UserMessage("1");
                this.template.convertAndSend("/topic/adminStatus",me); //给所有用户发送上线指令
            }
            else{//说明是普通用户上线，给用户发送一个上线的指令
                int flag=Constants.isAdminOnline;
                UserMessage me=new UserMessage(Integer.toString(flag));
//                logger.info("common user,the admin flag is:"+flag);
                this.template.convertAndSendToUser(passw,"/querey/adminStatus",me); //只是给这个用户发送用户是否在线的消息
            }
            new GetXMPPConnForUser().callForConn(message.getContent(),pass);
        }
        else{ // 让后台发送信息
            //        收到的消息返回一次，以便让收到消息的用户打开的所有页面同步消息
            replayMessageForClentTongbu(message,name);

            // 用openfire发送
            GetXMPPConnection xm=null;
            String pass= Decryption.parseTokenToPassword(passw);
           xm=new GetXMPPConnForUser().callForConn(name,pass); //这里的name代表的是发送信息的用户

            JSONObject contentJson=JSONObject.fromObject(message.getContent());
            String srcId=(String)contentJson.get("desId");
            if(srcId.contains("noLogin"))
                srcId="demo";
           xm.sendMessage(srcId,message.getContent());
       }

    }

    /**
     *
     * @param message
     * @param name, 消息的发送者，也即是说这个消息是谁发过来的
     */
    private void replayMessageForClentTongbu(UserMessage message, String name) {
//        logger.info("==================in replayMessageForClentTongbu============================");
        JSONObject contentJson=JSONObject.fromObject(message.getContent());
        String srcId=(String)contentJson.get("srcId");
        User user=new User();
        user.setName(srcId);

        Message msg = new Message();
        msg.setBody(message.getContent());
        msg.setFrom(srcId);

        String strArr=(String)contentJson.get("desId");

        msg.setTo(strArr);

        SenderMessageController sendController=new SenderMessageController();
        sendController.sendUserMessage(user,msg);
    }


    @MessageMapping("/cancel/{order}")
    // principal 可以获取用户信息
    public void cancelWebMessageTips(UserMessage message ,@DestinationVariable String order) throws Exception {
        logger.info("receive message from:"+ message.getFrom());
        String from = message.getFrom();
        if(order.equals("cancelMessageTips")){
            //取消前台新消息提示
            logger.info("返回了消息");
            this.template.convertAndSendToUser(from,"/cancel/MsgTips",message);
        }
    }


    @MessageMapping("/hello") //接收是hello
    @SendTo("/topic/greetings") //返回是针对订阅/topic/greetings的用户
    public Greeting greeting(UserMessage message) throws Exception {
       logger.info("已经进入了websocket的函数");
        Thread.sleep(1000); // simulated delay
       logger.info("已经返回应答");
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getContent()) + "!");
    }



    //广播推送消息，一对多，测试用
    @Scheduled(fixedRate = 50000)
    @Async
    public void sendTopicMessage() {
        logger.info("one to many send message！+++++++");
        Greeting ge=new Greeting("周期性消息");
        this.template.convertAndSend("/topic/getResponse",ge);
        //返回是针对订阅/topic/getResponse的用户
    }


    //一对一推送消息，测试用
//    @Scheduled(fixedRate = 10000)
//    @Async
    public void sendQueueMessage() {
       logger.info("one to one send message！+++++++");
        Greeting ge=new Greeting(1,"周期性消息");
        this.template.convertAndSendToUser(String.valueOf(ge.getId()),"/queue/getResponse",ge);
        //返回是针对ge.getId()的用户
    }
//    原文：https://blog.csdn.net/liyongzhi1992/article/details/81221103



}