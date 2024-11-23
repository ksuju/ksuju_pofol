package com.ksuju.www.dto;

public class CommentLikeDto {
    private int boardSeq;
    private int boardTypeSeq;
    private String regDtm;
    private String cmtIsLike;
    private int memberSeq;
    private int commentSeq;
    
	public int getBoardSeq() {
		return boardSeq;
	}
	public void setBoardSeq(int boardSeq) {
		this.boardSeq = boardSeq;
	}
	public int getBoardTypeSeq() {
		return boardTypeSeq;
	}
	public void setBoardTypeSeq(int boardTypeSeq) {
		this.boardTypeSeq = boardTypeSeq;
	}
	public String getRegDtm() {
		return regDtm;
	}
	public void setRegDtm(String regDtm) {
		this.regDtm = regDtm;
	}
	public String getIsLike() {
		return cmtIsLike;
	}
	public void setIsLike(String cmtIsLike) {
		this.cmtIsLike = cmtIsLike;
	}
	public int getMemberSeq() {
		return memberSeq;
	}
	public void setMemberSeq(int memberSeq) {
		this.memberSeq = memberSeq;
	}
	public int getCommentSeq() {
		return commentSeq;
	}
	public void setCommentSeq(int commentSeq) {
		this.commentSeq = commentSeq;
	}
}
