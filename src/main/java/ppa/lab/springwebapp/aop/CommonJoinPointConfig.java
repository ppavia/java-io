package ppa.lab.springwebapp.aop;

import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ppa.lab.springwebapp.service.api.SimplePersonService;

@Component
public class CommonJoinPointConfig {
    private SimplePersonService simplePersonService;

    @Pointcut("execution(* ppa.lab.springwebapp.service.api.SimplePersonService.*(..))")
    public void beforeSimplePersonService () {}
}
