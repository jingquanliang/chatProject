package websocket;


import dao.bean.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;

import java.io.UnsupportedEncodingException;

import java.util.LinkedList;
import java.util.Map;

import ru.own.www.smack.ReceiveMessage;

public class UserInterceptor extends GetXMPPConnForUser implements ChannelInterceptor {



    private Logger logger = LoggerFactory.getLogger(UserInterceptor.class);
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if(accessor != null && accessor.getCommand() !=null && accessor.getCommand().getMessageType() != null){
            if (StompCommand.CONNECT.equals(accessor.getCommand())) { //是connect请求

                String name = accessor.getFirstNativeHeader("name");
                String password = accessor.getFirstNativeHeader("password");
                if (StringUtils.isNotEmpty(name)) {

//                    Map sessionAttributes = SimpMessageHeaderAccessor.getSessionAttributes(message.getHeaders());
//                    sessionAttributes.put(CsrfToken.class.getName(), new DefaultCsrfToken("Auth-Token", "Auth-Token", name));
//                        UserAuthenticationToken authToken = tokenService.retrieveUserAuthToken(jwtToken);
//                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("22222webSocket token is :" + name);
                    Map sessionAttributes  = accessor.getSessionAttributes();
                    User user = new User();
                    user.setName(name);
                    accessor.setUser(user);
                    sessionAttributes.put(Constants.WEBSOCKET_USER_KEY,user);

                    MessageHanlder.UserPassword.put(name, password);//存放用户名和密码

                    accessor.setUser(user);//可以在类中用Principal类型接收

                    logger.info("in preSend--web socket connect success and usrnaeme:"+name);

                    Integer count= 0;
                    // 把用户访问页面的用户数量增加1
                    if(MessageHanlder.UserConnCount.containsKey(name)){
                        count =  MessageHanlder.UserConnCount.get(user.getName());
                        count=count+1;
                    }
                    else{
                        count++;
                    }

                    MessageHanlder.UserConnCount.put(name,count);
                }
            }
            else if(StompCommand.DISCONNECT.equals(accessor.getCommand()))
            {
                User user =null;
                user = (User)accessor.getSessionAttributes().get(Constants.WEBSOCKET_USER_KEY); //取出来这个user的信息
                if(user!=null){
                    logger.info("in preSend--web socket disconnect success and usrnaeme:"+user.getName());
//                simpHeartbeat shb= message.toString();
                    //经过我的测试，如果主动在客户端采用onclose方法关闭，服务端会收到两次DISCONNECT请求
                    //两次的区别是是否含有 simpHeartbeat 关键字
                    String  shb= message.toString();
                    logger.info("Message: message={}", message);
                    boolean flag= shb.contains("simpHeartbeat");
                    if(!flag){
                        if("admin".equals(user.getName())){
                            //发送客服下线消息
                            new Constants().setIsAdminOnline(0); //下线之后变为0
                            SenderMessageController sendController=new SenderMessageController();
                            sendController.adminOffLineMessage();
                        }

                        Integer count= MessageHanlder.UserConnCount.get(user.getName());
                        count=count-1;
                        logger.info("count is :"+count);
                        if(count==0){ //已经没有页面打开了
                            //停止openfire端的接收线程，不再接收数据并向该用户发送数据
                            ReceiveMessage receiveMessageThread = MessageHanlder.OpenfireReceiveThread.get(user.getName());
                            receiveMessageThread.setStopRunFlag(true);

                            if(MessageHanlder.XMPPConnections.containsKey(user.getName())){
                                ru.own.www.smack.GetXMPPConnection gx=MessageHanlder.XMPPConnections.get(user.getName());
                                if(null!=gx)
                                    gx.closeConnection();
                            }

                            logger.info("delete a conn frem  XMPPConnections,its key:"+user.getName());
                            MessageHanlder.XMPPConnections.remove(user.getName());//把连接删除
                            MessageHanlder.UserConnCount.remove(user.getName()); //把计数值清空
                        }
                        else{
                            MessageHanlder.UserConnCount.put(user.getName(),count);
                        }
                    }
                }


            }
        }
        return message;
    }



    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
//        logger.info("Inbound postSend. message={}", message);
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//        MessageHeaders header = message.getHeaders();
//        String sessionId = (String)header.get("simpSessionId");
//        if (accessor != null && accessor.getCommand() !=null && accessor.getCommand().getMessageType() != null) {
//            SimpMessageType type = accessor.getCommand().getMessageType();
//            if (accessor!= null && SimpMessageType.CONNECT.equals(type)) {
//                String jwtToken = accessor.getFirstNativeHeader("AuthToken");
//
//                if(StringUtils.isNotBlank(jwtToken)) {
//                    logger.info("Inbound preSend: sessionId={}, jwtToken={}", sessionId, jwtToken);
//                }
//                else {
//                    logger.error("no token, will be disallowed to connect.");
////                    return null;
//                }
//            }else if (type == SimpMessageType.DISCONNECT) {
//                logger.info("Inbound sessionId={} is disconnected", sessionId);
//            }else if (type == SimpMessageType.SUBSCRIBE) {
//                String topicDest = (String)header.get("simpDestination");
//                logger.info("subscribe topicDest={}, sessionId={} SUBSCRIBE", topicDest, sessionId);
//            } else if (type == SimpMessageType.MESSAGE) {
//                String topicDest = (String)header.get("simpDestination");
//                logger.info("之前的消息 topicDest={}, sessionId={} MESSAGE", topicDest, sessionId);
////                message = UpdateMessage(message, "Inbound");
////                logger.info("之后的消息e topicDest={}, message={} MESSAGE", topicDest, message);
//            }
//        }

    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
//        logger.info("Inbound afterSendCompletion. message={}", message);
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//        MessageHeaders header = message.getHeaders();
//        if (accessor != null && accessor.getCommand() !=null && accessor.getCommand().getMessageType() != null) {
//            SimpMessageType type = accessor.getCommand().getMessageType();
//            if (type == SimpMessageType.SUBSCRIBE) {
//                String topicDest = (String)header.get("simpDestination");
//                logger.info("afterSendCompletion. topicDest={}, message={} SUBSCRIBE", topicDest, message);
//
////                String payload = "{\"myfield1\":\"afterSendCompletion初始化消息\"}";
////                SimpMessagingTemplate messagingTemplate = SpringContextUtils.getBean(SimpMessagingTemplate.class);
////                messagingTemplate.convertAndSend(topicDest, payload);
////                logger.info("send complete. topic={}", topicDest);
//            }
//        }

    }

    @Override
    public boolean preReceive(MessageChannel channel) {
        return true;
    }

    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel channel) {
        return message;
    }

    @Override
    public void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {

    }
}

