package ru.own.www.smack;

import dao.bean.User;
import dao.bean.UserMessage;
import net.sf.json.JSONObject;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import  ru.own.www.smack.SendMessage;
import  ru.own.www.smack.ReceiveMessage;
import websocket.MessageController;
import websocket.MessageHanlder;
import websocket.SenderMessageController;
import websocket.UserInterceptor;

import java.io.IOException;
import java.util.Collection;

import static java.lang.Thread.sleep;

/**
 * @author GWCheng
 */
public class GetXMPPConnection {

    private Logger logger = LoggerFactory.getLogger(UserInterceptor.class);


    private AbstractXMPPConnection conn;

    ReceiveMessage receiveMessageThread;

    String username;
    String password;

    public GetXMPPConnection(String username, String password) {
        this.username = username;
        this.password = password;
//        connect();
    }



    public boolean connect() {

//        while(!buildAndConnect()){ //一直到连接成功为止
////            try {
////                sleep(2000);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
////        };
        boolean flag=buildAndConnect(); //是否连接成功
        createFilter(); // 在这类好用，不知道放在登录之后是否好用，过滤器暂时不用，作用是对接收到的包进行过滤等操作
        RosterListener();  //必须要登录之前设置
        boolean loginFlag=login(username, password); //是否登录成功
//        getAllFriends();

        readOfflineMsg();  //读取离线消息
        setStatus();  //设置状态

        receiveMessage();
        return true;
    }

    private boolean buildAndConnect() {
        try {
            // Create a connection to the jabber.org server on a specific port.
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
//                .setUsernameAndPassword("demo", "qazwsx")
                    .setServiceName("999own.com")
                    .setHost("999own.com")
                    .setPort(5222)
                    .setConnectTimeout(20000)//设置连接超时时间
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled).setCompressionEnabled(false)//设置是否启用安全连接
                    .setSendPresence(false) //设置离线状态
//                    .setDebuggerEnabled(true)
                    .build();

            //需要经过同意才可以添加好友，如果打开，需要有一个监听器
//            Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);

//            Log.d(TAG, "Setting TLS Certificates Settings for XMPPTCPConnectionConfiguration Object ");
            //  TLSUtils.acceptAllCertificates(builder);

//            Log.d(TAG, "Setting XMPPTCPConnection Static StreamManagement Default Properties to true ");
            //流管理机制，不过我自己也添加了回执，不知道这个流管理是做什么的，反正就打开吧
            XMPPTCPConnection.setUseStreamManagementDefault(true);
            XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);

//            Log.d(TAG, "Setting SASLAuthentication Blacklist");

            SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
//            SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
//            SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");


            conn = new XMPPTCPConnection(config);//根据配置生成一个连接
//            ((XMPPTCPConnection)conn).setUseStreamManagementResumption(true); //这个是流管理器，还不知具体怎么样用

