package com.netease.mini.bietuola;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.netease.mini.bietuola.mapper")
public class BietuolaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BietuolaApplication.class, args);
	}

}
