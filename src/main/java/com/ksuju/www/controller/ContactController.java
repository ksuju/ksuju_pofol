package com.ksuju.www.controller;

import java.util.Calendar;

import com.ksuju.www.service.ContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class ContactController {

    private final static Logger logger = LoggerFactory.getLogger(ContactController.class);

    private final ContactService contactService;

    @RequestMapping("resume.do")
    public ModelAndView sendResume(@RequestParam String name,
                                   @RequestParam String email) {
        logger.info("resume.do");
        ModelAndView mv = new ModelAndView();
        mv.addObject("key", Calendar.getInstance().getTimeInMillis());
        mv.setViewName("util/contact");

        // 이메일 유효성 검사
        boolean checkEmailValid = isValidEmail(email);
        // 이메일 유효성 검사
        if(!checkEmailValid) {
            mv.addObject("alert","올바른 이메일 주소를 입력해 주세요!");
            return mv;
        }

        if(name.isEmpty() || email.isEmpty()) {
            mv.addObject("alert","이름과 이메일 입력란을 모두 입력해 주세요!");
            return mv;
        }

        boolean result = contactService.sendResume(name, email);

        if(result) {
            logger.info("이력서 전송에 성공했습니다.");
            mv.addObject("success","이력서 전송에 성공했습니다!");
            return mv;
        } else {
            logger.info("이력서 전송에 실패했습니다.");
            return mv;
        }
    }

    private boolean isValidEmail(String email) {
        // 이메일 유효성 검사 정규표현식
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}

