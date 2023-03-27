package ppa.lab.springwebapp.configuration;

import fr.assia.javacustomutils.string.StringUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.UserCredentialsDataSourceAdapter;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ConditionalOnProperty(
        value = "db.h2.server.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class DbH2MultiTenantConfiguration {
    @PostConstruct
    public void postContruct() {
        Assert.isTrue(!StringUtils.isNotBlank(databaseUrl), "db.url property is not set");
    }

    @Value("${spring.datasource.driver-class-name}")
    private String driver;

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory ) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        transactionManager.setJpaDialect(new HibernateJpaDialect());
        return transactionManager;
    }

    /**
     * Factory de création de la sessionFactory hibernate
     * @return
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSourceAdapter
            , Properties hibernateProperties
            , JpaVendorAdapter jpaVendorAdapter
    ) {
        final LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSourceAdapter);
        entityManagerFactory.setPackagesToScan("ppa.spring.domain.bean");
        entityManagerFactory.setPersistenceUnitName("spring-data");
        entityManagerFactory.setJpaProperties(hibernateProperties);
        entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter);
        return entityManagerFactory;
    }

    @Bean
    public UserCredentialsDataSourceAdapter dataSource() {
        final UserCredentialsDataSourceAdapter datasource = new UserCredentialsDataSourceAdapter();
        datasource.setCredentialsForCurrentThread(user, password);
        datasource.setTargetDataSource(targetDataSource());
        return datasource;
    }

    private DataSource targetDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(databaseUrl);
        return dataSource;
    }

    @Bean
    public Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        hibernateProperties.setProperty("hibernate.cache.use_second_level_cache", "false");
        hibernateProperties.setProperty("hibernate.readOnly", "true");
        return hibernateProperties;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabase(Database.H2);
        jpaVendorAdapter.setGenerateDdl(true);
        jpaVendorAdapter.setShowSql(true);
        return jpaVendorAdapter;
    }
}