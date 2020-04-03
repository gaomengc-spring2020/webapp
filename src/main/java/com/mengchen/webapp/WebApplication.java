package com.mengchen.webapp;

import com.mengchen.webapp.properties.FileStorageProperties;
import com.mengchen.webapp.sqs.SQSPollThread;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages={"com.*"})
@EnableConfigurationProperties(FileStorageProperties.class)
//@PropertySource("classpath:application.properties")
public class WebApplication {

	public static void main(String[] args) {

		new Thread(new SQSPollThread()).start();

		System.out.println(System.getenv("RDS_MYSQL_DB_HOST"));
//		SpringApplication.run(WebApplication.class, args);

		// run sqs tread in background

	}
}
