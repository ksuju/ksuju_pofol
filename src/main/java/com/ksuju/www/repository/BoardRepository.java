package com.ksuju.www.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksuju.www.dto.BoardAttachDto;
import com.ksuju.www.dto.BoardDto;
import com.ksuju.www.dto.BoardLikeDto;
import org.apache.ibatis.annotations.Param;


public interface BoardRepository {

	// 첨부파일 크기 제한에러 발생시 작성중이던 정보 가져오기 (notice)
	public HashMap<String, Object> getSave(@Param("memberSeq") int memberSeq, @Param("boardTypeSeq") int boardTypeSeq); 
	
	// 인기글 상위 5개 출력 (notice)
	public List<Map<String, Integer>> getLikeTopFive(@Param("boardTypeSeq") int boardTypeSeq);
	
    // 게시글의 파일 개수, 댓글 개수, 좋아요 여부를 포함한 통합 쿼리 (notice)
    public List<Map<String, Object>> getBoardDetails(@Param("boardSeqs") List<Integer> boardSeqs,
                                              @Param("boardTypeSeq") int boardTypeSeq);
	
	// 좋아요 수 (notice)
	public Integer like(@Param("boardSeq") Integer boardSeq,
			@Param("bdTypeSeq") Integer bdTypeSeq);
	
	// 여러 개의 boardSeq에 대한 is_like 값 가져오기 (notice)
	public List<Map<String, Object>> selectIsLikeList(@Param("memberSeq") Integer memberSeq,
			@Param("boardSeqs") List<Integer> boardSeqs, @Param("boardTypeSeq") int boardTypeSeq);
	
	// 첨부파일 개수 가져오기	 (notice)
    List<Map<String, Object>> bringFileCnt(@Param("boardSeqs") List<Integer> boardSeqs, @Param("boardTypeSeq") int boardTypeSeq);
    
	// 게시글 좋아요 Y or N 셀렉트 (notice)
	public String selectIsLike(@Param("memberSeq")int memberSeq,
			@Param("boardSeq")int boardSeq,
			@Param("boardTypeSeq")int boardTypeSeq);
	
	// 게시글 좋아요 Y or N (notice)
	public int thumbUpDownCvt(BoardLikeDto boardLikeDto);
	
	// 게시글 좋아요 (notice)
	public int thumbUpDown(BoardLikeDto boardLikeDto);
	
	// 수정페이지 파일 개별 삭제 (notice)
	public boolean deleteFile(@Param("attachSeq")int attachSeq, @Param("boardSeq")int boardSeq, @Param("boardTypeSeq")int boardTypeSeq);
	
	// attach_seq로 첨부파일 정보 가져오기 (notice)
	public BoardAttachDto getAttachInfo(int attachSeq);
	
	// 저장되어 있는 모든 파일 가져오기 (notice)
	public List<BoardAttachDto> selectAllFile(@Param("boardTypeSeq") int boardTypeSeq,
			@Param("boardSeq") int boardSeq);
	
	// boardAttach delete (notice)
	public int deleteBoardAttach(@Param("boardTypeSeq") int boardTypeSeq,
			@Param("boardSeq") int boardSeq);
	
	// 파일첨부 > boardAttach insert (notice)
	public int insertBoardAttach(BoardAttachDto boardAttachDto);
	
	// 게시글 수정하기 (notice)
	public int updateBoard(HashMap<String, String> params);
	
	// 특정 게시글 가져오기 (notice)
	public HashMap<String, Object> selectBoard(@Param("boardSeq") int boardSeq,
			@Param("boardTypeSeq") int boardTypeSeq);
	
	// 게시글 작성 (notice)
	public int boardCreate(HashMap<String, Object> params);
	
	// 게시글 삭제 (notice)
	public int boardDelete(@Param("memberId") String memberId,
			@Param("boardTypeSeq") int boardTypeSeq,
			@Param("boardSeq") int boardSeq);
	
	// 게시판 내에 있는 게시글 모두 가져오기 (notice)
	public List<BoardDto> getList(HashMap<String, Integer> params);
	public List<Integer> getSeq(HashMap<String, Integer> params);
	
	// 게시판 내에 있는 전체 게시글의 수 (notice)
	public int totalCnt(int boardTypeSeq);

}
