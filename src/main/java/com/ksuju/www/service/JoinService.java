package com.ksuju.www.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ksuju.www.dto.EmailAuthDto;
import com.ksuju.www.dto.MemberAuthDto;
import com.ksuju.www.message.MessageEnum;
import com.ksuju.www.repository.JoinRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.RequiredArgsConstructor;

@Service("joinService")
@RequiredArgsConstructor
public class JoinService {

	private final JoinRepository joinRepository;

	// auth_yn 변경하기 트랜잭션으로 member_auth 테이블과 member 테이블에 있는 auth_yn 컬럼을 동시에 바꿈
	@Transactional
	public boolean updateAuthAndMemAuth(EmailAuthDto emailAuthDto) {

		// auth_yn 변경하기 (member_auth 테이블)
		int updateAuth = joinRepository.updateAuth(emailAuthDto);
		// auth_yn 변경하기 (member 테이블)
		int updateMemAuth = joinRepository.updateMemAuth(emailAuthDto.getMemberSeq());

		if (updateMemAuth == 1 && updateAuth == 1) {
			return true;
		}
		return false;
	}

	// 비밀번호 유효성 체크 기능
	public int validPasswd(String passwordCheck) {
		// 비밀번호 제약사항 체크
		if (!isValidPassword(passwordCheck)) {
			return Integer.parseInt(MessageEnum.VALLID_PASSWD.getCode());
		}
		return 999999;
	}

	// 비밀번호 = 비밀번호확인 체크 기능
	public int passCheck(String conPassCheck, String passwordCheck) {
		// 비밀번호 제약사항 체크
		if (isValidPassword(passwordCheck)) {
			// 비밀번호 비교
			if (conPassCheck.equals(passwordCheck)) {
				System.out.println("controller > passCheck > 비밀번호 같음");
				System.out.println(
						"==============================JoinController > passCheck.do종료==============================");
				return Integer.parseInt(MessageEnum.EQUAL_PASSWD.getCode());
			} else {
				System.out.println("controller > passCheck > 비밀번호 다름");
				System.out.println(
						"==============================JoinController > passCheck.do종료==============================");
				return Integer.parseInt(MessageEnum.NOT_EQUAL_PASSWD.getCode());
			}
		} else {
			return Integer.parseInt(MessageEnum.VALLID_PASSWD.getCode());
		}
	}

	// 이메일 인증하기
	public EmailAuthDto authURI(String authURI) {
		List<HashMap<String, Object>> dbAuthURI = joinRepository.authURI();

		for (HashMap<String, Object> auth : dbAuthURI) {
			String authUri = (String) auth.get("auth_uri");
			if (authURI.equals(authUri)) {
				Integer memberSeq = (Integer) auth.get("member_seq");
				System.out.println("======service > authURI > email인증성공========");

				return new EmailAuthDto(memberSeq, authUri);
			}

		}
		System.out.println("======service > authURI > email인증실패========");
		return null;
	}

	// 멤버지우기
	public int deleteMember(int memberSeq) {
		return joinRepository.deleteMember(memberSeq);
	}

	// 멤버시퀀스가져오기
	public int getMemberSeq(String memberID) {
		return joinRepository.getMemberSeq(memberID);
	}

	public int idCheck(String idCheck) {
		System.out.println("========================service > idCheck========================");
		List<String> memberList = joinRepository.memberSelectAll();
		if (isValidUsername(idCheck)) {
			if (memberList.contains(idCheck)) {
				System.out.println("========================service > idCheck > 아이디중복========================");
				return Integer.parseInt(MessageEnum.DUPL_ID.getCode());
			} else {
				System.out.println("========================service > idCheck > 아이디중복없음========================");
				return Integer.parseInt(MessageEnum.NO_DUPL_ID.getCode());

			}
		}
		// 유효하지 않은 아이디
		return Integer.parseInt(MessageEnum.VALLID_USER_NAME.getCode());
	}

