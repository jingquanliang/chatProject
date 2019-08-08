package websocket;

import config.WxMappingJackson2HttpMessageConverter;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
//import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig  implements WebSocketMessageBrokerConfigurer {


    private Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);
    /**
     * 配置信息代理
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // 订阅Broker名称
        //点对点应配置一个/user消息代理，广播式应配置一个/topic消息代理
        config.enableSimpleBroker("/topic","/user");



        // 全局使用的消息前缀（客户端订阅路径上会体现出来）
//        registry.setApplicationDestinationPrefixes("/app");//        指服务端接收地址的前缀，意思就是说客户端给服务端发消息的地址的前缀。

        // 点对点使用的订阅前缀（客户端订阅路径上会体现出来），不设置的话，默认也是/user/
        config.setUserDestinationPrefix("/user/"); //给指定用户发送一对一的消息前缀

//        setApplicationDestinationPrefixes 与 setUserDestinationPrefix 起到的效果敲好相反，
//         前者定义了客户端发送地址的前缀,后端定义了客户端接收的地址前缀。

//        原文：https://blog.csdn.net/u011943534/article/details/97098687
        // 原文：https://blog.csdn.net/liyongzhi1992/article/details/81221103

    }

    /**
     * 注册stomp的端点
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 允许使用socketJs方式访问，访问点为webSocketServer，允许跨域
        // 在网页上我们就可以通过这个链接
        // http://localhost:8888/gs-guide-websocket
        // 来和服务器的WebSocket连接
        registry.addEndpoint("/gs-guide-websocket").setAllowedOrigins("*").addInterceptors(new SessionAuthHandshakeInterceptor()).withSockJS();
//        registry.addEndpoint("/gs-guide-websocket").withSockJS();
        //在这里也可以添加拦截器addInterceptor即可
    }

    /**
     * 配置客户端入站通道拦截器
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(createUserInterceptor());

     }



    /*将客户端渠道拦截器加入spring ioc容器*/
    @Bean
    public UserInterceptor createUserInterceptor() {
        return new UserInterceptor();
    }


    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(500 * 1024 * 1024);
        registration.setSendBufferSizeLimit(1024 * 1024 * 1024);
        registration.setSendTimeLimit(200000);
    }

}