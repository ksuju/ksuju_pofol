package com.ksuju.www.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.util.Locale;

/**
 * packageName    : com.ksuju.www.config
 * fileName       : ViewResolverConfig
 * author         : sungjun
 * date           : 2024-12-20
 * description    : 자동 주석 생성
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-12-20        kyd54       최초 생성
 */
@Configuration
public class ViewResolverConfig {

    @Bean
    public ViewResolver viewResolver() {
        return new ViewResolver() {
            @Override
            public View resolveViewName(String viewName, Locale locale) throws Exception {
                // "fileDownloadHandler" 뷰 이름이 들어오면 FileDownloadHandler를 반환
                if ("fileDownloadHandler".equals(viewName)) {
                    return new FileDownloadHandler();
                }
                // 다른 뷰 이름에 대해서는 null을 반환
                return null;
            }
        };
    }

    @Bean
    public FileDownloadHandler fileDownloadHandler() {
        return new FileDownloadHandler();
    }
}
