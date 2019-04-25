package top.infra.cloudready.springboot;

import io.micrometer.core.instrument.MeterRegistry;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceMonitor {

    @Autowired
    private MeterRegistry meterRegistry;

    @Before("execution(* top.infra.cloudready.springboot.controller.*.*(..))")
    public void countServiceInvoke(final JoinPoint joinPoint) {
        this.meterRegistry.counter(joinPoint.getSignature() + "_count").increment();
    }

    @Around("execution(* top.infra.cloudready.springboot.controller.*.*(..))")
    public void latencyService(final ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        pjp.proceed();
        long end = System.currentTimeMillis();

        this.meterRegistry.gauge(pjp.getSignature() + "_latency", (double) (end - start));
        //this.gaugeService.submit(pjp.getSignature() + "_latency", (double) (end - start));
    }
}