            conn.addConnectionListener(new ConnectionListener() {
                @Override
                public void connected(XMPPConnection connection) {
                    //已连接上服务器
                    System.out.println("已经连接上服务器!恭喜！");
                }

                @Override
                public void authenticated(XMPPConnection connection, boolean resumed) {
                    //已认证
                    System.out.println("认证成功！");
                }

                @Override
                public void connectionClosed() {
                    //连接已关闭
                    System.out.println("连接已关闭");
                }

                public void connectionClosedOnError(Exception e) {
                    //关闭连接发生错误
                    System.out.println("关闭连接发生错误");
                }

                public void reconnectionSuccessful() {
                    //重连成功
                    System.out.println("重连成功");
                }


                public void reconnectingIn(int seconds) {
                    //重连中
                    System.out.println("重连中");
                }


                public void reconnectionFailed(Exception e) {
                    //重连失败
                    System.out.println("重连失败");
                }
            });
            conn.connect(); // 登录，上边的username和password分别为用户名和密码
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 只对接收起作用，比如可以只针对某个用户的包
    private void createFilter() {
        // Create a packet filter to listen for new messages from a particular
        // user. We use an AndFilter to combine two other filters._
        PacketFilter packetfilter = new PacketFilter() {
            public boolean accept(Stanza packet) {
                return true;  //对所有的包都接收
                //因为下面有了一些强制类型的转换，所有会一起一些错误
                //java.lang.ClassCastException: org.jivesoftware.smack.packet.Bind cannot be cast to org.jivesoftware.smack.packet.Message
            }
        };
        // 包的过滤器
        PacketTypeFilter filterMessage = new PacketTypeFilter(Message.class);

        // 创建包的监听器
        PacketListener myListener = new PacketListener() {
            public void processPacket(Stanza packet) {
                // 以XML格式输出接收到的消息
//                System.out.println("在过滤器里面执行");

                // 监听消息，在检查到对方要求回执时，客户端手动发送回执给对方
                if (packet instanceof Message) {
                    System.out.println("Body: " + packet.getFrom());
                    Message message = (Message) packet;
                    DeliveryReceiptRequest receipt = message.getExtension(DeliveryReceiptRequest.ELEMENT, DeliveryReceipt.NAMESPACE);
                    if (receipt != null) {
                        Message receiptMessage = new Message();
                        receiptMessage.setTo(message.getFrom());
                        receiptMessage.setFrom(message.getTo());
                        receiptMessage.addExtension(new DeliveryReceipt(message.getPacketID()));
                        try {
                            System.out.println("回执 To：" + message.getFrom());
                            System.out.println("回执 from：" + message.getTo());
                            conn.sendPacket(receiptMessage);
//                            conn.sendStanza(receiptMessage);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        };
        // 给连接注册一个包的监听器
        conn.addPacketListener(myListener, packetfilter);

    }

    private void RosterListener() {
        Roster roster = Roster.getInstanceFor(conn);
        roster.addRosterListener(new RosterListener() {
            public void entriesAdded(Collection<String> addresses) {
                System.out.println("entries Added");
            }

            public void entriesDeleted(Collection<String> addresses) {
                System.out.println("entries Deleted");
            }

            public void entriesUpdated(Collection<String> addresses) {
                System.out.println("entries Updated");
            }

            public void presenceChanged(Presence presence) {
                System.out.println("Presence changed: " + presence.getFrom() + " " + presence);
            }
        });
    }

    public boolean login(String username, String password) {
        try {
            if (conn != null) {
                conn.login(username, password);
                System.out.println("user " + username + " login successfully.");
                return true;
            }
            return  false;
        } catch (XMPPException e) {
            e.printStackTrace();
            return false;
        } catch (SmackException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void readOfflineMsg() {
        OfflineMessageManager offlineManager = new OfflineMessageManager(conn);
        java.util.Iterator<Message> it = null;
        try {
            it = offlineManager.getMessages().iterator();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        while (it.hasNext()) {
            org.jivesoftware.smack.packet.Message message = it.next();
            System.out.println("收到离线消息, Received from 【" + message.getFrom() + "】 message: " + message.getBody());
            //把消息推到前台
            if (null != message.getBody()) {
                //这个地方应该通过websocket推到前台
                JSONObject contentJson=JSONObject.fromObject(message.getBody());
                String desId=(String)contentJson.get("desId");

                User user=new User();
                user.setName(desId);
//                UserMessage tempmessage = new UserMessage(message.getBody());
                SenderMessageController sendController=new SenderMessageController();
                sendController.sendUserMessage(user,message);

            } else {
//                                System.out.println("接收到新消息：内容为空");
            }

        }
        //删除离线消息
        try {
            offlineManager.deleteMessages();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        //将状态设置成在线
        Presence presence;
        presence = new Presence(Presence.Type.available);
        try {
            conn.sendPacket(presence);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    private void setStatus() {

        //设置成在线，这里如果改成unavailable则会显示用户不在线
        Presence presence = new Presence(Presence.Type.available);
        presence.setStatus("在线");

        try {
            conn.sendStanza(presence);//发送Presence包
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void closeConnection() {
        if (conn != null) {
            try {
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    //得到的时候可以确保连接
    public AbstractXMPPConnection getConn() {
        boolean flag = conn.isConnected();  //代表是否连接
        logger.info("conn flag:" + flag);
        while (!flag) {
            logger.info("need to reconnect");
            connect();
            flag = conn.isConnected();  //代表是否连接
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }


    public void sendMessage(String toUser, String content) {
//        System.out.println("在getXMPPconnection中发送");
        SendMessage sm= new SendMessage(this, toUser,content); //发送消息
        sm.send();
    }

    private void receiveMessage() {
        receiveMessageThread = new ReceiveMessage(conn, new SenderMessageController());
        MessageHanlder.OpenfireReceiveThread.put(username,receiveMessageThread);
//        MessageHanlder.OpenfireReceiveThread.get(user.getName());
        receiveMessageThread.start();
    }
}
