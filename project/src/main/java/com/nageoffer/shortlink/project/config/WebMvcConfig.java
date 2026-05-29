package com.nageoffer.shortlink.project.config;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebMvcConfig {

//    @Bean
    public FilterRegistrationBean<Filter> faviconFilter() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter((request, response, chain) ->
                ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_NO_CONTENT));
        registration.addUrlPatterns("/favicon.ico");
        return registration;
    }
}
