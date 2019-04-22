package top.infra.cloudready.springboot;

import de.codecentric.boot.admin.server.config.EnableAdminServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

@EnableAdminServer
@EnableDiscoveryClient
@EnableHystrix
@EnableTurbine
@SpringBootApplication
public class SpringBootAdminApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SpringBootAdminApplication.class, args);
    }
}
