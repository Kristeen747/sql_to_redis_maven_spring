package ru.riji.sql_to_influx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.riji.sql_to_influx.config.FileStorageProperties;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@ComponentScan({"ru.riji.sql_to_influx"})
@EnableConfigurationProperties({FileStorageProperties.class })
@EnableScheduling
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class,args);
    }
}
