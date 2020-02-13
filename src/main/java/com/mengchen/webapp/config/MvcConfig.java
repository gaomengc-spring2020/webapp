package com.mengchen.webapp.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.logging.Logger;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.mengchen.webapp")
@PropertySource("classpath:persistence-mysql.properties")
public class MvcConfig {

    @Autowired
    private Environment env;

    private Logger logger = Logger.getLogger(getClass().getName());

    @Bean
    public DataSource securityDataSource() {
        ComboPooledDataSource securityDataSource = new ComboPooledDataSource();

        try{
            securityDataSource.setDriverClass(env.getProperty("jdbc.driver"));
        }catch (PropertyVetoException e){
            throw new RuntimeException(e);
        }

        logger.info(">>>>>> jdbc.url=" + env.getProperty("jdbc.url"));
        logger.info(">>>>>> jdbc.user=" + env.getProperty("jdbc.user"));

        // set database connection props
        securityDataSource.setJdbcUrl(env.getProperty("spring.datasource.url"));
        securityDataSource.setUser(env.getProperty("spring.datasource.username"));
        securityDataSource.setPassword(env.getProperty("spring.datasource.password"));

        // set connection pool props
        securityDataSource.setInitialPoolSize(150);
        securityDataSource.setMinPoolSize(150);
        securityDataSource.setMinPoolSize(500);
        securityDataSource.setMaxIdleTime(300);

        return securityDataSource;
    }
}
