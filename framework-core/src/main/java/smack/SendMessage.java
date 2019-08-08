package ru.own.www.smack;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.own.www.smack.GetXMPPConnection;

import java.util.Date;

import static java.lang.Thread.sleep;

/**
 * 该类是openfire的信息发送类
 */
public class SendMessage {

    private Logger logger = LoggerFactory.getLogger(SendMessage.class);

    GetXMPPConnection getConnInstance;
    Message messa;
    String toUser; //发送对象

    String deliveryReceiptId; //回执的id

    SendMessage(GetXMPPConnection connection, String to,String message){
        getConnInstance=connection;

        if (null != to && !"@".equals(to)) {
            toUser = to + "@" + getConnInstance.getConn().getServiceName();
        }
        else
            toUser = to;
        messa = new Message(toUser, message);
    }

    public void send() {
//        System.out.println("发送消息在SendMessages中");
        AbstractXMPPConnection conn = getConnInstance.getConn();
        // 发送消息
        boolean flag = conn.isConnected();  //代表是否连接
        while (!flag) {
            conn = getConnInstance.getConn();//不连接，再去获得一次
            flag = conn.isConnected();  //代表是否连接
        }
        Chat newChat = null;
        while (true) {
            requestReponse(conn); //添加回执的要求
            try {
                // 发送消息
                // Assume we've created an XMPPConnection name "connection"._
//                ChatManager chatmanager = ChatManager.getInstanceFor(conn);
//
//                newChat = chatmanager.createChat(toUser, new ChatMessageListener() {
//                    @Override
//                    public void processMessage(Chat chat, Message message) {
//                        System.out.println("in function sendMessage: Received message: " + message);
//                    }
//                });
//                newChat.sendMessage(messa); // 不知道为什么，这个不起作用
                conn.sendStanza(messa); // 这是官方要求回执的方法，目前测试的是不起作用
                logger.info("++++++++++++++++++++++++++openfire 发送了消息+++++++++++++++++++++++++++++++++++");
                System.out.println("to:"+messa.getTo()+"正确发送了消息:" + messa.getBody());
                break; //发送完毕之后，就不在循环发送了
            } catch (SmackException.NotConnectedException e) { //没有连接，则连接上再次发送
                System.out.println("发送之前重新连接");
                conn = getConnInstance.getConn();
                try {
                    sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                //e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
//            GetXMPPConnection.closeConnection(conn);
            }
        }
    }

    private void requestReponse(AbstractXMPPConnection conn) {

//        要求回执的代码
        //在发消息之前通过DeliveryReceiptManager订阅回执
        DeliveryReceiptManager ss = DeliveryReceiptManager.getInstanceFor(getConnInstance.getConn());
        ss.addReceiptReceivedListener(new ReceiptReceivedListener() {
            public void onReceiptReceived(String fromJid, String toJid, String receiptId, Stanza receipt) {
                // If the receiving entity does not support delivery receipts,
                // then the receipt received listener may not get invoked.
                logger.info("接收到回执了");
                logger.info((new Date()).toString() + " - drm:" + receipt.toXML());
            }
        });
//        ss.autoAddDeliveryReceiptRequests(); //这个好像是自动添加要求回执的要求，如果这个打开，那么下面这个addTo貌似就不用了，我没有测试
        //将消息放到DeliveryReceiptRequest中，这样就可以在发送Message后发送回执请求
        deliveryReceiptId = DeliveryReceiptRequest.addTo(messa);
        System.out.println("sendMessage: deliveryReceiptId for this message is: " + deliveryReceiptId);
    }
}
