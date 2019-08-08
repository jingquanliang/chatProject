package websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetXMPPConnForUser {

    private Logger logger = LoggerFactory.getLogger(GetXMPPConnForUser.class);

    public ru.own.www.smack.GetXMPPConnection callForConn(String name,String password) {
        ru.own.www.smack.GetXMPPConnection xm=null;
        xm=(ru.own.www.smack.GetXMPPConnection)MessageHanlder.XMPPConnections.get(name);
//        logger.info("get GetXMPPConnection："+xm);
        if(xm==null) { // 说明没有创建过
            logger.info("create GetXMPPConnection");
//            if(name.equals("demo"))
//                xm = new ru.own.www.smack.GetXMPPConnection(name, "qazwsx");
//            else
                xm = new ru.own.www.smack.GetXMPPConnection(name, password);  //如果是登录用户，那就要修改这里
            boolean flag=xm.connect(); // 代表是否连接成功
            MessageHanlder.XMPPConnections.put(name, xm);
        }
        return xm;
    }
}
