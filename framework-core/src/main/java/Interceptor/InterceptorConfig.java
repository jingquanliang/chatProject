package Interceptor;

import javax.script.ScriptContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.PrintWriter;


public class InterceptorConfig implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(InterceptorConfig.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // TODO Auto-generated method stub
        log.info("------preHandle------");
        // 获取session
        HttpSession session = request.getSession(true);
        // 判断用户ID是否存在，不存在就跳转到登录界面
        if (session.getAttribute("userId") == null) {
            log.info("-session 为空--未登录---！");
            //System.out.println(request.getContextPath() + "/login");
            //response.sendRedirect("/user/gologin");
            PrintWriter printWriter = response.getWriter();
            printWriter.write("{code:0,message:\"session is invalid,please login first!\"}");
            return false;
        } else {
            log.info("-session 获取了值--已登录---！");
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // TODO Auto-generated method stub
    }

}