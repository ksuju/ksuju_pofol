package com.ksuju.www.service;

/**
 * packageName    : com.ksuju.www.service
 * fileName       : ContactService
 * author         : sungjun
 * date           : 2024-12-24
 * description    : 자동 주석 생성
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-12-24        kyd54       최초 생성
 */

import com.ksuju.www.dto.EmailDto;
import com.ksuju.www.dto.ResumeDto;
import com.ksuju.www.repository.ResumeRepository;
import com.ksuju.www.util.EmailUtil;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service("contactService")
@RequiredArgsConstructor
public class ContactService {

    private final ResumeRepository resumeRepository;

    private final EmailUtil emailUtil;

    public boolean sendResume(String name, String email) {

        try {
            // 인증 메일 발송
            EmailDto sendResume = new EmailDto();
            sendResume.setReceiver(email);
            sendResume.setSubject(name+"님 안녕하세요! 강성준 이력서 보내드립니다.");
            // host + contextRoot + URI
            String text = "매사 항상 성실하게, 모르는 것은 배우고자 하는 자세로 기꺼이 임하겠습니다!";
            sendResume.setText(text);

            // 이력서 가져오기
            ResumeDto resume = resumeRepository.resume();

            if (resume != null && resume.getFileData() != null) {
                sendResume.setResume(resume.getFileData()); // 이력서 파일 데이터 설정
                emailUtil.sendResume(sendResume, true);
                resumeRepository.resumeRec(name,email);
                return true;
            } else {
                throw new Exception("이력서 파일 데이터를 가져오지 못했습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
