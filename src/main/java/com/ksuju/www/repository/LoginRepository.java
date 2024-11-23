package com.ksuju.www.repository;

import java.util.HashMap;

import org.apache.ibatis.annotations.Param;

public interface LoginRepository {
	
	//로그인 (login)
	public String loginCheck(String memberID);
	
	// 로그인 기록 남기기 (login)
	public boolean saveLoginLog(@Param("userID") String userID, @Param("loginDate") String loginDate);

	// 비밀번호 변경 (login)
	public int changePasswd(HashMap<String, String> params);
}
