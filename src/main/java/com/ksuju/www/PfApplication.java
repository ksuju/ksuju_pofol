package com.ksuju.www;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ksuju.www.repository") // 매퍼 패키지 경로
public class PfApplication {

	public static void main(String[] args) {
		SpringApplication.run(PfApplication.class, args);
	}

}
