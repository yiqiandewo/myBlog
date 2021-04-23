package com.yiqiandewo.interceptor;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.yiqiandewo.util.CookieUtils;
import com.yiqiandewo.util.JWTUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class JWTInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        try {
            //这里token通过cookie传输
            Cookie cookie = CookieUtils.get(request, "token");
            String token = cookie.getValue();
            JWTUtils.verifyToken(token);//验证token令牌
            //验证成功
            //拿到token payload中的username
            String username = JWTUtils.parserToken(token, "username");
            String avatar = JWTUtils.parserToken(token, "avatar");
            request.setAttribute("username", username);
            request.setAttribute("avatar", avatar);
            return true;  //放行
        } catch (SignatureVerificationException e) {
            e.printStackTrace();
            System.out.println("无效签名");
        } catch (TokenExpiredException e) {
            e.printStackTrace();
            System.out.println("token过期");
        } catch (AlgorithmMismatchException e) {
            e.printStackTrace();
            System.out.println("token算法不一致");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("token无效");
        }

        CookieUtils.delete(request, response, "token");

        if (CookieUtils.get(request, "tokenInvalid") == null) {
            CookieUtils.set(response, "tokenInvalid", "请先登录", -1);
        }

        response.sendRedirect("/admin");
        return false;  //拦截
    }
}
