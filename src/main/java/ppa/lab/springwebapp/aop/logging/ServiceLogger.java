package ppa.lab.springwebapp.aop.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class ServiceLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLogger.class);

    @Before("ppa.lab.springwebapp.aop.CommonJoinPointConfig.beforeSimplePersonService()")
    public void before (JoinPoint joinPoint) {
        LOGGER.info("start find simpleperson");
    }
}
