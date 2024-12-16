package com.ksuju.www.dto;

public class PageHandler {
	final static int NAV_SIZE = 10;
	int page; // 현재 페이지
	int totalCnt; // 전체 게시물 개수
	int pageSize; // 페이지 사이즈
	int startPage; // 시작 페이지
	int endPage; // 마지막 페이지
	int totalPage; // 전체 페이지 수
	boolean showPrev; // 이전 페이지
	boolean showNext; // 다음 페이지
	
	
	public PageHandler(int page, int totalCnt, int pageSize) {
		this.page = page;
		this.totalCnt = totalCnt;
		this.pageSize = pageSize;
		
        totalPage = (int)Math.ceil(totalCnt / (double)pageSize);
        startPage = (page - 1) / NAV_SIZE * NAV_SIZE + 1;
        endPage = Math.min(startPage + NAV_SIZE - 1, totalPage);
        showPrev = startPage != 1;
        showNext = endPage != totalPage;     
	}
	
	
	// 이전, 게시판 번호 (i), 다음
    public String toString() {
        String str = "";
        if (showPrev) {
            str += "< 이전";
        }
        for (int i = startPage; i <= endPage; i++) {
            System.out.println(" " + i + " ");
        }
        if (showNext) {
            str += "다음 >";
        }
        return str;
    }


	public int getPage() {
		return page;
	}


	public void setPage(int page) {
		this.page = page;
	}


	public int getTotalCnt() {
		return totalCnt;
	}


	public void setTotalCnt(int totalCnt) {
		this.totalCnt = totalCnt;
	}


	public int getPageSize() {
		return pageSize;
	}


	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}


	public int getStartPage() {
		return startPage;
	}


	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}


	public int getEndPage() {
		return endPage;
	}


	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}


	public int getTotalPage() {
		return totalPage;
	}


	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}


	public boolean isShowPrev() {
		return showPrev;
	}


	public void setShowPrev(boolean showPrev) {
		this.showPrev = showPrev;
	}


	public boolean isShowNext() {
		return showNext;
	}


	public void setShowNext(boolean showNext) {
		this.showNext = showNext;
	}


	public static int getNavSize() {
		return NAV_SIZE;
	}
}
