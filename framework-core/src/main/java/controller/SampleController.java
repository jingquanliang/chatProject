package controller;

import Utility.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
// import Host;

@RestController
@EnableAutoConfiguration
public class SampleController {

   /**
     * 引入日志，注意都是"org.slf4j"包下
     */
    private final static Logger logger = LoggerFactory.getLogger(SampleController.class);

    @RequestMapping("/")
    String home(HttpSession session) {
	    session.setAttribute("aa","55");
        logger.info("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaahome");
        logger.error("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee=");
        String value = (String) session.getAttribute("bb");
        System.out.println("hello "+value);
        return Host.getIPAndHostName()+"-Hello World! from framework-cor"+"redis-bb:"+value;
    }

    @RequestMapping("/test/{name}")
//    @ResponseBody  //if use @Controller in class header， @ResponseBody must be included
    public String pathVariable(HttpSession session,@PathVariable("name")String name,@RequestHeader("User-Agent")String userAgent,@RequestHeader("Cookie")String cookie){
	    session.setAttribute("bb","55");
	    String value = (String) session.getAttribute("bb");
        System.out.println("hello "+name);
        return "name:"+name+"\n"+userAgent+"-cooke:"+cookie+"redis-bb:"+value;
    }

    // http:localhost:8080/put?key=name&value=liwei
    @RequestMapping("/put")
    public String put(HttpSession session,
                      @RequestParam("key") String key,@RequestParam("value") String value){
        session.setAttribute(key, value);
        return "PUT OK";
    }

    // http:localhost:8080/get?key=name
    @RequestMapping("/get")
    public String get(HttpSession session,
                      @RequestParam("key") String key){
        String value = (String) session.getAttribute(key);

        if(value == null || "".equals(value)){
            return "NO VALUE GET";
        }
        return value;
    }

    /**
     * 获取请求头中的信息
     * @RequestHeader 也有 value ,required ,defaultValue 三个参数
     * @param userAgent
     * @param cookie
     * @return
     */
    @RequestMapping("/requestHeader")
    public String requestHeader(@RequestHeader("User-Agent")String userAgent,@RequestHeader("Cookie")String cookie){
        System.out.println("userAgent:["+userAgent+"]");
        System.out.println("cookie:["+cookie+"]");
        return userAgent+"-"+cookie;
    }

    /**
     * 使用@CookieValue 绑定cookie值<br/>
     * 注解@CookieValue 也有 value ,required ,defaultValue 三个参数
     * @param session
     * @return
     */
    @RequestMapping("/cookieRequest")
    public String cookieValue(@CookieValue(value = "JSESSIONID")String session){
        System.out.println("JESSIONID:"+session+"");
        return session;
    }
}