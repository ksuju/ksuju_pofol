package com.ksuju.www.dto;

public class EmailAuthDto {
    private Integer memberSeq;
    private String authUri;
    
    public EmailAuthDto(Integer memberSeq, String authUri) {
        this.memberSeq = memberSeq;
        this.authUri = authUri;
    }

    public Integer getMemberSeq() {
        return memberSeq;
    }

    public void setMemberSeq(Integer memberSeq) {
        this.memberSeq = memberSeq;
    }

    public String getAuthUri() {
        return authUri;
    }

    public void setAuthUri(String authUri) {
        this.authUri = authUri;
    }
}
