package com.ksuju.www.service;

import java.util.HashMap;
import java.util.List;

import com.ksuju.www.dto.BoardCommentDto;
import com.ksuju.www.dto.CommentLikeDto;
import com.ksuju.www.repository.CommentRepository;
import com.ksuju.www.repository.JoinRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final static Logger logger = LoggerFactory.getLogger(CommentService.class);
	
	private final CommentRepository commentRepository;
	
	private final JoinRepository joinRepository;
	
	
	// 댓글 좋아요 Y or N 셀렉트
	public List<HashMap<String, Object>> commentIsLike(int memberSeq, int boardSeq, int boardTypeSeq) {
		logger.info("commentIsLike");
		return commentRepository.commentIsLike(memberSeq, boardSeq, boardTypeSeq);
	}
	
	// 댓글 좋아요 Y or N
	public int commentUpDownCvt(CommentLikeDto commentLikeDto) {
		logger.info("commentUpDownCvt");
		return commentRepository.commentUpDownCvt(commentLikeDto);
	}
	
	// 댓글 좋아요
	public int commentUpDown(CommentLikeDto commentLikeDto) {
		logger.info("commentUpDown");
		return commentRepository.commentUpDown(commentLikeDto);
	}
	
	// 댓글 삭제하기
	public int deleteComment(HashMap<String,Object> params) {
		logger.info("deleteComment");
		return commentRepository.deleteComment(params);
	}
	
	// 댓글 수정하기
	public int updateComments(HashMap<String,Object> params) {
		logger.info("updateComments");
		return commentRepository.updateComments(params);
	}
	
	// 댓글 작성하기
	public int addComment(BoardCommentDto dto,
						  HttpServletRequest request) {
		logger.info("addComment");
		HttpSession session = request.getSession();
		
		String memberId = (String) session.getAttribute("logInUser");
		
		if(joinRepository.getMemberSeq(memberId) > 0) {
			dto.setMemberSeq(joinRepository.getMemberSeq(memberId));
		} else {
			return -1;
		}
		
	    // parentCommentSeq가 null이거나 0이면 null로 설정
	    if (dto.getParentCommentSeq() == null || dto.getParentCommentSeq() == 0) {
	        dto.setParentCommentSeq(null);
	    }
		commentRepository.insertComment(dto);
		
		return 1;
	}
}
