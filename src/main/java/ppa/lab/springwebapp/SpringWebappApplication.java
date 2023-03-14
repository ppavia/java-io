package ppa.lab.springwebapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import ppa.lab.springwebapp.configuration.AppConfig;

@SpringBootApplication
@EntityScan(basePackages = {"ppa.spring.domain.bean"})
@Import({ AppConfig.class })
public class SpringWebappApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringWebappApplication.class, args);
    }

}
