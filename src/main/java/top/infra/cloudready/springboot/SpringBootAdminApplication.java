package top.infra.cloudready.springboot;

import de.codecentric.boot.admin.server.config.EnableAdminServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
// import org.springframework.cloud.netflix.hystrix.EnableHystrix;
// import org.springframework.cloud.netflix.turbine.EnableTurbine;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

// @Configuration
// @EnableAutoConfiguration
// @ComponentScan(excludeFilters = {
//     @ComponentScan.Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
//     @ComponentScan.Filter(type = FilterType.CUSTOM,
//         classes = AutoConfigurationExcludeFilter.class)})
@EnableDiscoveryClient
@EnableAdminServer
// @EnableHystrix
// @EnableTurbine
@SpringBootApplication
public class SpringBootAdminApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SpringBootAdminApplication.class, args);
    }
}
