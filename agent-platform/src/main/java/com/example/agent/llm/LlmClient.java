package com.example.agent.llm;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LlmClient {

   public String chat(String prompt){
       System.out.println("=== LLM 生成内容 ===");
       System.out.println(prompt);
       return """
        {
            "queryContent": "订单管理系统，包含创建、查询、列表功能",
            "modules": "订单模块",
            "tables": "order(id,order_no,user_id,amount,status)",
            "apis": "/order/create, /order/list, /order/get",
            "pages": "订单列表页、订单详情页、订单创建页"
        }
        """;
    }
}
