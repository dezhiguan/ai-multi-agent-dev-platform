package com.example.agent.state;
import lombok.Data;
@Data
public class RuntimeResult {
    private String backendUrl;
    private String frontendUrl;
    private boolean success;
}