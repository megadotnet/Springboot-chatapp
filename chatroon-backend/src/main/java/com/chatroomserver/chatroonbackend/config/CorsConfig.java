package com.chatroomserver.chatroonbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @Description
 * @Author peter
 * @Date 2023/4/14 17:47
 **/
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Bean
    public CorsFilter corsFilter(){

        // 初始化cors配置对象
        CorsConfiguration configuration = new CorsConfiguration();

        // 设置允许跨域的域名,如果允许携带cookie的话,路径就不能写*号, *表示所有的域名都可以跨域访问
        configuration.addAllowedOrigin(CorsConfiguration.ALL);
        // 设置跨域访问可以携带cookie

        // 允许所有的请求方法 ==> GET POST PUT Delete
        configuration.addAllowedMethod(CorsConfiguration.ALL);
        // 允许携带任何头信息
        configuration.addAllowedHeader(CorsConfiguration.ALL);


        // 初始化cors配置源对象
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();

        // 给配置源对象设置过滤的参数
        // 参数一: 过滤的路径 == > 所有的路径都要求校验是否跨域
        // 参数二: 配置类
        configurationSource.registerCorsConfiguration("*/**", configuration);

        // 返回配置好的过滤器
        return new CorsFilter(configurationSource);
    }
}