	public int joinMember(HashMap<String, String> params) {
		System.out.println("========================service > joinMember========================");

		String username = params.get("memberID");
		String password = params.get("passwd");
		String confirmPassword = params.get("con_pass");

		// 아이디 유효성 검사
		if (!isValidUsername(username)) {
			System.out.println("유효하지 않은 아이디.");
			return Integer.parseInt(MessageEnum.VALLID_USER_NAME.getCode());
		}

		// 비밀번호 유효성 검사
		if (!isValidPassword(password)) {
			System.out.println("유효하지 않은 패스워드.");
			return Integer.parseInt(MessageEnum.VALLID_PASSWD.getCode());
		}

		// 비밀번호와 확인용 비밀번호가 일치하는지 확인
		if (!password.equals(confirmPassword)) {
			System.out.println("비밀번호가 일치하지 않습니다.");
			return Integer.parseInt(MessageEnum.NOT_EQUAL_PASSWD.getCode());
		}

		// 아이디가 사용 가능한지 확인하는 조건
		int idCheckCode = idCheck(username);
		if (idCheckCode == Integer.parseInt(MessageEnum.DUPL_ID.getCode())) {
			System.out.println("중복된 아이디 입니다.");
			return Integer.parseInt(MessageEnum.DUPL_ID.getCode());
		}

		// BCrypt를 사용한 비밀번호 암호화
		String passwd = params.get("passwd");
		String encPasswd = BCrypt.withDefaults().hashToString(12, passwd.toCharArray());
		// System.out.println("encPasswd >>>>>>>>> " + encPasswd);
		// BCrypt.Result result = BCrypt.verifyer().verify(passwd.toCharArray(),
		// encPasswd);
		// System.out.println("result.verified >>>>>>> " + result.verified);

		params.put("passwd", encPasswd);
		// BCrypt를 사용한 비밀번호 암호화 끝

		try {
			int cnt = joinRepository.joinMember(params);
			
			// member_auth 추가
			 int memberSeq = joinRepository.getMemberSeq(params.get("memberID")); // 인증메일구조 만들기
			 MemberAuthDto authDto = new MemberAuthDto();
			 authDto.setMemberSeq(memberSeq);
			 // UUID
			 authDto.setAuthUri(UUID.randomUUID().toString().replaceAll("-", ""));
			 
			 Calendar cal = Calendar.getInstance(); cal.add(Calendar.MINUTE, 30); // 30분만 유효
			 authDto.setExpireDtm(cal.getTimeInMillis());
			 
			 joinRepository.addAuthInfo(authDto);
			 
			 return cnt;
		} catch (DuplicateKeyException sqlEx) {
			System.out.println("이미 가입된 이메일 입니다.");
			return Integer.parseInt(MessageEnum.DUPL_EMAIL.getCode());
		}

	}

	// 이메일 중복확인 버튼 > 이메일 중복, 유효성 검사
	public int emailCount(String email) {
		
		if (joinRepository.emailCount(email) == 1) {
			return Integer.parseInt(MessageEnum.DUPL_EMAIL.getCode());
		}
		if (!isValidEmail(email)) {
			return Integer.parseInt(MessageEnum.VALLID_EMAIL.getCode());
		}
		
		return 1;
	}

	// 아이디 제약사항 - 아이디는 공백 또는 빈 칸일 수 없고 4~20자의 영어 소문자, 숫자만 사용 가능함.
	private boolean isValidUsername(String username) {
		return username != null && username.matches("^[a-z0-9]{4,20}$");
	}

	// 비밀번호 제약사항 - 비밀번호는 8~16자의 영문, 숫자, 특수문자를 1개 이상 포함해야 함.
	private boolean isValidPassword(String password) {
		return password != null && password.length() >= 8 && password.length() <= 16 && password.matches(".*[a-zA-Z].*")
				&& // 영문 대소문자
				password.matches(".*\\d.*") && // 숫자
				password.matches(".*[!@#$%^&*()\\-_=+\\[\\]{}|;:'\",.<>?/].*"); // 특수문자
	}

	// 이메일 제약사항
	// @가 하나 들어가야 하고 마지막은 .com으로 끝나야함
	private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@"
	        + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

	public static boolean isValidEmail(String email) {
		Pattern pattern = Pattern.compile(EMAIL_REGEX);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
}
