```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class YourControllerTest {

    private MockMvc mockMvc;

    @Mock
    private YourService yourService;

    @InjectMocks
    private YourController yourController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(yourController).build();
    }

    @Test
    public void testGetById_Success() throws Exception {
        // 准备测试数据
        Long id = 1L;
        YourEntity expectedEntity = new YourEntity();
        expectedEntity.setId(id);
        expectedEntity.setName("test");

        // 模拟Service层行为
        when(yourService.getById(id)).thenReturn(expectedEntity);

        // 执行请求并验证
        mockMvc.perform(get("/api/your-entity/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("test"));
    }

    @Test
    public void testGetById_NotFound() throws Exception {
        // 准备测试数据
        Long id = 999L;

        // 模拟Service层行为
        when(yourService.getById(id)).thenThrow(new ResourceNotFoundException("Entity not found"));

        // 执行请求并验证
        mockMvc.perform(get("/api/your-entity/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreate_Success() throws Exception {
        // 准备测试数据
        YourEntity requestEntity = new YourEntity();
        requestEntity.setName("new entity");

        YourEntity createdEntity = new YourEntity();
        createdEntity.setId(1L);
        createdEntity.setName("new entity");

        // 模拟Service层行为
        when(yourService.create(any(YourEntity.class))).thenReturn(createdEntity);

        // 执行请求并验证
        mockMvc.perform(post("/api/your-entity")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"new entity\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("new entity"));
    }

    @Test
    public void testUpdate_Success() throws Exception {
        // 准备测试数据
        Long id = 1L;
        YourEntity requestEntity = new YourEntity();
        requestEntity.setName("updated entity");

        YourEntity updatedEntity = new YourEntity();
        updatedEntity.setId(id);
        updatedEntity.setName("updated entity");

        // 模拟Service层行为
        when(yourService.update(eq(id), any(YourEntity.class))).thenReturn(updatedEntity);

        // 执行请求并验证
        mockMvc.perform(put("/api/your-entity/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"updated entity\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("updated entity"));
    }

    @Test
    public void testDelete_Success() throws Exception {
        // 准备测试数据
        Long id = 1L;

        // 模拟Service层行为
        when(yourService.delete(id)).thenReturn(true);

        // 执行请求并验证
        mockMvc.perform(delete("/api/your-entity/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDelete_NotFound() throws Exception {
        // 准备测试数据
        Long id = 999L;

        // 模拟Service层行为
        when(yourService.delete(id)).thenReturn(false);

        // 执行请求并验证
        mockMvc.perform(delete("/api/your-entity/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
```