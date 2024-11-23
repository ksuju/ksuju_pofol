package com.ksuju.www.controller;

import java.util.Calendar;
import java.util.HashMap;


import com.ksuju.www.message.MessageEnum;
import com.ksuju.www.service.AuthService;
import com.ksuju.www.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {
	
	private final LoginService loginService;

	private final AuthService authService;
	
	// 아이디찾기
	@RequestMapping("/auth/sendID.do")
	public String sendID(@RequestParam String name,
			@RequestParam String email
	,Model model) {
		
		System.out.println("sendID=================================");

		model.addAttribute("key", Calendar.getInstance().getTimeInMillis());

		try {
			String userID = authService.findID(name, email);
			
			if(userID.equals("이름X") || userID.equals("이메일X")) {
				model.addAttribute("alert", "이름 또는 이메일 주소를 다시 입력해주세요.");
				return "/auth/findID";
			}

			model.addAttribute("findID", userID);
			return "/auth/findYourID";
		} catch (NullPointerException e) {
			// TODO: handle exception
			model.addAttribute("alert", "이름 또는 이메일 주소를 다시 입력해주세요.");
			return "/auth/findID";
		}
		
	}
	
	// 아이디인증
	@RequestMapping("/auth/idAuth.do")
	public String idAuth(@RequestParam String authNum,
			@RequestParam String email,
			HttpServletRequest request
	,Model model) {
		model.addAttribute("key", Calendar.getInstance().getTimeInMillis());

		//인증로직
		boolean result = authService.authNum(authNum, email);
		
		if(result) {
			authService.cvtAuthYN(email, request);
			model.addAttribute("alert", "계정 인증이 완료되었습니다!");
			return "basic/index";
		} else {
			model.addAttribute("alert", "인증번호가 틀렸습니다.");
			model.addAttribute("idAuth", "idAuth");
			model.addAttribute("email", email);
			return "auth/authNum";
		}
	}
	

	// 비밀번호 인증번호 인증하기
	@RequestMapping("/auth/findpswd.do")
	public String authNum(@RequestParam String authNum,
			@RequestParam String email,
			Model model) {
		model.addAttribute("key", Calendar.getInstance().getTimeInMillis());
		
		//인증로직
		boolean result = authService.authNum(authNum, email);
		
		if(result) {
			model.addAttribute("email", email);
			return "auth/passwordChange";
		} else {
			model.addAttribute("alert", "인증번호가 틀렸습니다.");
			model.addAttribute("email", email);
			return "auth/authNum";
		}
	}
	
	// 비밀번호 변경 인증번호 생성 및 인증메일 발송
	@RequestMapping("/auth/convertPw.do")
	public String convertPw(@RequestParam String email, @RequestParam String name
			,Model model) {

		model.addAttribute("key", Calendar.getInstance().getTimeInMillis());
		System.out.println("====================convertPw controller 진입====================");
		
		// db에 저장된 유저아이디와 입력받은 유저아이디 비교
		if(email.isEmpty() || name.isEmpty()) {
			// 빨간글씨로 띄울거 알람보냄
			model.addAttribute("alert", "아이디와 이메일을 모두 입력해주세요.");
			return "auth/recoverPassword";
		} else if (!loginService.compareID(email, name)) {
			// 빨간글씨로 띄울거 알람보냄
			model.addAttribute("alert", "입력하신 이메일 주소와 아이디가 일치하지 않습니다.");
			return "auth/recoverPassword";
		} else {
			// 6자리의 인증번호 생성
			int authNum = (int) (Math.random() * (999999 - 100000 + 1)) + 100000;

			HashMap<String, String> updateAuthNum = new HashMap<>();

			updateAuthNum.put("authNum", String.valueOf(authNum));
			updateAuthNum.put("email", email);

			int cnt = loginService.updateAuthNum(updateAuthNum);

			if (cnt == 1) {
				//mv.setViewName("auth/login");
				// 빨간글씨로 띄울거 알람보냄
				model.addAttribute("alert", "이메일을 확인하세요.");
				model.addAttribute("email", email);
				return "auth/authNum";
			} else if (cnt == Integer.parseInt(MessageEnum.VALLID_EMAIL.getCode())) {
				// 빨간글씨로 띄울거 알람보냄
				model.addAttribute("alert", MessageEnum.VALLID_EMAIL.getDescription());
				return "auth/recoverPassword";
			} else {
				// 빨간글씨로 띄울거 알람보냄
				model.addAttribute("alert", "이메일 인증 실패");
				return "auth/recoverPassword";
			}
		}
	}


	// 인증메일 인증 후 비밀번호 변경 **지금 사용안함
/*
	 * @RequestMapping("emailAuthPw.do") public ModelAndView
	 * emailAuthPw(@RequestParam String authNum, @RequestParam String email, Model
	 * model) { System.out.
	 * println("==========================emailAuthPw controller진입========================"
	 * ); ModelAndView mv = new ModelAndView(); mv.addObject("key",
	 * Calendar.getInstance().getTimeInMillis());
	 * 
	 * HashMap<String, String> emailAuthPw = new HashMap<>();
	 * emailAuthPw.put("authNum", String.valueOf(authNum)); emailAuthPw.put("email",
	 * email);
	 * 
	 * // 1(성공) 또는 0(실패)를 반환함 int cnt = loginService.emailAuthPw(emailAuthPw); //
	 * param으로 받은 번호화 db에 저장된 인증번호 비교 후 같으면 비밀번호 변경 if (cnt == 1) {
	 * mv.setViewName("auth/passwordChange"); model.addAttribute("email", email);
	 * 
	 * return mv; } return mv; }
*/
}
