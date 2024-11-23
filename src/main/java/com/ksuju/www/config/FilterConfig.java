package com.ksuju.www.config;

/**
 * packageName    : com.ksuju.www.config
 * fileName       : FilterConfig
 * author         : sungjun
 * date           : 2024-11-23
 * description    : 자동 주석 생성
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-11-23        kyd54       최초 생성
 */
import com.ksuju.www.filter.LoginFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<LoginFilter> loginFilter() {
        FilterRegistrationBean<LoginFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoginFilter());
        registrationBean.addUrlPatterns("/pf/forum/notice/*"); // 필터를 적용할 URL 패턴 지정
        return registrationBean;
    }
}
