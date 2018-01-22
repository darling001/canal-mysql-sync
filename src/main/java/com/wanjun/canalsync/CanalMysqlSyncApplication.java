package com.wanjun.canalsync;

import com.wanjun.canalsync.client.CanalInitHandler;
import com.wanjun.canalsync.client.config.CanalProperties;
import com.wanjun.canalsync.client.config.RedisProperties;
import com.wanjun.canalsync.util.SpringUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableConfigurationProperties({CanalProperties.class, RedisProperties.class})
@EnableScheduling
@EnableTransactionManagement
@MapperScan("com.wanjun.canalsync.dao")
//@Import(value = {SpringUtil.class})
public class CanalMysqlSyncApplication {

    @Autowired
    private CanalInitHandler canalInitHandler;

    public static void main(String[] args) {
        SpringApplication.run(CanalMysqlSyncApplication.class, args);
    }

    @Component
    class InitRunner implements CommandLineRunner {
        @Override
        public void run(String... strings) throws Exception {
            canalInitHandler.initCanalStart();
        }
    }
}
