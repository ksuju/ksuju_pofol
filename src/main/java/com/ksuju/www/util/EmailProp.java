package com.ksuju.www.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:properties/email.properties")
public class EmailProp {
    @Value("${email.username}")
    private String username;

    @Value("${email.password}")
    private String password;

    @Value("${email.pwChangeUri}")
    private String pwChangeUri;

    @Value("${email.joinUri}")
    private String joinUri;
    
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getPwChangeUri() {
		return pwChangeUri;
	}

	public String getJoinUri() {
		return joinUri;
	}
}
