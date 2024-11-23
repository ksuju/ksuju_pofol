package com.ksuju.www.repository;

import java.util.HashMap;

import org.apache.ibatis.annotations.Param;

public interface MemberRepository {
	
	// reg_member_seq로 member_id 가져오기 (member)
	public HashMap<String, String> selectMemberId(int regMemberSeq);
	
	// 계정인증여부 가져오기 (member)
	public String getAuthYN(String memberID);
	
	// memberID로 email 가져오기
	public String getEmail(@Param("memberID") String memberID);
}
