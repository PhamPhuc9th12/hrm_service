package org.example.hrm_salary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class HrmSalaryApplication {

    public static void main(String[] args) {
        SpringApplication.run(HrmSalaryApplication.class, args);
    }

}
