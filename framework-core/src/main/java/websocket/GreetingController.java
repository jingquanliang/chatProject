package websocket;

import hello.Greeting;
import hello.HelloMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
//@EnableAutoConfiguration
@EnableScheduling //让方法自动执行
@EnableAsync // 两个自动执行的方法，异步执行
public class GreetingController {


    @MessageMapping("/hello") //接收是hello
    @SendTo("/topic/greetings") //返回是针对订阅/topic/greetings的用户
    public Greeting greeting(HelloMessage message) throws Exception {
        System.out.println("已经进入了websocket的函数");
        Thread.sleep(1000); // simulated delay
        System.out.println("已经返回应答");
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

    @Autowired
    private SimpMessagingTemplate template;

    //广播推送消息，一对多
    @Scheduled(fixedRate = 10000)
    @Async
    public void sendTopicMessage() {
        System.out.println("后台周期广播推送！");
        Greeting ge=new Greeting("周期性消息");
        this.template.convertAndSend("/topic/getResponse",ge);
        //返回是针对订阅/topic/getResponse的用户
    }


    //一对一推送消息
    @Scheduled(fixedRate = 10000)
    @Async
    public void sendQueueMessage() {
        System.out.println("后台一对一推送！");
        Greeting ge=new Greeting(1,"周期性消息");
        this.template.convertAndSendToUser(String.valueOf(ge.getId()),"/queue/getResponse",ge);
        //返回是针对ge.getId()的用户
    }
//    原文：https://blog.csdn.net/liyongzhi1992/article/details/81221103

}