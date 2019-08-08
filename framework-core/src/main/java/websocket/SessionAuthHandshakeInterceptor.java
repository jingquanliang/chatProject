package websocket;

import dao.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;
//这里有个逻辑就是当webscoket建立连接的时候被拦截，获取当前应用的session，
// 将用户登录信息获取出来，如果用户未登录，那么不好意思拒绝连接，如果已经登陆了，
// 那么将用户绑定到stomp的session中，第UserInterceptor中的时候就调用了这个用户信息。
public class SessionAuthHandshakeInterceptor extends TextWebSocketHandler implements HandshakeInterceptor {
    private Logger logger = LoggerFactory.getLogger(SessionAuthHandshakeInterceptor.class);
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        HttpSession session = getSession(request);
        if(session == null || session.getAttribute(Constants.SESSION_USER) == null){
            logger.info("no session information, web socket should return!");
//            return false;
        }
        else{
            logger.info("session information find+++++++++++");
        }
//            HttpHeaders header = request.getHeaders();
//
//            logger.info("================header================");
//            logger.info(String.valueOf(header));
//            logger.info("================attributes================");
//            logger.info(String.valueOf(attributes));

//            User user = new User();
//            user.setName("demo");
//            session.setAttribute(Constants.SESSION_USER,user);

//        attributes.put(Constants.WEBSOCKET_USER_KEY,session.getAttribute(Constants.SESSION_USER));
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
    private HttpSession getSession(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
//            return serverRequest.getServletRequest().getSession(false);
            return serverRequest.getServletRequest().getSession(); //没有session 就创建一个
        }
        return null;
    }
    //参考：https://blog.csdn.net/u011943534/article/details/81007002
}
