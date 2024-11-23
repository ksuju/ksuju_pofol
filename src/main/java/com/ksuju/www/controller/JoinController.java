package com.ksuju.www.controller;

import com.ksuju.www.dto.EmailAuthDto;
import com.ksuju.www.message.MessageEnum;
import com.ksuju.www.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Calendar;
import java.util.HashMap;

@Controller
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;

    // 이메일 중복확인 버튼 > 이메일 중복, 유효성 검사
    @ResponseBody
    @RequestMapping("/auth/emailCheck.do")
    public int emailCheck(@RequestParam String email) {

        int seq = joinService.emailCount(email);

        return seq;
    }

    // joinPage 진입
    @RequestMapping("/auth/joinPage.do")
    public String joinPage(Model model) {
		model.addAttribute("key", Calendar.getInstance().getTimeInMillis());
        model.addAttribute("MessageEnum", MessageEnum.class);

        System.out.println("==============================JoinController > joinPage.do진입==============================");
        return "auth/join";
    }

    // 이메일 인증 체크
    @RequestMapping("emailAuth.do")
    public String emailAuth(@RequestParam HashMap<String, String> params
    , Model model) {
        System.out.println("====================emailAuth진입====================" + params);
        model.addAttribute("key", Calendar.getInstance().getTimeInMillis());
        // params.value (authURI)와 db에서 불러온 authURI를 비교해야함
        if (joinService.authURI(params.get("uri")) != null) {
            EmailAuthDto emailAuthDto = joinService.authURI(params.get("uri"));

            // 트랜잭션으로 auth_yn 업데이트
            if (joinService.updateAuthAndMemAuth(emailAuthDto)) {
                System.out.println("인증성공");
                return "auth/login";
            }
        }
        System.out.println("인증실패");
        return "basic/index";
    }

    // 비밀번호 유효성 체크 기능
    @ResponseBody
    @RequestMapping("/auth/validPasswd.do")
    public int validPasswd(@RequestParam String passwordCheck) {
        System.out.println("==============================JoinController > passCheck.do진입==============================");
        return joinService.validPasswd(passwordCheck);
    }

    // 비밀번호 = 비밀번호확인 체크 기능
    @ResponseBody
    @RequestMapping("/auth/passCheck.do")
    public int passCheck(@RequestParam String conPassCheck, @RequestParam String passwordCheck) {
        System.out.println("==============================JoinController > passCheck.do진입==============================");

        return joinService.passCheck(conPassCheck, passwordCheck);
    }

    // 아이디 중복체크 기능
    @ResponseBody
    @RequestMapping("/auth/idCheck.do")
    public int idCheck(@RequestParam String idCheck) {
        System.out.println("==============================JoinController > idCheck.do진입==============================");
        int idCheckCode = joinService.idCheck(idCheck);

        // 유효한 아이디
        if (idCheckCode == Integer.parseInt(MessageEnum.DUPL_ID.getCode())) {
            System.out.println("controller > idCheck > 아이디중복");
            System.out.println("==============================JoinController > idCheck.do종료==============================");
            return idCheckCode;
        } else {
            System.out.println("controller > idCheck > 아이디중복없음");
            System.out.println("==============================JoinController > idCheck.do종료==============================");
            return idCheckCode;
        }
    }

    // 회원가입 기능
    @ResponseBody
    @RequestMapping("/auth/join.do")
    public String join(@RequestParam HashMap<String, String> params) {

        System.out.println("==============================JoinController > join.do진입==============================");

        System.out.println("params 값 확인 ==================>" + params);

        // System.out.println("join params 확인 ========================>"+params);

        // 모든 조건을 통과한 경우 회원가입 진행
        int joinCheck = joinService.joinMember(params);

        if (joinCheck == 1) {
            System.out.println("=====================회원가입 성공 확인=====================");
            System.out.println("==============================JoinController > join.do종료==============================");
            return MessageEnum.SUCCESS.getCode();
        } else if (joinCheck == Integer.parseInt(MessageEnum.VALLID_USER_NAME.getCode())) {
            // 유효하지 않은 아이디.
            return MessageEnum.VALLID_USER_NAME.getCode();
        } else if (joinCheck == Integer.parseInt(MessageEnum.VALLID_PASSWD.getCode())) {
            // 유효하지 않은 패스워드.
            return MessageEnum.VALLID_PASSWD.getCode();
        } else if (joinCheck == Integer.parseInt(MessageEnum.NOT_EQUAL_PASSWD.getCode())) {
            // 비밀번호가 일치하지 않습니다.
            return MessageEnum.NOT_EQUAL_PASSWD.getCode();
        } else if (joinCheck == Integer.parseInt(MessageEnum.DUPL_EMAIL.getCode())) {
            // 이미 가입된 이메일 주소입니다.
            return MessageEnum.DUPL_EMAIL.getCode();
        } else {
            System.out.println("=====================회원가입 실패 확인=====================");
            System.out.println("==============================JoinController > join.do종료==============================");
            return MessageEnum.FAILED.getCode();
        }
    }
}
