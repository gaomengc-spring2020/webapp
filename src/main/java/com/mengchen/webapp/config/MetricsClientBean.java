package com.mengchen.webapp.config;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
@Configuration

public class MetricsClientBean {

    @Bean
    public StatsDClient statsDClient(
            @Value("${metrics.statsd.host}") String host,
            @Value("${metrics.statsd.port}") int port,
            @Value("${metrics.prefix}") String prefix
    ) {
        return new NonBlockingStatsDClient(prefix, host, port);
    }

}
