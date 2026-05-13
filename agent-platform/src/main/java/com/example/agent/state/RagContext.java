package com.example.agent.state;
import lombok.Data;
import java.util.List;
@Data
public class RagContext {
    private List<String> knowledgeDocs;
}