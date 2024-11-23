package com.ksuju.www.dto;

public class MemberAuthDto {
	private int authSeq;
	private int memberSeq;
	private String authNum;
	private String authUri;
	private String regDtm;
	private Long expireDtm;
	private String authYn;
	
	public int getAuthSeq() {
		return authSeq;
	}
	public void setAuthSeq(int authSeq) {
		this.authSeq = authSeq;
	}
	public int getMemberSeq() {
		return memberSeq;
	}
	public void setMemberSeq(int memberSeq) {
		this.memberSeq = memberSeq;
	}
	public String getAuthNum() {
		return authNum;
	}
	public void setAuthNum(String authNum) {
		this.authNum = authNum;
	}
	public String getAuthUri() {
		return authUri;
	}
	public void setAuthUri(String authUri) {
		this.authUri = authUri;
	}
	public String getRegDtm() {
		return regDtm;
	}
	public void setRegDtm(String regDtm) {
		this.regDtm = regDtm;
	}
	public Long getExpireDtm() {
		return expireDtm;
	}
	public void setExpireDtm(Long expireDtm) {
		this.expireDtm = expireDtm;
	}
	public String getAuthYn() {
		return authYn;
	}
	public void setAuthYn(String authYn) {
		this.authYn = authYn;
	}

}
