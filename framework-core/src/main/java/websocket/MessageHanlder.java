package websocket;

import java.util.concurrent.ConcurrentHashMap;
import ru.own.www.smack.GetXMPPConnection;

public class MessageHanlder {

    public static ConcurrentHashMap<String, String> UserPassword=new ConcurrentHashMap<String, String>(); //存放用户名和密码

    public static ConcurrentHashMap<String, Integer> UserConnCount=new ConcurrentHashMap<String, Integer>(); //存放用户连接数量

    //存放用户连接时，openfire端的接收数据线程
    public static ConcurrentHashMap<String, ru.own.www.smack.ReceiveMessage> OpenfireReceiveThread=new ConcurrentHashMap<String, ru.own.www.smack.ReceiveMessage>();

    public static ConcurrentHashMap<String, GetXMPPConnection> XMPPConnections=new ConcurrentHashMap<String, GetXMPPConnection>(); //用户和用户的openfire连接



    //key是用户的name，value是用户的消息，消息包括回执id 和 消息内容
    public static ConcurrentHashMap<String,ConcurrentHashMap<String,String> > userMessages = new  ConcurrentHashMap<>();
}
