package com.assignment.booksmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class BooksManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(BooksManagementApplication.class, args);
    }

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                        .select()
                        .apis(RequestHandlerSelectors.basePackage(getClass().getPackage().getName()))
                        .paths(PathSelectors.any())
                        .build()
                        .apiInfo(generateApiInfo());
    }

    private ApiInfo generateApiInfo() {
        return new ApiInfo("Book management Service",
                        "This service is to manage book i.e. you can perform CRUD operation using ISBN, import books via CSV and search them as well",
                        "Version 1.0 ",
                        "abc.com", "rahulrapariya@gmail.com", "Apache 2.0",
                        "http://www.apache.org/licenses/LICENSE-2.0");
    }

}
