package com.sp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class SpApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(SpApplication.class, args);
        System.out.println("启动成功!");
    }
}