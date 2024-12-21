package com.ksuju.www.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUtil {

	// 업로드 용량 제한
	private static final long MAX_FILE_SIZE_BYTES = 1024 * 1024; // 1MB
	
	// 날짜출력형식 지정
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	// 지정한 형식을 바탕으로 날짜 출력
    Date now = new Date();
    String nowTime = sdf.format(now);
	
    //@Value("#conis('file.save.path'")
	//private String SAVE_PATH = "/home/ec2-user/files/"+nowTime;	> 기존 파일 경로 코드
	//private String SAVE_PATH = System.getProperty("user.home") + "/files/" + nowTime;
	private final String SAVE_PATH;

	// 생성자에서 OS에 맞는 경로를 설정
	public FileUtil() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			// 로컬 환경 (Windows)
			SAVE_PATH = "C:/home/ec2-user/files/" + nowTime;
		} else {
			// 서버 환경 (Linux)
			SAVE_PATH = "/home/ec2-user/files/" + nowTime;
		}
	}

	public File saveFile(MultipartFile mf) throws FileUploadException {
		System.out.println("==================== FileUtil>saveFile 진입 ====================");
		File destFile = new File(SAVE_PATH);
		
	    // 파일이 저장될 디렉토리가 없으면 생성
		if(!destFile.exists()) {
			destFile.mkdirs();
		}
		
		try {
			// 업로드하는 파일 크기
			long getByte = mf.getSize();
			
			if(getByte > MAX_FILE_SIZE_BYTES) {
				System.out.println("파일의 크기는 1MB를 넘을 수 없습니다.");
		        throw new MaxUploadSizeExceededException(MAX_FILE_SIZE_BYTES);
			}
			
			// 업로드된 파일이 없거나 파일 업로드 칸이 비어 있는 경우 빈 파일이 저장되지 않도록 방지
		    if(mf == null || mf.isEmpty()) {
		        System.out.println("업로드된 파일이 없거나 비어 있습니다.");
		        throw new NullPointerException();
		    }
		    
			destFile = new File(SAVE_PATH, UUID.randomUUID().toString().replaceAll("-", ""));

			mf.transferTo(destFile);
			
			return destFile;
			
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
            throw new FileUploadException("파일 업로드 중 오류가 발생했습니다.", e);
        }
	}
}