package top.infra.cloudready.springboot;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceMonitor {

    @Autowired
    private CounterService counterService;

    @Before("execution(* top.infra.cloudready.springboot.controller.*.*(..))")
    public void countServiceInvoke(final JoinPoint joinPoint) {
        this.counterService.increment(joinPoint.getSignature() + "_count");
    }

    @Autowired
    private GaugeService gaugeService;

    @Around("execution(* top.infra.cloudready.springboot.controller.*.*(..))")
    public void latencyService(final ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        pjp.proceed();
        long end = System.currentTimeMillis();

        this.gaugeService.submit(pjp.getSignature() + "_latency", (double) (end - start));
    }
}
