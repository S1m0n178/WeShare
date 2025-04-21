package com.weshare.web.controller;
import com.weshare.component.RedisComponent;
import com.weshare.entity.constants.Constants;
import com.weshare.entity.dto.TokenUserInfoDto;
import com.weshare.entity.enums.ResponseCodeEnum;
import com.weshare.entity.vo.ResponseVO;
import com.weshare.exception.BusinessException;
import com.weshare.redis.RedisUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ABaseController {

    protected static final String STATUC_SUCCESS = "success";

    protected static final String STATUC_ERROR = "error";

    @Resource
    private RedisComponent redisComponent;

    protected <T> ResponseVO getSuccessResponseVO(T t) {
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(STATUC_SUCCESS);
        responseVO.setCode(ResponseCodeEnum.CODE_200.getCode());
        responseVO.setInfo(ResponseCodeEnum.CODE_200.getMsg());
        responseVO.setData(t);
        return responseVO;
    }

    protected <T> ResponseVO getBusinessErrorResponseVO(BusinessException e, T t) {
        ResponseVO vo = new ResponseVO();
        vo.setStatus(STATUC_ERROR);
        if (e.getCode() == null) {
            vo.setCode(ResponseCodeEnum.CODE_600.getCode());
        } else {
            vo.setCode(e.getCode());
        }
        vo.setInfo(e.getMessage());
        vo.setData(t);
        return vo;
    }

    protected <T> ResponseVO getServerErrorResponseVO(T t) {
        ResponseVO vo = new ResponseVO();
        vo.setStatus(STATUC_ERROR);
        vo.setCode(ResponseCodeEnum.CODE_500.getCode());
        vo.setInfo(ResponseCodeEnum.CODE_500.getMsg());
        vo.setData(t);
        return vo;
    }

    protected String getIpAddr() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.indexOf(",") != -1) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    protected void saveToken2Cookie(HttpServletResponse response, String token){
        Cookie cookie = new Cookie(Constants.TOKEN_WEB,token);
        cookie.setMaxAge(Constants.TIME_SECONDS_WEEK);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
    private String getTokenFromCookie(HttpServletRequest request){
        Cookie[]cookies = request.getCookies();
        if(cookies ==null ){
            return null;
        }
        String token = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Constants.TOKEN_WEB)) {
                return cookie.getValue();
            }
        }
        return null;

    }

    protected TokenUserInfoDto getTokenUserInfoDto(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //TODO 接口测试工具发来的Header与实际前端可能不同，用request.getHeader(Constants.TOKEN_WEB)直接取不到token必须读取cookie才行
        String token = getTokenFromCookie(request);
//        String token=request.getHeader(Constants.TOKEN_WEB);
        return redisComponent.getTokenInfo(token);
    }
    protected void cleanCookie(HttpServletResponse response){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        Cookie[] cookies = request.getCookies();
        if(cookies==null){
            return;
        }
        String token = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Constants.TOKEN_WEB)) {
                redisComponent.cleanToken(cookie.getValue());
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
                break;
            }
        }
    }

}
