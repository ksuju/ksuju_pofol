package com.ksuju.www.controller;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


import com.ksuju.www.message.MessageEnum;
import com.ksuju.www.service.LoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class LoginController {

	private final LoginService loginService;
	
	// 아이디찾기 페이지로 이동 & 아이디 변경
	@RequestMapping("/auth/findID.do")
	public ModelAndView findID() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("key", Calendar.getInstance().getTimeInMillis());
		mv.setViewName("auth/findID");
		
		return mv;
	}

	// 비밀번호 변경페이지로 이동 & 비밀번호 변경
	@RequestMapping("/auth/resetPw.do")
	public ModelAndView resetPw(@RequestParam HashMap<String, String> params, Model model) {
		ModelAndView mv = new ModelAndView();
		mv.addObject("key", Calendar.getInstance().getTimeInMillis());

		if (!params.isEmpty()) {
			mv.setViewName("login/login");
			if (isValidPassword(params.get("passwd"))) {
				int changePW = loginService.changePasswd(params);
				if (changePW == 1) {
					return mv;
				} else if (changePW == Integer.parseInt(MessageEnum.EXPIRE_AUTH_DTM.getCode())) {
					mv.setViewName("login/login");
					model.addAttribute("alert", MessageEnum.EXPIRE_AUTH_DTM.getDescription());
					return mv;
				}
			}
			mv.setViewName("auth/passwordChange");
			model.addAttribute("email", params.get("email"));
			model.addAttribute("alert", MessageEnum.VALLID_PASSWD.getDescription());
			return mv;
		}

		mv.setViewName("auth/recoverPassword");
		return mv;
	}

	@RequestMapping("/logout.do")
	public String logout(HttpServletRequest request) {

		System.out.println("==================logout진입==================");
		// 세션 무효화
		request.getSession().invalidate();

		return "redirect:/pf/index.do";
	}

	@RequestMapping("/auth/login.do")
	public String login(@RequestParam HashMap<String, String> params
			, HttpServletRequest request
			, HttpServletResponse response
			, Model model) {
		
		System.out.println("======== LoginController > login.do ========");
		
		String memberId = params.get("memberID");
		
		// 아이디저장 클릭시 쿠키 생성
		loginService.saveIdCookie(params, response);
		
		// 아이디로 비밀번호 찾기
		int loginCheck = loginService.loginCheck(memberId, params.get("passwd"), request);
		
	    // 허용되지 않은 리다이렉트 URL 목록
	    Set<String> forbiddenRedirects = new HashSet<>(Arrays.asList(
	        "convertPw.do",
	        "resetPw.do",
	        "sendID.do",
	        "joinPage.do",
	        "findID.do"
	    ));

	    if (loginCheck == Integer.parseInt(MessageEnum.SUCCESS.getCode())) {

	        // 비밀번호가 일치하는 경우
	        String redirectUrl = params.get("redirectUrl"); // redirectUrl을 HashMap에서 가져오기

	        if (redirectUrl != null && !redirectUrl.isEmpty()) {
	            // 리다이렉트 URL에서 마지막 경로만 비교
	            String lastPath = redirectUrl.substring(redirectUrl.lastIndexOf("/") + 1);
	            if (forbiddenRedirects.contains(lastPath)) {
	                return "redirect:/pf/index.do"; // forbiddenRedirects에 포함된 경우 index로 리다이렉트
	            }
	            return "redirect:" + redirectUrl; // 이전 페이지로 리다이렉트
	        }

	        return "redirect:/pf/index.do"; // 기본 리다이렉트 페이지
	    }
		
		// 비밀번호가 일치하지 않는 경우
		// 로그인 실패 처리
		System.out.println("로그인실패==========================");
		// 쿠키삭제
		deleteCookie(request);
		model.addAttribute("errorMessage", MessageEnum.LOGIN_FAILD.getDescription());
		return "login/login";
	}

	@RequestMapping("/auth/loginPage.do")
	public ModelAndView loginPage(@RequestParam HashMap<String, String> params
			, HttpServletRequest request) {
		System.out.println("========================== LoginController > loginPage ========================");
		ModelAndView mv = new ModelAndView();
		mv.addObject("key", Calendar.getInstance().getTimeInMillis());

		HttpSession session = request.getSession();

		/*
		헤더 로그인 버튼 여러번 누른 후 로그인시
		인덱스로 이동하지 않고 로그인페이지에 남아있는 버그 수정
		*/
		Boolean isLogin = (Boolean) session.getAttribute("loggedIn");
		if(isLogin != null && isLogin) {
			mv.setViewName("basic/index");
			return mv;
		}

		// 아이디저장 쿠키가 존재하면 request에 saveId라는 이름으로 저장됨
		existCookie(request);
        // request에 설정된 속성을 ModelAndView에 추가
        String saveId = (String) request.getAttribute("saveId");
        mv.addObject("saveId", saveId);
        
        String referer = request.getHeader("Referer"); // 이전 URL 가져오기

        System.out.println("====== referer ============== referer =================");
        
        if (referer == null || referer.isEmpty()) {
            referer = "/pf/index.do"; // 기본 페이지 설정
        }

        mv.addObject("redirectUrl", referer); // redirect url

		mv.setViewName("login/login");
		return mv;
	}

	// 비밀번호 제약사항 - 비밀번호는 8~16자의 영문, 숫자, 특수문자를 1개 이상 포함해야 함.
	private boolean isValidPassword(String password) {
		return password != null && password.length() >= 8 && password.length() <= 16 && password.matches(".*[a-z].*")
				&& password.matches(".*\\d.*") && password.matches(".*[!@#$%^&*()\\-_=+\\[\\]{}|;:'\",.<>?/].*");
	}
	
	
	// 쿠키에 저장된 아이디가 있을 때 request에 saveId라는 이름으로 저장
	private void existCookie(HttpServletRequest request) {
		String memberId = "";
		
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("saveId")) {
            	memberId = cookie.getValue();
            	break; // 쿠키를 찾았으니 더 이상 반복할 필요 없음
            }
        }
        request.setAttribute("saveId", memberId);
	}
	
	// 쿠키에 저장된 아이디 삭제
	private void deleteCookie(HttpServletRequest request) {
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if (cookie.getName().equals("saveId")) {
	                cookie.setMaxAge(0); // 쿠키의 유효 시간을 0으로 설정하여 삭제
	                cookie.setPath("/"); // 쿠키의 경로 설정 > 모든 페이지에서 삭제함
	                HttpServletResponse response = (HttpServletResponse) request.getAttribute("response");
	                try {
	                	response.addCookie(cookie); // 응답에 쿠키 추가
		                break;
	                } catch (NullPointerException ne) {
	                	System.out.println("============쿠키삭제 null============");
	                }
	            }
	        }
	    }
	}
}
