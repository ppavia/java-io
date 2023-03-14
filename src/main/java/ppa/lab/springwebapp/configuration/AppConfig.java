package ppa.lab.springwebapp.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ppa.spring.domain.bean.SimplePerson;

@Configuration
@EnableJpaRepositories({"ppa.lab.springwebapp.model.repository"})
@Import({ SimplePerson.class
        , DatasourceProxyConfig.class})
@ComponentScan(basePackages = {
        "ppa.lab.springwebapp.model.repository"
        , "ppa.lab.springwebapp.service"
        , "ppa.spring.domain.bean"
})
public class AppConfig {
}
