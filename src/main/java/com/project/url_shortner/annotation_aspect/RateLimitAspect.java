package com.project.url_shortner.annotation_aspect;

import com.project.url_shortner.annotations.RateLimit;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class RateLimitAspect {

    private final Map<String, List<Long>> cache = new ConcurrentHashMap<String,List<Long>>();
    @Around("@annotation(rateLimit)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(null == attributes){
            throw new IllegalStateException("Could not find current HTTP request. Ensure this annotation is only used on web endpoints.");
        }

        //Getting request to take the user ip

        HttpServletRequest request = attributes.getRequest();
        String ipAddress = getIpAddress(request);

        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime-(rateLimit.windowSeconds()*1000);
        cache.putIfAbsent(ipAddress,new ArrayList<>());
        List<Long> timestamps = cache.get(ipAddress);

        synchronized (timestamps) {
            timestamps.removeIf(timestamp -> timestamp < windowStart);
            if(timestamps.size()>rateLimit.maxRequest()) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,"Rate Limit Exceeded. Try again Later");
            }
            timestamps.add(currentTime);
            System.out.println("Ip Address"+ipAddress+" Current Time"+currentTime+" limit Size"+cache.get(ipAddress).size() );
        }
        return joinPoint.proceed();
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(null==ip){
            return request.getRemoteAddr();
        }
        return ip.split(",")[0];
    }
}
