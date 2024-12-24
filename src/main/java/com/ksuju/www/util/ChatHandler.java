package com.ksuju.www.util;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class ChatHandler extends TextWebSocketHandler {
    private final static Logger logger = LoggerFactory.getLogger(ChatHandler.class);

	private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
	
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("웹소켓 연결됨 ===>" + session.getId());
        sessions.add(session);  // 연결된 세션 추가
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String payload = message.getPayload();  // 클라이언트로부터 받은 메시지
            logger.info("payload ===> " + payload);
            for (WebSocketSession s : sessions) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage("서버로부터 받은 메시지: " + payload));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("웹소켓 연결 종료 ===> " + session.getId());
        if (session.isOpen()) {
            sessions.remove(session);  // 연결 종료된 세션 제거
        }
    }
}
