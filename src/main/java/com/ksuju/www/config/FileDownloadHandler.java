package com.ksuju.www.config;

/**
 * packageName    : com.ksuju.www.config
 * fileName       : FileDownloadHandler
 * author         : sungjun
 * date           : 2024-12-20
 * description    : 자동 주석 생성
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-12-20        kyd54       최초 생성
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Map;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.view.AbstractView;

@Component
public class FileDownloadHandler extends AbstractView {

    public FileDownloadHandler() {
        setContentType("application/download; charset=UTF-8");
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Map<String, Object> fileInfo =
                (Map<String, Object>) model.get("fileInfo");
        File file = (File) fileInfo.get("downloadFile");
        String orgFileNm = (String) fileInfo.get("orgFileNm");
        // 헤더 세팅
        response.setContentType(getContentType());
        response.setContentLength((int) file.length());

        String userAgent = request.getHeader("User-Agent");

        boolean ie = userAgent.indexOf("MSIE") > -1;
        String fileNm = null;
        if(ie) {
            fileNm = URLEncoder.encode(orgFileNm, "UTF-8");
        }
        else {
            fileNm = new String(orgFileNm.getBytes("UTF-8"), "ISO-8859-1");
        }

        response.setHeader("Content-Disposition", "attachment; filename=\""+ fileNm + "\"");
        response.setHeader("Content-Transfer-Encoding", "binary");

        // 파일 읽어와 Response에 보내기
        OutputStream out = response.getOutputStream();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            FileCopyUtils.copy(fis, out);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ioe) { }
            }
            out.flush();
        }
    }
}