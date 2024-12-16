package com.ksuju.www.controller;

import com.ksuju.www.dto.*;
import com.ksuju.www.message.MessageEnum;
import com.ksuju.www.service.BoardService;
import com.ksuju.www.service.CommentService;
import com.ksuju.www.service.ZipService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *packageName    : com.ksuju.www.controller
 * fileName       : BoardController
 * author         : sungjun
 * date           : 2024-12-12
 * description    : 자동 주석 생성
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-12-12        kyd54       최초 생성
 */

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final ZipService zipService;

    private final BoardService boardService;

    private final CommentService commentService;

    // 게시글 좋아요 or 싫어요
    @RequestMapping("/forum/notice/thumbUpDown.do")
    @ResponseBody
    public int thumbUpDown(@RequestParam("boardSeq") int boardSeq, @RequestParam("boardTypeSeq") int boardTypeSeq,
                           @RequestParam("isLike") String isLike,
                           HttpServletRequest request) {
        System.out.println("======================= BoardController > thumbUpDown =======================");

        HttpSession session = request.getSession();
        String logInUser = (String)session.getAttribute("logInUser");

        int memberSeq = -1;

        try {
            memberSeq = boardService.getMemberSeq(logInUser);
        } catch (NullPointerException nep) {
            System.out.println("사용자 없음");
            // exception 던지거나 로그인 페이지로
        }
        BoardLikeDto boardLikeDto = new BoardLikeDto();

        boardLikeDto.setBoardTypeSeq(boardTypeSeq);
        boardLikeDto.setBoardSeq(boardSeq);
        boardLikeDto.setMemberSeq(memberSeq);
        boardLikeDto.setIsLike(isLike);

        // 좋아요 & 싫어요 정보 없는 게시물은 새로 db에 추가해주고
        // db에 데이터가 있으면(DuplicateKeyException) update 해준다
        try {
            return boardService.thumbUpDown(boardLikeDto);
        } catch (DuplicateKeyException de) {
            return boardService.thumbUpDownCvt(boardLikeDto);
        }

    }

    // 수정페이지 파일삭제
    @RequestMapping("/forum/notice/deleteFile.do")
    public String deleteFile(@RequestParam HashMap<String, Object> params) {

        boardService.deleteFile(params);

        Integer boardSeq = Integer.parseInt((String) params.get("boardSeq"));
        Integer boardTypeSeq = Integer.parseInt((String) params.get("boardTypeSeq"));
        String memberId = (String) params.get("memberId");

        return "redirect:/forum/notice/updatePage.do?boardSeq=" + boardSeq + "&boardTypeSeq=" + boardTypeSeq
                + "&memberId=" + memberId;
    }

    // 모든 첨부파일 다운로드
    @RequestMapping("/forum/notice/downloadAll.do")
    public void downloadAll(Model model, @RequestParam Integer boardSeq, @RequestParam Integer boardTypeSeq,
                            HttpServletResponse response) {

        System.out.println("============ BoardController > downloadAll.do 진입 ============");

        HashMap<String, Object> boardInfo = boardService.getReadBoard(boardSeq, boardTypeSeq);

        // 파일이름 저장할 List 생성
        List<String> fileNames = (List<String>) boardInfo.get("fileNames");
        // 파일이 현재 저장된 경로를 저장한 List 생성
        List<String> filePaths = (List<String>) boardInfo.get("filePaths");

        try {
            zipService.createZip(fileNames, filePaths);
            zipService.downloadZip(response);
            zipService.deleteZip();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 첨부파일 개별 다운로드
    @RequestMapping("/forum/notice/download.do")
    public String download(@RequestParam int attachSeq, Model model) {
        BoardAttachDto dto = boardService.getDownloadFileInfo(attachSeq);
        File file = new File(dto.getSavePath());

        Map<String, Object> fileInfo = new HashMap<>();
        fileInfo.put("downloadFile", file);
        fileInfo.put("orgFileNm", dto.getOrgFileNm());
        model.addAttribute("fileInfo", fileInfo);

        return "fileDownloadView";
    }

    // 게시글 수정
    @RequestMapping("/forum/notice/updateBoard.do")
    public String updateBoard(@RequestParam HashMap<String, String> params, ServletRequest request,
                              @RequestParam(value = "attFile", required = false) MultipartFile[] attFiles,
                              RedirectAttributes redirectAttributes) throws FileUploadException {

        System.out.println("======= BoardController > updateBoard =======");

        int boardTypeSeq = Integer.parseInt(params.get("boardTypeSeq"));
        int boardSeq = Integer.parseInt(params.get("boardSeq"));
        String memberId = params.get("memberId");

        String updateResult = String.valueOf(boardService.updateBoard(params, request, attFiles));

        if(updateResult.equals(MessageEnum.FAILD_BOARD_SIZE.getCode())) {
            redirectAttributes.addFlashAttribute("errorMsg", MessageEnum.FAILD_BOARD_SIZE.getDescription());
            redirectAttributes.addAttribute("boardTypeSeq", boardTypeSeq);
            redirectAttributes.addAttribute("boardSeq", boardSeq);
            redirectAttributes.addAttribute("memberId", memberId);
            // updatePage로 리다이렉트
            return "redirect:/forum/notice/updatePage.do";
        }

        // 수정 성공했을 때
        return "redirect:/forum/notice/listPage.do?bdTypeSeq=" + boardTypeSeq;
    }

    // 게시글 수정 thymeleaf 이동
    @RequestMapping("/forum/notice/updatePage.do")
    public ModelAndView updatePage(@RequestParam HashMap<String, String> params) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("key", Calendar.getInstance().getTimeInMillis());
        mv.setViewName("board/update");

        System.out.println("======= BoardController > updatePage =======");

        int boardSeq = Integer.parseInt((String)params.get("boardSeq"));
        int boardTypeSeq = Integer.parseInt((String)params.get("boardTypeSeq"));
        String memberId = params.get("memberId");

        HashMap<String, Object> getBoard = boardService.getReadBoard(boardSeq, boardTypeSeq);

        List<BoardAttachDto> fileList = (List<BoardAttachDto>) getBoard.get("fileList");

        String title = (String) getBoard.get("title");
        String content = (String) getBoard.get("content");

        mv.addObject("title", title);
        mv.addObject("content", content);
        mv.addObject("boardTypeSeq", boardTypeSeq);
        mv.addObject("boardSeq", boardSeq);
        mv.addObject("memberId", memberId);
        mv.addObject("fileList", fileList);

        return mv;
    }

    // 게시글 작성
    @RequestMapping("/forum/notice/createBoard.do")
    public String createBoard(@RequestParam HashMap<String, Object> params, ServletRequest request,
                              @RequestParam(value = "attFile", required = false) MultipartFile[] attFiles,
                              RedirectAttributes redirectAttributes) throws FileUploadException {

        System.out.println("======= BoardController > createBoard =======");

        String boardTypeSeq = (String) params.get("boardTypeSeq");

        String createResult = boardService.boardCreate(params, request, attFiles);

        if (createResult.equals(MessageEnum.FAILD_BOARD.getCode())) {
            redirectAttributes.addFlashAttribute("errorMsg", MessageEnum.FAILD_BOARD.getDescription());
        } else if (createResult.equals(MessageEnum.FAILD_BOARD_LOGIN.getCode())) {
            redirectAttributes.addFlashAttribute("errorMsg", MessageEnum.FAILD_BOARD_LOGIN.getDescription());
        } else if (createResult.equals(MessageEnum.FAILD_BOARD_SIZE.getCode())) {
            Integer memberSeq = boardService.getMemberSeq(String.valueOf(params.get("memberId")));

            // 에러 처리 로직을 Service 메서드로 위임
            // 문자열 구분자로 title과 content 전달받음, map을 활용하는게 더 나은듯? 경험삼아 해봄
            String[] titleAndContent = boardService.fileUploadSizeError(memberSeq, Integer.parseInt(boardTypeSeq), String.valueOf(params.get("memberId"))).split("\\|");
            String title = titleAndContent[0];
            String content = titleAndContent[1];

            redirectAttributes.addFlashAttribute("errorMsg", MessageEnum.FAILD_BOARD_SIZE.getDescription());
            redirectAttributes.addAttribute("title", title);
            redirectAttributes.addAttribute("content", content);

            return "redirect:/forum/notice/writePage.do?boardTypeSeq=" + boardTypeSeq;
        }
        return "redirect:/forum/notice/listPage.do?bdTypeSeq=" + boardTypeSeq;
    }

    // 게시글 삭제 > 댓글이 있을 경우 댓글도 다 같이 삭제해줘야 함 commentService에서 처리할 것
    @RequestMapping("/forum/notice/deleteBoard.do")
    public String deleteBoard(@RequestParam String memberId, @RequestParam int boardTypeSeq,
                              @RequestParam int boardSeq) {

        boardService.boardDelete(memberId, boardTypeSeq, boardSeq);

        return "redirect:/forum/notice/listPage.do?bdTypeSeq=" + boardTypeSeq;
    }

    // 게시글 list 불러오기
    @RequestMapping("/forum/notice/listPage.do")
    public ModelAndView listPage(@RequestParam HashMap<String, String> params, HttpServletRequest request) {
        System.out.println("======= BoardController > listPage =======");

        ModelAndView mv = new ModelAndView();
        mv.addObject("key", Calendar.getInstance().getTimeInMillis());

        setDefaultParams(params);
        mv.addObject("bdTypeSeq", params.get("bdTypeSeq"));

        int page = Integer.parseInt(params.get("page"));
        int size = Integer.parseInt(params.get("size"));
        int start = (page - 1) * size;
        int totalCnt = boardService.totalCnt(Integer.parseInt(params.get("bdTypeSeq")));

        setPagingAttributes(mv, page, totalCnt, size);

        String loginMember = getLoginMember(request);
        Integer loginMemberSeq = getMemberSeq(loginMember);

        // 아이디 인증여부 확인
        String authYN = boardService.getAuthYN(loginMember);

        if("N".equals(authYN)) {
            params.put("logInUser", loginMember);
            boardService.updateAuthNum(params);
            String getEmail = boardService.getEmail(loginMember);
            mv.setViewName("auth/authNum");
            mv.addObject("alert", "이메일 인증이 필요합니다 가입 시 입력하신 이메일을 확인해주세요.");
            mv.addObject("idAuth", "idAuth");
            mv.addObject("email", getEmail);
            return mv;
        } else if ("guest".equals(authYN)) {
            mv.setViewName("auth/login");
            mv.addObject("alert", "로그인이 필요합니다.");
            return mv;
        }

        List<BoardDto> list = getBoardList(params, start, size, loginMemberSeq);
        mv.addObject("list", list);
        mv.addObject("loginMember", loginMember);

        // 좋아요 top 5 게시글 가져오기
        List<Map<String, Integer>> topFive = boardService.getLikeTopFive(Integer.parseInt(params.get("bdTypeSeq")));

        mv.setViewName("board/list");
        mv.addObject("topFive", topFive);

        return mv;
    }

    // 기본 페이지와 사이즈 설정
    private void setDefaultParams(HashMap<String, String> params) {
        if (!params.containsKey("page")) {
            params.put("page", "1");
        }
        if (!params.containsKey("size")) {
            params.put("size", "10");
        }
    }

    // 페이징 관련
    private void setPagingAttributes(ModelAndView mv, int page, int totalCnt, int size) {
        PageHandler pageHandler = new PageHandler(page, totalCnt, size);

        mv.addObject("startPage", pageHandler.getStartPage());
        mv.addObject("endPage", pageHandler.getEndPage());
        mv.addObject("currentPage", page);
        mv.addObject("hasPrev", pageHandler.isShowPrev());
        mv.addObject("hasNext", pageHandler.isShowNext());
        mv.addObject("totalCnt", totalCnt);
    }


    // 세션에서 로그인한 회원 정보 가져옴
    private String getLoginMember(HttpServletRequest request) {
        HttpSession session = request.getSession();

        // 비로그인 상태일때 리턴할 문자열
        String result = "guest";

        // 세션에 logInUser가 있는지 null 체크
        String logInUser = (String) session.getAttribute("logInUser");

        // 세션에 logInUser이 없으면 guest, 있으면 아이디
        if(logInUser == null || logInUser.isEmpty()) {
            return result;
        } else {
            return logInUser;
        }
    }


    // 회원 번호를 가져오고 null일 경우 기본값으로 설정
    private Integer getMemberSeq(String loginMember) {
        Integer loginMemberSeq = boardService.getMemberSeq(loginMember);
        if (loginMemberSeq == null) {
            loginMemberSeq = -999999;
        }
        return loginMemberSeq;
    }


    // 게시글 목록 가져오기
    private List<BoardDto> getBoardList(HashMap<String, String> params, int start, int size, Integer loginMemberSeq) {
        HashMap<String, Integer> boardList = new HashMap<>();
        boardList.put("bdTypeSeq", Integer.parseInt(params.get("bdTypeSeq")));
        boardList.put("start", start);
        boardList.put("size", size);
        boardList.put("loginMemberSeq", loginMemberSeq);

        return boardService.getList(boardList);
    }


    @RequestMapping("/forum/notice/writePage.do")
    public ModelAndView writePage(@RequestParam HashMap<String, String> params,
                                  ServletRequest request,
                                  Model model) {

        System.out.println("========================= writePage.do =========================");

        ModelAndView mv = new ModelAndView();
        mv.addObject("key", Calendar.getInstance().getTimeInMillis());

        int boardTypeSeq = Integer.parseInt(params.get("boardTypeSeq"));

        // 현재 로그인한 사용자의 id 가져오기
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession();
        String logInUser = (String) session.getAttribute("logInUser");

        mv.setViewName("board/write");
        mv.addObject("boardTypeSeq", boardTypeSeq);
        mv.addObject("memberId", logInUser);

        mv.addObject("title", params.get("title"));
        mv.addObject("content", params.get("content"));
        return mv;

    }

    @RequestMapping("/forum/notice/readPage.do")
    public ModelAndView readPage(@RequestParam HashMap<String, String> params, ServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("key", Calendar.getInstance().getTimeInMillis());
        mv.setViewName("board/read");

        System.out.println("========= BoardController > readPage =========");

        int boardSeq = Integer.parseInt(params.get("boardSeq"));
        int boardTypeSeq = Integer.parseInt(params.get("boardTypeSeq"));

        // 클릭한 게시글 가져오기
        HashMap<String, Object> selectBoard = boardService.getReadBoard(boardSeq, boardTypeSeq);

        List<BoardAttachDto> fileList = (List<BoardAttachDto>) selectBoard.get("fileList");

        // 현재 로그인한 사용자의 id 가져오기
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession();

        String logInUser = (String) session.getAttribute("logInUser");

        int loginMemberSeq = boardService.getMemberSeq(logInUser);

        String isLike = boardService.selectIsLike(loginMemberSeq,boardSeq,boardTypeSeq);

        mv.addObject("title", selectBoard.get("title"));
        mv.addObject("content", selectBoard.get("content"));
        mv.addObject("memberId", selectBoard.get("memberId"));
        mv.addObject("regDtm", selectBoard.get("reg_dtm"));
        mv.addObject("comments", selectBoard.get("comments"));
        mv.addObject("fileList", fileList);
        mv.addObject("boardSeq", boardSeq);
        mv.addObject("boardTypeSeq", boardTypeSeq);
        mv.addObject("logInUser", logInUser);
        mv.addObject("isLike", isLike);
        mv.addObject("cmtIsLike", commentService.commentIsLike(loginMemberSeq,boardSeq,boardTypeSeq));

        return mv;
    }

    @RequestMapping("/contact.do")
    public ModelAndView contactView() {
        ModelAndView mv = new ModelAndView();
        mv.addObject("key", Calendar.getInstance().getTimeInMillis());
        mv.setViewName("auth/contact");

        return mv;
    }

    // 댓글 좋아요 or 싫어요
    @RequestMapping("/forum/notice/commentIsLike.do")
    @ResponseBody
    public int commentIsLike(@RequestParam("boardSeq") int boardSeq,
                             @RequestParam("boardTypeSeq") int boardTypeSeq,
                             @RequestParam("cmtIsLike") String cmtIsLike,
                             @RequestParam("commentSeq") int commentSeq,
                             HttpServletRequest request) {
        System.out.println("======================= BoardController > commentIsLike =======================");

        HttpSession session = request.getSession();
        String logInUser = (String)session.getAttribute("logInUser");

        int memberSeq = -1;

        try {
            memberSeq = boardService.getMemberSeq(logInUser);
        } catch (NullPointerException nep) {
            System.out.println("사용자 없음");
            // exception 던지거나 로그인 페이지로
        }
        CommentLikeDto commentLikeDto = new CommentLikeDto();

        commentLikeDto.setBoardTypeSeq(boardTypeSeq);
        commentLikeDto.setBoardSeq(boardSeq);
        commentLikeDto.setMemberSeq(memberSeq);
        commentLikeDto.setIsLike(cmtIsLike);
        commentLikeDto.setCommentSeq(commentSeq);

        // 좋아요 & 싫어요 정보 없는 댓글은 새로 db에 추가해주고
        // db에 데이터가 있으면(DuplicateKeyException) update 해준다
        try {
            return commentService.commentUpDown(commentLikeDto);
        } catch (DuplicateKeyException de) {
            return commentService.commentUpDownCvt(commentLikeDto);
        }
    }


    // 댓글삭제
    @RequestMapping("/forum/notice/deleteComment.do")
    public String deleteComment(@RequestParam HashMap<String, Object> params) {
        System.out.println("======================= BoardController > deleteComment =======================");

        commentService.deleteComment(params);

        int boardSeq = Integer.parseInt((String) params.get("boardSeq"));
        int boardTypeSeq = Integer.parseInt((String) params.get("boardTypeSeq"));
        return "redirect:/forum/notice/readPage.do?boardTypeSeq=" + boardTypeSeq + "&boardSeq=" + boardSeq;
    }

    // 댓글수정
    @RequestMapping("/forum/notice/updateComment.do")
    public String updateComment(@RequestParam HashMap<String, Object> params) {
        System.out.println("======================= BoardController > updateComment =======================");

        commentService.updateComments(params);

        int boardSeq = Integer.parseInt((String) params.get("boardSeq"));
        int boardTypeSeq = Integer.parseInt((String) params.get("boardTypeSeq"));

        return "redirect:/forum/notice/readPage.do?boardTypeSeq=" + boardTypeSeq + "&boardSeq=" + boardSeq;
    }

    // 댓글작성
    @RequestMapping("/forum/notice/reply.do")
    @ResponseBody
    public int addComment(@RequestBody BoardCommentDto dto, HttpServletRequest request) {
        System.out.println("======================= BoardController > reply.do =======================");

        if (commentService.addComment(dto, request) == 1) {
            return 1;
        } else {
            return -1;
        }
    }
}
