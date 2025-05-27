package com.kathapatel.qnaverse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import jakarta.validation.Validator;


@SpringBootApplication(exclude = { 
       
        HibernateJpaAutoConfiguration.class 
})
public class QnaverseApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(QnaverseApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
	    return builder.sources(QnaverseApplication.class);
	}

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

}
