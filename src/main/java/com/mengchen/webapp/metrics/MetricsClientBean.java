package com.mengchen.webapp.metrics;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

public class MetricsClientBean {

    @Configuration
    public class MetricsConfig {
        @Bean
        public StatsDClient statsDClient(
                @Value("${metrics.statsd.host}") String host,
                @Value("${metrics.statsd.port}") int port,
                @Value("${metrics.prefix}") String prefix
        ) {
            return new NonBlockingStatsDClient(prefix, host, port);
        }
    }
}
