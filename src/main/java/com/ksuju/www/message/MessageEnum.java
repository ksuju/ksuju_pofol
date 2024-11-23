package com.ksuju.www.message;

public enum MessageEnum {

    //생성자 무조건 필요, 앞에 부분을 생성자로 취급
    SUCCESS("0000","회원가입성공"),
    FAILED("0001","회원가입실패"),
    USER_NOT_FOUND("9000","사용자가 없습니다"),
	VALLID_USER_NAME("1100","유효하지 않은 아이디."),
	VALLID_PASSWD("1101","유효하지 않은 패스워드."),
	EQUAL_PASSWD("2000","비밀번호가 일치합니다."),
	NOT_EQUAL_PASSWD("2001","비밀번호가 일치하지 않습니다."),
	DUPL_ID("1103","중복된 아이디 입니다."),
	NO_DUPL_ID("1104","사용 가능한 아이디 입니다."),
	LOGIN_FAILD("9999","로그인에 실패하셨습니다."),
	DUPL_EMAIL("3000","이미 가입된 이메일 주소입니다."),
	EXPIRE_AUTH_DTM("99999","인증기간이 만료되었습니다."),
	NOT_EMAIL_AUTH("1102","이메일 인증이 완료되지 않은 아이디입니다."),
	VALLID_EMAIL("1102","유효하지 않은 이메일입니다."),
	SUCCESS_BOARD("4000","게시글을 작성하였습니다."),
	FAILD_BOARD("4001","게시글 작성에 실패했습니다."),
	FAILD_BOARD_LOGIN("4002","로그인이 필요한 작업입니다."),
	FAILD_BOARD_SIZE("4100","파일의 크기는 1MB를 넘을 수 없습니다.")
	;

    MessageEnum(String code, String description){
        this.code = code;
        this.description = description;
    }

    private String code;
    private String description;

    //Enum은 setter 필요X, getter만 필요
    public String getCode() {
        return code;
    }
    public String getDescription() {
        return description;
    }
}