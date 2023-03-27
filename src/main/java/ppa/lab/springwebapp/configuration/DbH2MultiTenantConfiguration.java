package ppa.lab.springwebapp.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ConditionalOnProperty(
        value = "db.multitenant.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class DbH2MultiTenantConfiguration {

}