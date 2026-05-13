package com.example.agent.state;
import lombok.Data;
@Data
public class CodeGenerationResult {
    private boolean success;
    private String codePath;
}