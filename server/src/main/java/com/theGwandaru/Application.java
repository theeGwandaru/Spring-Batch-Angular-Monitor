/**
 * 
 */
package com.theGwandaru;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * @author Swathi
 *
 */
@SpringBootApplication
@PropertySource("classpath:application.properties")
public class Application extends SpringBootServletInitializer {

	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
