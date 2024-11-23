package com.ksuju.www.repository;

import java.util.HashMap;
import java.util.List;

import com.ksuju.www.dto.EmailAuthDto;
import com.ksuju.www.dto.MemberAuthDto;
import org.apache.ibatis.annotations.Param;

public interface JoinRepository {
	
	// 이메일 유무 확인 (join)
	public int emailCount(String email);

	//회원가입 (join)
	public int joinMember(HashMap<String, String> params);
	
	//아이디 중복검사 (join)
	public List<String> memberSelectAll();
	
	//멤버 seq 가져오기 (join)
	public Integer getMemberSeq(String memberID);
	
	//멤버지우기 (join)
	public int deleteMember(int memberSeq);
	
	//addAuthInfo (join)
	public int addAuthInfo(MemberAuthDto memberAuthDto);
	
	//authURI가져오기 (join)
	public List<HashMap<String, Object>> authURI();
	
	//updateAuth (join)
	public int updateAuth(EmailAuthDto emailAuthDto);
	
	//updateMemAuth (join)
	public int updateMemAuth(@Param("memberSeq") int memberSeq);
}
