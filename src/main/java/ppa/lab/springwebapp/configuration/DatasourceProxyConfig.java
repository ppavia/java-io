package ppa.lab.springwebapp.configuration;

import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.listener.logging.SystemOutQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;

@Configuration
@ConditionalOnProperty(
        value = "trace-sql.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class DatasourceProxyConfig implements BeanPostProcessor {
    private static final Logger LOG = LogManager.getLogger(DatasourceProxyConfig.class);

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (bean instanceof DataSource) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("DEBUGGING DATA SOURCE : {}", () -> beanName);
            }
            ProxyFactory factory = new ProxyFactory(bean);
            factory.setProxyTargetClass(true);
            factory.addAdvice(new ProxyDataSourceInterceptor((DataSource) bean));
            return factory.getProxy();
        }
        return bean;
    }

    private static class ProxyDataSourceInterceptor implements MethodInterceptor {
        private final DataSource dataSource;

        public ProxyDataSourceInterceptor(final DataSource dataSource) {
            DefaultQueryLogEntryCreator creator = new DefaultQueryLogEntryCreator();
            creator.setMultiline(true);
            SystemOutQueryLoggingListener listener = new SystemOutQueryLoggingListener();
            listener.setQueryLogEntryCreator(creator);
            this.dataSource = ProxyDataSourceBuilder
                    .create(dataSource)
                    .countQuery()
                    .multiline()
                    .listener(listener)
  //                  .logQueryToSysOut()
                    .logQueryBySlf4j(SLF4JLogLevel.DEBUG)
                    .asJson()
                    .build();
        }

        @Override
        public Object invoke(final MethodInvocation invocation) throws Throwable {
            Method proxyMethod = ReflectionUtils.findMethod(dataSource.getClass(), invocation.getMethod().getName());
            if (proxyMethod != null) {
                return proxyMethod.invoke(dataSource, invocation.getArguments());
            }
            return invocation.proceed();
        }
    }
}
