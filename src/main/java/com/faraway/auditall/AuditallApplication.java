package com.faraway.auditall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AuditallApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuditallApplication.class, args);

    }

}
