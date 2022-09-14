package com.example.controller;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Controller
@RestController
@Slf4j
public class Resilience4jController {
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @GetMapping("/circuitBreakerAOPTestNoFallbackMethod")
    @CircuitBreaker(name = "order")
    public Map circuitBreakerAOPTestNoFallbackMethod() throws Exception{
        throw new Exception("服务异常");
    }
    @GetMapping("/circuitBreakerAOPTest")
    @CircuitBreaker(name = "order", fallbackMethod = "fallBack")
    public Map circuitBreakerAOPTest() throws Exception{
        System.out.println("ssssssssssssss");
        throw new Exception("服务异常");
    }

    private Map fallBack(CallNotPermittedException e){
        log.info("熔断器已经打开，拒绝访问被保护方法~");
        io.github.resilience4j.circuitbreaker.CircuitBreaker order = circuitBreakerRegistry.circuitBreaker("order");
        io.github.resilience4j.circuitbreaker.CircuitBreaker.Metrics metrics = order.getMetrics();
        log.info("方法降级中：" + "state=" + order.getState() + " , metrics[ failureRate=" + metrics.getFailureRate() +
                ", bufferedCalls=" + metrics.getNumberOfBufferedCalls() +
                ", failedCalls=" + metrics.getNumberOfFailedCalls() +
                ", successCalls=" + metrics.getNumberOfSuccessfulCalls() +
                ", maxBufferCalls=" + metrics.getNumberOfBufferedCalls() +
                ", notPermittedCalls=" + metrics.getNumberOfNotPermittedCalls() +
                " ]"
        );
        Map map=new HashMap();
        map.put("熔断测试", 0);
        return map;
    }
}
