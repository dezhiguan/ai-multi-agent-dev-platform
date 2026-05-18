package com.example.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testCreateOrder() throws Exception {
        OrderCreateRequest request = new OrderCreateRequest(1L, 1L, 2, new BigDecimal("100.00"), "PENDING");
        OrderDTO orderDTO = new OrderDTO(1L, 1L, 1L, 2, new BigDecimal("100.00"), "PENDING", LocalDateTime.now(), LocalDateTime.now());

        when(orderService.createOrder(any(OrderCreateRequest.class))).thenReturn(orderDTO);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.productId").value(1L))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.totalPrice").value(100.00))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    public void testGetAllOrders() throws Exception {
        OrderDTO order1 = new OrderDTO(1L, 1L, 1L, 2, new BigDecimal("100.00"), "PENDING", LocalDateTime.now(), LocalDateTime.now());
        OrderDTO order2 = new OrderDTO(2L, 2L, 2L, 1, new BigDecimal("50.00"), "COMPLETED", LocalDateTime.now(), LocalDateTime.now());
        List<OrderDTO> orders = Arrays.asList(order1, order2);

        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].userId").value(2L));
    }

    @Test
    public void testGetOrderById() throws Exception {
        OrderDTO orderDTO = new OrderDTO(1L, 1L, 1L, 2, new BigDecimal("100.00"), "PENDING", LocalDateTime.now(), LocalDateTime.now());

        when(orderService.getOrderById(eq(1L))).thenReturn(orderDTO);

        mockMvc.perform(get("/api/orders/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.productId").value(1L))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.totalPrice").value(100.00))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}