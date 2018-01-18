package com.wanjun.canalsync;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@MapperScan("com.wanjun.canalsync.dao")
public class CanalMysqlSyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(CanalMysqlSyncApplication.class, args);
	}
}
