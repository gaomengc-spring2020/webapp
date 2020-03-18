package com.mengchen.webapp;

import com.mengchen.webapp.properties.FileStorageProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages={"com.*"})
@EnableConfigurationProperties(FileStorageProperties.class)
//@PropertySource("classpath:application.properties")
public class WebApplication {

	public static void main(String[] args) {

		System.out.println(System.getenv("RDS_MYSQL_DB_HOST"));

		SpringApplication.run(WebApplication.class, args);


	}

}
