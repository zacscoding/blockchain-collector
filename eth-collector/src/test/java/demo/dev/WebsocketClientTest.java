package demo.dev;

import demo.util.SimpleLogger;
import java.lang.reflect.Type;
import java.util.Scanner;
import org.junit.Test;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

/**
 * @author zacconding
 * @Date 2018-07-19
 * @GitHub : https://github.com/zacscoding
 */
public class WebsocketClientTest {

    @Test
    public void test() {
        String url = "";
        WebSocketClient client = new StandardWebSocketClient();

        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSessionHandler sessionHandler = new MyWebsocketHandler();
        stompClient.connect(url, sessionHandler);

        new Scanner(System.in).nextLine(); // Don't close immediately
    }

    static class MyWebsocketHandler implements StompSessionHandler {

        @Override
        public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
            SimpleLogger.println("## afterConnected : {} | {}", stompSession, stompHeaders);
        }

        @Override
        public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {
            SimpleLogger.println("## handleException : {} | {} | {} | {} ", stompSession, stompHeaders, stompHeaders, throwable.getMessage());
        }

        @Override
        public void handleTransportError(StompSession stompSession, Throwable throwable) {
            SimpleLogger.println("## handleTransportError : {} | {}", stompSession, throwable.getMessage());
        }

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            SimpleLogger.println("## getPayloadType : {}", stompHeaders);
            return null;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object payload) {
            SimpleLogger.println("## getPayloadType : {} | {}", stompHeaders, payload);
        }
    }
}
