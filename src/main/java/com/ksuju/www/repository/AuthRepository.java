package com.ksuju.www.repository;

import java.util.HashMap;

import org.apache.ibatis.annotations.Param;

public interface AuthRepository {
	
	// 아이디 찾기 (auth)
	public String findID(@Param("name") String name, @Param("email") String email);
	
	//updateAuth (auth)
	public int updateAuthaa(@Param("memberSeq") Integer memberSeq);
	
	//updateMAuth (auth)
	public boolean updateMAuth(@Param("memberID") String memberID);
	
	// authNumSelect db에 저장된 authNum 조회 (auth)
	public Integer authNumSelect(String email);
	
	// auth_num update to null (auth)
	public int updateAuthNumToNull(String email);
	
	// auth_num update (auth)
	public int updateAuthNum(HashMap<String, String> params);
	
	// 인증만료시간 가져오기 (auth)
	public long getExpireDtm(String email);
	
	// 비밀번호 찾기 할 때 입력한 아이디와 이메일로부터 가져온 db에 있는 아이디 비교 (auth)
	public String compareID(@Param("email") String email);
}
