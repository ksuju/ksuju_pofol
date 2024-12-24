package com.ksuju.www.service;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.ksuju.www.repository.EtcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import lombok.RequiredArgsConstructor;

@Service("indexService")
@RequiredArgsConstructor
public class IndexService {
    private final static Logger logger = LoggerFactory.getLogger(IndexService.class);

	private final EtcRepository etcRepository;

	// 내 블로그 RSS 가져오기 및 파싱, 서버에서 RSS가져온 뒤 파싱해서 View에 뿌려줌
	public List<Map<String,String>> blogRssAndParsing(String urlString) throws Exception {

        logger.info("blogRssAndParsing");
		
        List<Map<String, String>> items = new ArrayList<>();
        
        try {
            URL url = new URL(urlString);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(url.openStream());

            NodeList itemNodes = doc.getElementsByTagName("item");
            
            // 데이터 파싱
            for (int i = 0; i < itemNodes.getLength(); i++) {
                Element item = (Element) itemNodes.item(i);
                Map<String, String> itemMap = new HashMap<>();
                
                //pubDate 날짜형식 보기 편하게 변환
                SimpleDateFormat oldFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                SimpleDateFormat newFormat = new SimpleDateFormat("yy.MM.dd. HH:mm");
                Date parsedDate = oldFormat.parse(getElementContent(item, "pubDate"));
                String date = newFormat.format(parsedDate);
                
                //description 텍스트만 추출
                String rawDescription = getElementContent(item, "description");
                String description = rawDescription.replaceAll("<[^>]*>", "");
                itemMap.put("description", description);
                //itemMap.put("description", getElementContent(item, "description"));

                itemMap.put("title", getElementContent(item, "title"));
                itemMap.put("link", getElementContent(item, "link"));
                itemMap.put("category", getElementContent(item, "category"));
                itemMap.put("pubDate", date);
                items.add(itemMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return items;
	}
	
    private static String getElementContent(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }

	// 특정 카테고리 식사메뉴 가져오기
	public String getMenu(String category) {
        logger.info("getMenu");

		List<String> menu = etcRepository.menu(category);
		double random = Math.random();
		int num = (int)Math.round(random * (menu.size()-1));

		String menuName = menu.get(num);

		return menuName;
	}

	// 모든 식사메뉴 카테고리 가져오기
	public List<String> menuCategory() {
        logger.info("menuCategory");
        return etcRepository.menuCategory();
	}

	// 모든 식사메뉴 가져오기
	public String allMenu() {
        logger.info("allMenu");

		List<String> allMenu = etcRepository.allMenu();

		Collections.shuffle(allMenu);

		double random = Math.random();
		int num = (int)Math.round(random * (allMenu.size()-1));

		String menuName = allMenu.get(num);

		return menuName;
	}
}
