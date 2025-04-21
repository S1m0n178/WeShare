package com.weshare.admin.interceptor;

import com.weshare.component.RedisComponent;
import com.weshare.entity.constants.Constants;
import com.weshare.entity.enums.ResponseCodeEnum;
import com.weshare.exception.BusinessException;
import com.weshare.utils.StringTools;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AppInterceptor implements HandlerInterceptor {
    private final static String URL_ACCOUNT="/account";
    private final static String URL_FILE="/file";

    @Resource
    RedisComponent redisComponent;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(null ==handler){
            return false;
        }
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        if(request.getRequestURI().contains(URL_ACCOUNT)){
            return true;
        }
        //TODO 接口测试工具发来的Header与实际前端可能不同，用request.getHeader(Constants.TOKEN_ADMIN)直接取不到token必须读取cookie才行
//        String tmp = request.getHeader("Cookie");
//        String token = request.getHeader(Constants.TOKEN_ADMIN);
        String token = getTokenFromCookie(request);
        //获取图片
        if(request.getRequestURI().contains(URL_FILE)){
            token = getTokenFromCookie(request);
        }
        if(StringTools.isEmpty(token)){
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        Object sessionObj = redisComponent.getTokenInfo4Admin(token);
        if(null==sessionObj){
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        return true;
    }
    private String getTokenFromCookie(HttpServletRequest request){
        Cookie[]cookies = request.getCookies();
        if(cookies ==null ){
            return null;
        }
            String token = null;
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(Constants.TOKEN_ADMIN)) {
                    return cookie.getValue();
                }
            }
            return null;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
