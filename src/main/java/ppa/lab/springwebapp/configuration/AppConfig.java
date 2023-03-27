package ppa.lab.springwebapp.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"classpath:persistence.properties"})
@EntityScan(basePackages = {"ppa.spring.domain.bean"})
@ComponentScan({"ppa.lab.springwebapp.configuration"})
public class AppConfig {
}
