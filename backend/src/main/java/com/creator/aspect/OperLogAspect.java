package com.creator.aspect;

import com.creator.annotation.OperLog;
import com.creator.entity.OperationLog;
import com.creator.mapper.OperationLogMapper;
import com.creator.util.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperLogAspect {
    
    private final OperationLogMapper operationLogMapper;
    private final ObjectMapper objectMapper;
    
    @Around("@annotation(operLog)")
    public Object around(ProceedingJoinPoint point, OperLog operLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        Object result = point.proceed();
        
        long duration = System.currentTimeMillis() - startTime;
        
        try {
            saveLog(point, operLog, duration);
        } catch (Exception e) {
            log.error("保存操作日志失败: ", e);
        }
        
        return result;
    }
    
    private void saveLog(ProceedingJoinPoint point, OperLog operLog, long duration) throws Exception {
        MethodSignature signature = (MethodSignature) point.getSignature();
        
        OperationLog log = new OperationLog();
        log.setUserId(UserContext.getUserId());
        log.setUsername(UserContext.getUsername());
        log.setOperation(operLog.value());
        log.setMethod(signature.getDeclaringTypeName() + "." + signature.getName());
        
        Object[] args = point.getArgs();
        if (args != null && args.length > 0) {
            String params = objectMapper.writeValueAsString(args);
            log.setParams(params.length() > 2000 ? params.substring(0, 2000) : params);
        }
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            log.setIp(getIpAddress(request));
        }
        
        log.setDuration(duration);
        
        operationLogMapper.insert(log);
    }
    
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
