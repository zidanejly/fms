package com.jfhealthcare;

import java.io.File;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@SpringBootApplication
@MapperScan(basePackages = "com.jfhealthcare.*.*.mapper")
@ServletComponentScan
public class FmsApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(FmsApplication.class, args);
		ConfigurableEnvironment environment = run.getEnvironment();
		File f = new File(environment.getProperty("uploadFilePath"));
		File f2 = new File(environment.getProperty("uploadedRecordPath"));
		if (!f.exists()) {
			f.mkdir();
		}
		if (!f2.exists()) {
			f2.mkdir();
		}
	}
}
