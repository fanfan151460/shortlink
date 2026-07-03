package com.nageoffer.shortlink.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ShortLinkGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShortLinkGatewayApplication.class, args);
    }
}
