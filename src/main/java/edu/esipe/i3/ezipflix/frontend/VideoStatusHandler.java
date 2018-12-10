package edu.esipe.i3.ezipflix.frontend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Created by Gilles GIRAUD gil on 1/22/18.
 */
public class VideoStatusHandler extends TextWebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoStatusHandler.class);

    public VideoStatusHandler() {
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        LOGGER.info("Session opened = {}", session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        LOGGER.info("Status = {}", message.getPayload());
    }
}
