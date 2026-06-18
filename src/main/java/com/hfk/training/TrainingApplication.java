package com.hfk.training;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 大学学生专业培养计划管理系统 - 启动类
 *
 * @author HFK Training Team
 */
@SpringBootApplication
public class TrainingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainingApplication.class, args);
        System.out.println("""

                ╔══════════════════════════════════════════════════════╗
                ║     大学学生专业培养计划管理系统 - 后端服务启动完成      ║
                ║     API 文档: http://localhost:8080/doc.html          ║
                ║     Swagger:  http://localhost:8080/swagger-ui.html   ║
                ╚══════════════════════════════════════════════════════╝
                """);
    }
}
