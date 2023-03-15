package ppa.lab.springwebapp.aop;

import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
public class CommonJoinPointConfig {

    @Pointcut("execution(* ppa.lab.springwebapp.service.api.SimplePersonService.*(..))")
    public void beforeSimplePersonService () {
        // not implemented yet
    }
}
