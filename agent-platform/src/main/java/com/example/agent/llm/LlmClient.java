package com.example.agent.llm;

import com.example.agent.llm.dto.DeepSeekRequest;
import com.example.agent.llm.dto.DeepSeekResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
public class LlmClient {

    @Value("${llm.deepseek.api-key}")
    private String apiKey;

    @Value("${llm.deepseek.api-url}")
    private String apiUrl;

    @Value("${llm.deepseek.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 真实调用 DeepSeek 大模型
     */
    public String chat(String userPrompt) {
        DeepSeekRequest request = new DeepSeekRequest();
        request.setModel(model);
        request.setMessages(List.of(
                DeepSeekRequest.Message.system("你是专业的软件工程AI助手，严格按要求输出，不闲聊、不多余解释。"),
                DeepSeekRequest.Message.user(userPrompt)
        ));

        // 设置请求头
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        org.springframework.http.HttpEntity<DeepSeekRequest> entity =
                new org.springframework.http.HttpEntity<>(request, headers);

        try {
            var resp = restTemplate.postForObject(apiUrl, entity, DeepSeekResponse.class);
            if (resp == null || resp.getChoices() == null || resp.getChoices().isEmpty()) {
                return "大模型返回为空";
            }
            return resp.getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            log.error("调用DeepSeek大模型异常", e);
            return "大模型调用失败：" + e.getMessage();
        }
    }
}