package com.creator.interceptor;

import com.creator.common.BusinessException;
import com.creator.util.JwtUtil;
import com.creator.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    
    private final JwtUtil jwtUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }
        
        String token = request.getHeader("Authorization");
        
        // Try to set user context if token is present
        if (token != null && token.startsWith("Bearer ")) {
            try {
                String tokenValue = token.substring(7);
                if (!jwtUtil.isTokenExpired(tokenValue)) {
                    UserContext.setUserId(jwtUtil.getUserId(tokenValue));
                    UserContext.setUsername(jwtUtil.getUsername(tokenValue));
                    UserContext.setRole(jwtUtil.getRole(tokenValue));
                }
            } catch (Exception e) {
                // Ignore token errors for public endpoints
            }
        }
        
        // Allow public access to certain endpoints
        if (isPublicEndpoint(uri, method)) {
            return true;
        }
        
        // For non-public endpoints, require valid token
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(401, "请先登录");
        }
        
        if (UserContext.getUserId() == null) {
            throw new BusinessException(401, "登录已过期，请重新登录");
        }
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
    
    /**
     * Check if the URI is a public endpoint that doesn't require authentication
     */
    private boolean isPublicEndpoint(String uri, String method) {
        // GET requests to certain endpoints are public
        if ("GET".equalsIgnoreCase(method)) {
            // Public list endpoints
            if (uri.equals("/api/creator/list") || uri.equals("/api/content/list") || 
                uri.equals("/api/creator/categories")) {
                return true;
            }
            
            // Public detail endpoints with numeric ID
            if (uri.matches("/api/content/\\d+") || uri.matches("/api/creator/\\d+")) {
                return true;
            }
            
            // Public content endpoints
            if (uri.matches("/api/content/\\d+/comments") || uri.matches("/api/content/creator/\\d+")) {
                return true;
            }
        }
        
        return false;
    }
}
