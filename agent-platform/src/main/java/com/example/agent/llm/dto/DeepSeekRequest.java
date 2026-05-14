package com.example.agent.llm.dto;

import lombok.Data;
import java.util.List;

@Data
public class DeepSeekRequest {
    private String model;
    private List<Message> messages;
    private double temperature = 0.1;

    @Data
    public static class Message {
        private String role;
        private String content;

        public static Message user(String content) {
            Message m = new Message();
            m.setRole("user");
            m.setContent(content);
            return m;
        }

        public static Message system(String content) {
            Message m = new Message();
            m.setRole("system");
            m.setContent(content);
            return m;
        }
    }
}