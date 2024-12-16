package com.ksuju.www.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public class ZipService {

    // 압축파일경로설정
    // 날짜출력형식 지정
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    // 지정한 형식을 바탕으로 날짜 출력
    Date now = new Date();
    String nowTime = sdf.format(now);

    private String CHECK_PATH = "/home/ec2-user/files/" + nowTime;
    private String SAVE_PATH = "/home/ec2-user/files/" + nowTime + "/downAll.zip";

    // fileNames는 원래 파일 이름 , filePaths는 파일이 저장되어있는 경로
    public void createZip(List<String> fileNames, List<String> filePaths) throws IOException {
        File destZip = new File(CHECK_PATH);
        // 경로가 없으면 생성
        if (!destZip.exists()) {
            destZip.mkdirs();
        }

        try (FileOutputStream fout = new FileOutputStream(SAVE_PATH);
             ZipOutputStream zout = new ZipOutputStream(fout)) {

            // 처리된 파일 이름을 추적하기 위한 Set (중복된 파일 압축하지 않기 위함)
            Set<String> processedFiles = new HashSet<>();

            for (int i = 0; i < filePaths.size(); i++) {
                String sourceFile = filePaths.get(i);
                String originalName = fileNames.get(i);

                // 파일 이름이 이미 처리되었는지 확인
                if (processedFiles.contains(originalName)) {
                    continue; // **중복 파일은 건너뜀**
                }

                File file = new File(sourceFile);
                if (file.exists()) {
                    try (FileInputStream fin = new FileInputStream(file)) {
                        // 압축 파일의 엔트리 이름을 원래 파일 이름으로 설정
                        ZipEntry zipEntry = new ZipEntry(originalName);
                        zout.putNextEntry(zipEntry);

                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fin.read(buffer)) > 0) {
                            zout.write(buffer, 0, length);
                        }
                        zout.closeEntry();
                    }
                    // 이 파일 이름을 처리된 것으로 표시
                    processedFiles.add(originalName);
                } else {
                    System.err.println("파일을 찾을 수 없습니다: " + sourceFile);
                }
            }
        }
    }

    public void downloadZip(HttpServletResponse response) throws IOException {

        //사용자가 전체다운로드 받았을때 다운받은 zip파일의 이름 설정
        /*
         * String downloadFileName = "downAll";
         * response.setContentType("application/zip");
         * response.addHeader("Content-Disposition", "attachment; filename=" +
         * downloadFileName + ".zip");
         */

        // zip파일 다운로드
        try (FileInputStream fis = new FileInputStream(SAVE_PATH);
             BufferedInputStream bis = new BufferedInputStream(fis);
             ServletOutputStream so = response.getOutputStream();
             BufferedOutputStream bos = new BufferedOutputStream(so)) {

            byte[] data = new byte[2048];
            int input;
            while ((input = bis.read(data)) != -1) {
                bos.write(data, 0, input);
                bos.flush();
            }
        }
    }

    public void deleteZip() {
        File file = new File(SAVE_PATH);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("downAll.zip파일이 성공적으로 삭제되었습니다.");
            } else {
                System.err.println("downAll.zip파일 삭제 실패.");
            }
        } else {
            System.err.println("downAll.zip파일이 존재하지 않습니다.");
        }
    }
}