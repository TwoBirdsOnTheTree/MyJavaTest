package com.mytest.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
public class ConfigurationBeanTest {
    @Autowired
    private ApplicationArguments args;
    @Value("${path}")
    private String systemPath;

    class BeanTest {
        String name;
    }

    @Bean
    public BeanTest getBean() {
        System.out.println("Application Arguments : "
                + String.join(",", this.args.getSourceArgs())
                + ", " + String.join(",", this.args.getOptionNames())
                + "," + String.join(",", this.args.getNonOptionArgs()));
        System.out.println("System environment variable: "
                + this.systemPath);

        BeanTest bean = new BeanTest();
        bean.name = "HelloBean!";
        return bean;
    }

    @Bean
    public void ss() {
        BeanTest bean1 = getBean();
        BeanTest bean2 = getBean();

        System.out.println("bean1 == bean 2 ? " + (bean1 == bean2));
    }

    @Bean("profileBean")
    @Profile("dev")
    // @Profile("devvvvvvvv") // 异常了NoSuchBeanDefinitionException, 因为没有这个配置
    public String profileBean() {
        return "这个是特定profile下才会有的bean";
    }

    public static void main(String[] args) {

    }

    @Bean("restTemplate")
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
