//package com.rkchat.demo.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//    @Value("${frontend.url}")
//    private String frontendUrl;
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // or just "/ws/**"
//                .allowedOrigins(frontendUrl)
//                .allowedMethods("*")
//                .allowedHeaders("*")
//                .allowCredentials(true); // if using cookies or Authorization header
//    }
//}
