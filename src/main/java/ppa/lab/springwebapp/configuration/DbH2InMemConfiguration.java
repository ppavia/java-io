package ppa.lab.springwebapp.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.UserCredentialsDataSourceAdapter;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ConditionalOnProperty(
        value = "db.h2.inmemory.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class DbH2InMemConfiguration {
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
        entityManagerFactory.setPersistenceUnitName("learning");
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
        EmbeddedDatabaseBuilder dbBuilder = new EmbeddedDatabaseBuilder();
        return dbBuilder.setType(EmbeddedDatabaseType.H2)
                .generateUniqueName(true)
                .addScript("schema.sql")
                .addScript("data.sql")
                .build();
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
        jpaVendorAdapter.setGenerateDdl(false);
        jpaVendorAdapter.setShowSql(false);
        return jpaVendorAdapter;
    }
}