package ru.own.www.smack;

import dao.bean.User;
import dao.bean.UserMessage;
import junit.framework.TestCase;
import net.sf.json.JSONObject;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import websocket.MessageController;
import websocket.SenderMessageController;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static java.lang.Thread.sleep;

/**
 * @author GWCheng
 */
public class ReceiveMessage extends Thread{

    private Logger logger = LoggerFactory.getLogger(ReceiveMessage.class);

    AbstractXMPPConnection conn;
    SenderMessageController sendController;

    boolean isStopRunFlag=false; //是否停止的标志

    ReceiveMessage(AbstractXMPPConnection conn, SenderMessageController sendController) {
        this.conn=conn;
        this.sendController=sendController;
    }


    public void run() {
        try {
            ChatManager chatmanager = ChatManager.getInstanceFor(conn);
            System.out.println("等待接受消息...");

            chatmanager.addChatListener(new ChatManagerListener() {
                @Override
                public void chatCreated(Chat chat, boolean createdLocally) {
                    if (!createdLocally) {
//                        System.out.println("新消息不是本地的");
                    }
                    chat.addMessageListener(new ChatMessageListener() {
                        @Override
                        public void processMessage(Chat chat, Message msg) {
                            if (null != msg.getBody()) {
                                logger.info("==================in ReceiveMessage============================");
                                System.out.println("+++++++++++++++++++++"+(msg.getFrom() + "-->" + msg.getTo() + "\n" + msg.getBody()));
                                //这个地方应该通过websocket推到前台
                                JSONObject contentJson=JSONObject.fromObject(msg.getBody());
                                String desId=(String)contentJson.get("desId");

                                User user=new User();
//                                if(desId.contains("noLogin"))
//                                    user.setName("demo");
//                                else
                                    user.setName(desId);
                                sendController.sendUserMessage(user,msg);  //通过websocket发送

                            } else {
//                                System.out.println("接收到新消息：内容为空");
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (!isStopRunFlag) {
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(" i am ding in the receive message!!!!!!!!!!!!!!!!!!!!!!!");
    }

    public boolean isStopRunFlag() {
        return isStopRunFlag;
    }

    public void setStopRunFlag(boolean stopRunFlag) {
        isStopRunFlag = stopRunFlag;
    }


}
