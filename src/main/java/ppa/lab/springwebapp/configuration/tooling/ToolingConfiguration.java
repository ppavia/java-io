package ppa.lab.springwebapp.configuration.tooling;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ppa.lab.springwebapp.tooling.validation.XmlFileValidation;
import ppa.lab.springwebapp.tooling.xml.XmlFileParser;

@Configuration
public class ToolingConfiguration {

    @Bean
    public XmlFileParser xmlFileParser () {
        return new XmlFileParser(xmlFileValidation());
    }

    @Bean
    public XmlFileValidation xmlFileValidation() {
        return new XmlFileValidation();
    }
}
