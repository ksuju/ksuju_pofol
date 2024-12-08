package com.ksuju.www.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;


import com.ksuju.www.dto.EmailDto;
import com.ksuju.www.message.MessageEnum;
import com.ksuju.www.repository.AuthRepository;
import com.ksuju.www.repository.JoinRepository;
import com.ksuju.www.repository.LoginRepository;
import com.ksuju.www.util.EmailUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.ibatis.binding.BindingException;
import org.springframework.stereotype.Service;


import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {

	private final JoinRepository joinRepository;
	
	private final AuthRepository authRepository;
	
	private final LoginRepository loginRepository;

	private final EmailUtil emailutil;
	
	// 비밀번호 찾기 할 때 입력한 아이디와 이메일로부터 가져온 db에 있는 아이디 비교
	public boolean compareID(String email, String name) {
		String dbMemberID = authRepository.compareID(email);
		
		System.out.println("============== LoginService > compareID ===============");
		
		if(!dbMemberID.isEmpty() && dbMemberID.equals(name)) {
			return true;
		} 
		return false;
	}
	
	// 아이디 저장 쿠키 생성
	public boolean saveIdCookie(HashMap<String, String> params, HttpServletResponse response) {
		String memberId = params.get("memberID");

		// on 또는 null
		String checkBox = params.get("checkBox");
		
		try {
			joinRepository.getMemberSeq(memberId);
			// 체크박스가 "on"일때 입력받은 아이디 쿠키로 저장
			if("on".equals(checkBox)) {
				Cookie saveId = new Cookie("saveId",memberId);
				saveId.setMaxAge(60 * 60 * 24 * 7); // 만료기한 7일
				response.addCookie(saveId);
				return true;
			} else {
				Cookie saveId = new Cookie("saveId",memberId);
				saveId.setMaxAge(0); // 만료기한 0초 > 삭제 > 쿠키를 삭제하는 기능은 없기에 만료시간을 0으로 설정함
				response.addCookie(saveId);
				return false;
			}
			
		} catch (BindingException | NullPointerException be) {
			return false;
		}

    }
	
	// 비밀번호 변경
	public int changePasswd(HashMap<String, String> params) {
		// BCrypt를 사용한 비밀번호 암호화
		String passwd = params.get("passwd");
		String encPasswd = BCrypt.withDefaults().hashToString(12, passwd.toCharArray());
		// System.out.println("encPasswd >>>>>>>>> " + encPasswd);
		// System.out.println("result.verified >>>>>>> " + result.verified);

		// wrapper 클래스 Long 사용 > NULL 반환 허용
		long expireDtm = authRepository.getExpireDtm(params.get("email"));

		long now = System.currentTimeMillis();

		if (expireDtm > now) {
			params.put("passwd", encPasswd);
			// BCrypt를 사용한 비밀번호 암호화 끝
			// 인증번호 null로 바꾸기
			authRepository.updateAuthNumToNull(params.get("email"));

			return loginRepository.changePasswd(params);
		} else {
			// 인증번호 null로 바꾸기
			authRepository.updateAuthNumToNull(params.get("email"));
			return Integer.parseInt(MessageEnum.EXPIRE_AUTH_DTM.getCode());
		}
	}

	// 비밀번호 변경을 위한 메일 인증 완료
	public int emailAuthPw(HashMap<String, String> params) {
		int dbAuthNum = authRepository.authNumSelect(params.get("email"));
		String authNumString = params.get("authNum");
		int authNum = 0;

		// controller로 부터 authNum을 제대로 받아왔을때
		if (authNumString != null) {
			authNum = Integer.parseInt(authNumString);

			// db에 저장된 auth_num과 인증메일로부터 받은 authNum이 일치하면
			if (dbAuthNum == authNum) {
				// 1(성공) 을 반환함
				return 1;
			}
		}
		// 실패
		return 0;
	}

	// 인증번호 생성 및 메일 발송
	public int updateAuthNum(HashMap<String, String> params) {
		int cnt = authRepository.updateAuthNum(params);
		int emailExist = joinRepository.emailCount(params.get("email"));
		
		if (emailExist == 0) {
			return Integer.parseInt(MessageEnum.VALLID_EMAIL.getCode());
		}
		
		if (cnt == 1) {
			// 인증 메일 발송
			EmailDto email = new EmailDto();
			email.setReceiver(params.get("email"));
			email.setSubject("인증 메일입니다 인증번호를 확인해주세요.");
			// host + contextRoot + URI
			//String html = emailprop.getPwChangeUri() + params.get("authNum") + "&email=" + params.get("email")
			//		+ "'>비밀번호 변경하기</a>";
			
			//String timestamp = String.valueOf(System.currentTimeMillis());
			
			//String uri = emailprop.getPwChangeUri() + params.get("authNum") + "&email=" + params.get("email") + "&t=" + timestamp + "'>비밀번호 변경하기</a>";
			
			String auth = "<p>" + params.get("authNum") + "</p>";
					
			//String html = "<html><body><p>비밀번호 변경을 위해 아래 링크를 클릭하세요:</p>" + uri + "</body></html>";

			//email.setText(html);
			email.setText(auth);

			emailutil.sendMail(email, true);
			return cnt;
		}
		return cnt;
	}

	public int loginCheck(String memberID, String passwd, HttpServletRequest request) {
		String dbPasswd = loginRepository.loginCheck(memberID);
		//String getAuthYN = noticeRepository.getAuthYN(memberID);
		System.out.println("================ LoginService > loginCheck 진입 ================");
		
		//if (dbPasswd != null && ("Y").equals(getAuthYN)) {	
		if (dbPasswd != null) {	
			BCrypt.Result result = BCrypt.verifyer().verify(passwd.toCharArray(), dbPasswd);

			if(result.validFormat && result.verified) {
				// 비밀번호가 일치하는 경우
				// 로그인 성공 처리
				HttpSession session = request.getSession();
				session.setAttribute("logInUser", memberID);
				session.setAttribute("loggedIn", true);
				
				// 현재 시스템 시간 가져오기
				LocalDate now = LocalDate.now();
				
		        // DateTimeFormatter를 사용하여 LocalDate를 String으로 포맷
		        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 원하는 형식을 지정
		        String formattedDate = now.format(formatter);
				
				// 로그인 성공 시 로그인 기록 남기기
		        loginRepository.saveLoginLog(memberID,formattedDate);
				
				return Integer.parseInt(MessageEnum.SUCCESS.getCode());
			}
		} else {
			System.out.println("================== 가입되지 않은 아이디 ==================");
			return -1;
		}
		/*
		 * else if(!("Y").equals(getAuthYN)) {
		 * System.out.println("================== 인증되지 않은 아이디 ==================");
		 * return Integer.parseInt(MessageEnum.NOT_EMAIL_AUTH.getCode()); }
		 */
		return Integer.parseInt(MessageEnum.FAILED.getCode());
	}
}
