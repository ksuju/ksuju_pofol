package com.ksuju.www.filter;

/**
 * packageName    : com.ksuju.www.filter
 * fileName       : LoginFilter
 * author         : sungjun
 * date           : 2024-11-23
 * description    : 자동 주석 생성
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-11-23        kyd54       최초 생성
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.util.ObjectUtils;

public class LoginFilter implements Filter {

    // 로그인 필터가 적용될 URI 목록
    private final String[] LOGIN_REQUIRED_URI = {
            "/pf/forum/notice/listPage.do",
            "/pf/forum/notice/writePage.do",
            "/pf/forum/notice/readPage.do",
            "/pf/forum/notice/updatePage.do"
    };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 필터 초기화 시 호출
        System.out.println("LoginFilter > init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String uri = req.getRequestURI();
        List<String> UriList = new ArrayList<>(Arrays.asList(LOGIN_REQUIRED_URI));
        HttpSession session = req.getSession();

        // 로그인 여부 확인
        if (ObjectUtils.isEmpty(session.getAttribute("logInUser"))) {
            if (UriList.contains(uri)) {
                response.setContentType("text/html; charset=UTF-8");
                PrintWriter out = response.getWriter();

                session.setAttribute("filterUri", uri); // 로그인 후 원래 요청 페이지로 돌아가기 위한 URI 저장

                out.println("<script>alert('로그인 후 이용해주세요.'); location.href='" + req.getContextPath() + "/auth/loginPage.do';</script>");

                return; // 로그인하지 않은 경우 요청을 차단하고 로그인 페이지로 리다이렉트
            }
        } else {
            // 로그인 상태인 경우, attribute를 설정하여 메뉴에 표시될 버튼 설정 > LoginService > loginCheck
            //session.setAttribute("loggedIn", true);
        }

        // 필터 체인으로 요청을 전달
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // 필터가 종료될 때 호출
        System.out.println("LoginFilter > destroy");
    }
}
