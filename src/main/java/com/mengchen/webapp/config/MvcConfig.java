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
import java.util.Properties;
import java.util.logging.Logger;
import java.lang.System;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.mengchen.webapp")
@PropertySource("classpath:application.properties")
public class MvcConfig {

    @Autowired
    private Environment env;


    private Logger logger = Logger.getLogger(getClass().getName());

    @Bean
    public DataSource securityDataSource() {

//        System.setProperty("javax.net.ssl.trustStore","/home/clientkeystore.jks");
//        System.setProperty("javax.net.ssl.trustStorePassword", "woshengri");

        ComboPooledDataSource securityDataSource = new ComboPooledDataSource();

        try{
            securityDataSource.setDriverClass("com.mysql.jdbc.Driver");
        }catch (PropertyVetoException e){
            throw new RuntimeException(e);
        }

        logger.info(">>>>>> jdbc.url=" + env.getProperty("spring.datasource.url"));
        logger.info(">>>>>> jdbc.user=" + env.getProperty("spring.datasource.username"));

        // set database connection props

        securityDataSource.setJdbcUrl(env.getProperty("spring.datasource.url"));
        securityDataSource.setUser(env.getProperty("spring.datasource.username"));
        securityDataSource.setPassword(env.getProperty("spring.datasource.password"));

        // set connection pool props
        securityDataSource.setInitialPoolSize(5);
        securityDataSource.setMinPoolSize(5);
        securityDataSource.setMaxPoolSize(20);
        securityDataSource.setMaxIdleTime(3000);

        return securityDataSource;
    }
}
