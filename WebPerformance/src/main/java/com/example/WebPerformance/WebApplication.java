package com.example.WebPerformance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}

	@Bean
	public ServletRegistrationBean<ServletContainer> jerseyServlet() {
		ServletRegistrationBean<ServletContainer> registration = new ServletRegistrationBean<>(new ServletContainer(),
				"/api/*");
		registration.addInitParameter("jersey.config.server.provider.packages",
				"com.example.webperformance.resource");
		registration.setLoadOnStartup(1);
		return registration;
	}
}