package com.ksuju.www.repository;

import com.ksuju.www.dto.ResumeDto;
import org.apache.ibatis.annotations.Param;

public interface ResumeRepository {
	
	// 이력서 가져오기 이용 기록
	public int resumeRec(@Param("name")String name,
			@Param("email")String email);
	
	// 이력서 가져오기
	public ResumeDto resume();

}
