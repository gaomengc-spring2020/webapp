package com.mengchen.webapp.config;

import com.mengchen.webapp.rest.BillRestController;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
@Configuration

public class MetricsClientBean {

    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Bean
    public StatsDClient statsDClient(
            @Value("${metrics.statsd.host:localhost}") String host,
            @Value("${metrics.statsd.port:8125}") int port,
            @Value("${metrics.prefix:example.app}") String prefix
    ) {
        logger.info(">>>>>>>HOST:" + host);
        logger.info(">>>>>>>Port:" + port);
        StatsDClient client = new NonBlockingStatsDClient(prefix, host, port);
        client.count("test",10);
        return client;
    }

}
