package com.xue;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xue.repository.dao")
public class SpringBatchImportApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchImportApplication.class, args);
	}

}
