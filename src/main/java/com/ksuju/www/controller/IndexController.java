package com.ksuju.www.controller;

/**
 * packageName    : com.ksuju.www.controller
 * fileName       : IndexController
 * author         : sungjun
 * date           : 2024-11-19
 * description    : 자동 주석 생성
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-11-19        kyd54       최초 생성
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksuju.www.service.IndexService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final IndexService indexService;

    // 인덱스페이지
    @RequestMapping("/pf/index.do")
    public String index(@RequestParam HashMap<String, String> params
    , HttpSession session
    , Model model) throws Exception {
        System.out.println("================ IndexContoller 진입 ================");
        String rssUrl = "https://ksuju.tistory.com/rss";

        List<Map<String, String>> rssItems = indexService.blogRssAndParsing(rssUrl);

        model.addAttribute("rssItems", rssItems);

        return "basic/index";
    }
    
    //웹소켓테스트
    @GetMapping("/basic/webSocketTest")
    public String WebSocketTest() {
        return "test/webSocketTest";
    }
}

