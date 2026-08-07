package com.fadhlika.kelana.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebUiConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/{path:^(?!api)[^.]*$}").setViewName("forward:/index.html");
        registry.addViewController("/{path:^(?!api).*$}/**/{path:^(?!api)[^.]*$}").setViewName("forward:/index.html");
    }
}
