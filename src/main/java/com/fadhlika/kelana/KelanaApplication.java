package com.fadhlika.kelana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableTransactionManagement
@IntegrationComponentScan
public class KelanaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KelanaApplication.class, args);
    }

}
