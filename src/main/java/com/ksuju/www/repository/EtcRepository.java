package com.ksuju.www.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * packageName    : com.ksuju.www.repository
 * fileName       : EtcRepository
 * author         : sungjun
 * date           : 2024-11-19
 * description    : 자동 주석 생성
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-11-19        kyd54       최초 생성
 */

public interface EtcRepository {

    // 특정 카테고리 식사메뉴 가져오기
    public List<String> menu(@Param("category")String category);

    // 모든 식사메뉴 카테고리 가져오기
    public List<String> menuCategory();

    // 모든 카테고리에서 식사메뉴 가져오기
    public List<String> allMenu();
}
